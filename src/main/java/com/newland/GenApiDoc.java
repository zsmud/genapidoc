package com.newland;

/**
 * @author zhengshenming
 * @date 2021-03-01
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.newland.parser.ParserContext;
import com.sun.tools.javadoc.Doclet;
import freemarker.template.Configuration;
import freemarker.template.Template;
import netbank.firm.api.annotation.FileProperties;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.ContainFieldAnnotationMetadataReadingVisitor;
import org.springframework.core.type.classreading.ExMethodMetadataReadingVisitor;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.URLClassLoader;
import java.util.*;

/**
 * 生成API文档插件
 *
 * @author wot_zhengshenming
 * @goal gendoc
 * @phase test
// * @configurator include-project-dependencies
 * @requiresProject true
 * @requiresDependencyResolution compile
 */
public class GenApiDoc extends AbstractMojo
{
    public final static String REQUEST_MAPPING = "org.springframework.web.bind.annotation.RequestMapping";

    public final static String FILE_ENTITY_FORMAT = "netbank.firm.api.annotation.FileEntityFormat";

    public final static String REQUEST_GENERIC_CLASS = "netbank.firm.api.model.RestfulRequest";
    /**
     * 扫描包路径
     * @parameter expression="${scanPackagePath}"
     * @required
     */
    private String scanPackagePath;
    /**
     * 模板路径
     * @parameter expression="${buildClassPath}" default-value="${project.build.outputDirectory}"
     * @required false
     */
    private String buildClassPath;

    /**
     * 文档输出路径
     * @parameter expression="${outputPath}"
     * @required false
     */
    private String outputPath;

    /**
     * 输出文件类型
     *  @parameter expression="${fileType}" default-value="EXCEL"
     */
    private FileType fileType;

    /**
     * 工程文件名
     *  @parameter expression="${projectName}" default-value="${project.artifactId}"
     */
    private String projectName;

    /**
     * 工程文件夹主目录
     *  @parameter expression="${projectBaseDir}" default-value="${basedir}"
     */
    private String projectBaseDir;

    /**
     * dto工程文件夹主目录
     *  @parameter expression="${dtoBaseDir}" default-value=""
     */
    private String dtoBaseDir;

    /**
     * api上下文路径，如/firm/cashapi
     *  @parameter expression="${apiContextPath}" default-value=""
     */
    private String apiContextPath;

    /**
     * 文档版本历史文件信息
     *  @parameter expression="${historyFile}" default-value="history.txt"
     */
    private String historyFile;

    /**
     * 依赖的工程类路径
     *  @parameter expression="${compilePath}" default-value="${project.compileClasspathElements}"
     */
    private List<String> compilePath;

    public void execute() throws MojoExecutionException
    {
        this.getLog().info("开始生成...");
        if(!StringUtils.hasText(scanPackagePath)){
            this.getLog().error("扫描包路径为空，请设置包扫描路径");
            return;
        }
        if(!StringUtils.hasText(buildClassPath)){
            this.getLog().error("项目编译后类路径为空，请设置类路径");
            return;
        }
        File file = new File(buildClassPath);
        if(!file.exists()){
            this.getLog().error("配置的类路径不存在，请设置正确的类路径");
            return;
        }
        if(!StringUtils.hasText(outputPath)){
            this.getLog().error("输出文件路径为空，请设置正确的文件路径");
            return;
        }
        //加载项目的类路径和本插件类路径
        MyClassLoader urlClassLoader = new MyClassLoader();
        if(compilePath!=null){
            for(String path:compilePath){
                if(this.getLog().isDebugEnabled()) {
                    this.getLog().debug("加载编译路径："+path);
                }
                urlClassLoader.addURL(new File(path));
            }
        }
        urlClassLoader.addURL(file);
        //插件工程自己的类路径
        urlClassLoader.addURL(((URLClassLoader)Thread.currentThread().getContextClassLoader()).getURLs());
        //设置类扫描提供器，只过滤Controller类
        ClassPathScanningCandidateApiDocProvider provider = new ClassPathScanningCandidateApiDocProvider(false);
        TypeFilter filter = new AnnotationTypeFilter(Controller.class);
        provider.addIncludeFilter(filter);
        ResourceLoader resourceLoader =  new PathMatchingResourcePatternResolver(urlClassLoader);
        provider.setResourceLoader(resourceLoader);
        //扫描出controller类
        this.getLog().info("扫描"+scanPackagePath+"...中");
        Set<BeanDefinition> controllers = provider.scanMetaData(this.getLog(),scanPackagePath);
        //根据方法输入输出扫描模型包
        if(controllers !=null) {
            List<ApiDescribe> apiDescribes = genApiDescribe(this.getLog(),controllers,(ResourcePatternResolver)resourceLoader,provider.getMetadataReaderFactory(),apiContextPath);
            //提取javadoc信息
            mergeCommentInfo(this.getLog(),apiDescribes,projectBaseDir,compilePath,dtoBaseDir);
            //提取文档修订记录
            List<DocVersion> docVersions = getVersionChangeLogFromFile(this.getLog(),projectBaseDir,historyFile);
            //输出文件
            output(this.getLog(),apiDescribes,docVersions,projectName,outputPath,fileType);
        }else{
            this.getLog().info("扫描"+scanPackagePath+"为空");
        }
    }

    /**
     * @param log
     * @param controllers
     * @param resourceLoader
     * @param factory
     * @return
     */
    public static List<ApiDescribe> genApiDescribe(Log log, Set<BeanDefinition> controllers,ResourcePatternResolver resourceLoader,
                                                   MetadataReaderFactory factory,String apiContextPath){
        List<ApiDescribe> list  = new ArrayList<ApiDescribe>();
        //遍历controller 类
        for(Iterator<BeanDefinition> it = controllers.iterator(); it.hasNext();){
            ScannedGenericBeanDefinition sbd = (ScannedGenericBeanDefinition)it.next();
            if(log.isDebugEnabled()) {
                log.info("controller名字："+sbd.getBeanClassName());
            }
            AnnotationMetadata amt = sbd.getMetadata();
            //类头上的RequestMapping
            Map<String, Object> requestmapping = amt.getAnnotationAttributes(REQUEST_MAPPING);
            String baseUrl =StringUtils.hasText(apiContextPath)?apiContextPath:"";
            if(requestmapping!=null)
                baseUrl += requestmapping.get("value")==null?"":((String[])requestmapping.get("value"))[0];
            //方法头上的RequestMapping
            if(!amt.hasAnnotatedMethods(REQUEST_MAPPING))
                continue;
            //根据输入参数获取输入接口
            if(amt instanceof ContainFieldAnnotationMetadataReadingVisitor) {
                Set<MethodMetadata> methods = ((ContainFieldAnnotationMetadataReadingVisitor) amt).getMethodMetadataSet();
                if(methods !=null){
                    //遍历方法头上的RequestMapping
                    for(MethodMetadata methodMetaData:methods){
                        if(!methodMetaData.isAnnotated(REQUEST_MAPPING)) continue;
                        Map<String, Object> mrequestmapping =methodMetaData.getAnnotationAttributes(REQUEST_MAPPING);
                        //取得方法请求的url地址
                        String methodUrl = mrequestmapping.get("value")==null || ((String[])mrequestmapping.get("value")).length==0 ?"":((String[])mrequestmapping.get("value"))[0];
                        if(!"".equals(methodUrl) && methodUrl.charAt(0)!='/') methodUrl = "/"+methodUrl;
                        //取得请求和返回模型对象
                        if(methodMetaData instanceof ExMethodMetadataReadingVisitor){
                            ExMethodMetadataReadingVisitor exMethodMetadataReadingVisitor = (ExMethodMetadataReadingVisitor) methodMetaData;
                            String resModel = exMethodMetadataReadingVisitor.getReturnTypeName();
                            String reqModel =  ExMethodMetadataReadingVisitor.getMethodArgumentGenericTypeName(REQUEST_GENERIC_CLASS,exMethodMetadataReadingVisitor.getSignature());
                            if(!StringUtils.hasText(reqModel)) continue;
                            resModel = resModel!=null?resModel.replaceAll("\\.","/"):resModel;
                            //组装ApiDescript
                            //解析请求参数
                            ParserContext reqContext = new ParserContext(factory,ClassUtils.convertResourcePathToClassName(reqModel),ClassUtils.getMethodArgumentType(exMethodMetadataReadingVisitor.getSignature()));
                            reqContext.getDispatcher().parser(reqContext);
                            //解析返回参数
                            ParserContext resContext = new ParserContext(factory,ClassUtils.convertResourcePathToClassName(resModel),ClassUtils.getMethodReturnType(exMethodMetadataReadingVisitor.getSignature()));
                            resContext.getDispatcher().parser(resContext);
                           //组装api
                            ApiDescribe apiDescribe = new ApiDescribe(methodMetaData.getMethodName(),baseUrl+methodUrl,
                                    reqModel,resModel,reqContext.getFields(),resContext.getFields(), exMethodMetadataReadingVisitor.getDeclaringClassName(),reqContext.getSuperClasses(),
                                    resContext.getSuperClasses(),reqContext.getGenericClasses(),resContext.getGenericClasses());
                            //判断是否有文件属性，如果有就解析文件类
                            dealWithFileEntity(exMethodMetadataReadingVisitor,apiDescribe,factory);
                            //组装回列表
                            list.add(apiDescribe);
                        }
                    }
                }
            }
        }
        return list;
    }

    /**
     * 处理接口中含有文件配置信息的
     * @param exMethodMetadataReadingVisitor
     * @param apiDescribe
     * @param factory
     */
    private static void dealWithFileEntity(ExMethodMetadataReadingVisitor exMethodMetadataReadingVisitor,ApiDescribe apiDescribe,MetadataReaderFactory factory){
        AnnotationAttributes fileEntity =exMethodMetadataReadingVisitor.getAnnotationAttributes(FILE_ENTITY_FORMAT);
        if(fileEntity!=null){
            AnnotationAttributes[] files = fileEntity.getAnnotationArray("files");
            if(files!=null){
                for(AnnotationAttributes file:files){
                    Object direct = file.get("direct");
                    String fileName = file.getString("name");
                    Class<?> cls = (Class<?>) file.get("cls");
                    if(direct.toString().equals(FileProperties.Direct.REEUEST.toString())){
                        String reqFileClassName = !Object.class.getName().equals(cls.getName())?cls.getName():"";
                        if(!StringUtils.isEmpty(reqFileClassName)){
                            ParserContext reqFileContext = new ParserContext(factory,reqFileClassName,cls.getTypeName());
                            reqFileContext.setMemo("对应输入接口字段："+fileName);
                            reqFileContext.getDispatcher().parser(reqFileContext);
                            apiDescribe.getReqFileMetadatas().addAll(reqFileContext.getFields());
                        }
                    }else{
                        String resFileClassName = !Object.class.getName().equals(cls.getName())?cls.getName():"";
                        if(!StringUtils.isEmpty(resFileClassName)){
                            ParserContext resFileContext = new ParserContext(factory,resFileClassName,cls.getTypeName());
                            resFileContext.setMemo("对应输出接口字段："+fileName);
                            resFileContext.getDispatcher().parser(resFileContext);
                            apiDescribe.getResFileMetadatas().addAll(resFileContext.getFields());
                        }
                    }
                }
            }
        }
    }

    /**
     * 利用javadoc工具将类中的注释设置到字段的名字中,
     * 由于注释信息只存在于源码当中，所以利用javadoc将中文信息提取出来
     * @param list
     */
    public static void mergeCommentInfo(Log log,List<ApiDescribe> list,String projectBaseDir,List<String> cp,String dtoBaseDir){
        log.info("提取javadoc中文注释...");
        Doclet.mergeJavaDoc(log,cp,projectBaseDir,list,dtoBaseDir);
    }

    /**
     * 输出文件
     * @param log
     * @param list
     * @param outputPath
     */
    public static void output(Log log, List<ApiDescribe> list,List<DocVersion> docVersions,String projectName,String outputPath,FileType fileType) {
        try{
            //数据
            Map data = new HashMap<String,List>();
            data.put("apiDescribes",list);
            data.put("docVersions",docVersions);
            //模板文件
            Configuration cfg = new Configuration(Configuration.getVersion());
            cfg.setDefaultEncoding("utf-8");
            cfg.setClassForTemplateLoading(cfg.getClass(),"/templates");
            Template template = null;
            File outfile = null;
            switch (fileType){
                case EXCEL:
                    template = cfg.getTemplate("template-excel.ftl");
                    outfile = new File(outputPath+File.separator+projectName+".xls");
                    break;
                case WORD:
                    template = cfg.getTemplate("template-word.ftl");
                    outfile = new File(outputPath+File.separator+projectName+".doc");
                    break;
                case HTML:
                    template = cfg.getTemplate("template-html.ftl");
                    outfile = new File(outputPath+File.separator+projectName+".html");
                    break;
                case JSON:
                    template = cfg.getTemplate("template-json.ftl");
                    Project project = RAPApiDescribeWarpper.api2project(new Project(new Integer(1),projectName),list,docVersions);
                    data.put("RAPJsonString",JSON.toJSONString(project, SerializerFeature.PrettyFormat,SerializerFeature.WriteNullStringAsEmpty));
                    outfile = new File(outputPath+File.separator+projectName+".json");
                    break;
                default:
                    template = cfg.getTemplate("template-excel.ftl");
                    outfile = new File(outputPath+File.separator+projectName+".xls");
            }
            //输出文件
            Writer writer = new OutputStreamWriter(new FileOutputStream(outfile),"utf-8");
            template.process(data,writer);
        }catch(Exception e){
            log.info("输出文件失败："+e.getMessage(),e);
        }

    }

    public static List<DocVersion> getVersionChangeLogFromFile(Log log,String projectBaseDir,String historyFileName){
        String changelogName = projectBaseDir+"/src/main/resources/"+historyFileName;
        log.info("检查是否存在历史版本文件："+changelogName);
        File changelog = new File(changelogName);
        if(changelog.exists()){
            try {
                FileInputStream fis = new FileInputStream(changelog);
                InputStreamReader reader = new InputStreamReader(fis);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String tmp="";
                List<DocVersion> list = null;
                while(StringUtils.hasText(tmp=bufferedReader.readLine())){
                    String[] docs = tmp.split(" ");
                    if(docs!=null && docs.length>2){
                        if(list == null) list = new ArrayList<DocVersion>();
                        DocVersion dv = new DocVersion();
                        dv.setVersionDate(docs[0]);
                        dv.setVersionNum(docs[1]);
                        dv.setChangelog(docs[2]);
                        list.add(dv);
                    }
                }
                return list;
            }catch(Exception e){
                log.warn("生成版本历史文件出错："+e.getMessage());
                return null;
            }
        }else{
            log.warn("版本历史文件不存在");
        }
        return null;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public void setScanPackagePath(String scanPackagePath) {
        this.scanPackagePath = scanPackagePath;
    }

    public void setBuildClassPath(String buildClassPath) {
        this.buildClassPath = buildClassPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setProjectBaseDir(String projectBaseDir) {
        this.projectBaseDir = projectBaseDir;
    }

    public void setDtoBaseDir(String dtoBaseDir) {
        this.dtoBaseDir = dtoBaseDir;
    }

    public void setApiContextPath(String apiContextPath) {
        this.apiContextPath = apiContextPath;
    }

    public void setCompilePath(List<String> compilePath) {
        this.compilePath = compilePath;
    }

    public void setHistoryFile(String historyFile) {
        this.historyFile = historyFile;
    }

    public static void main(String[] args) throws Exception{
        GenApiDoc gen = new GenApiDoc();
        gen.setScanPackagePath("f:/genapidoc/target");
        gen.execute();
    }
}