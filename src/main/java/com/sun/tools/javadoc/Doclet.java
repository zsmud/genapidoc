package com.sun.tools.javadoc;

import com.newland.ApiDescribe;
import com.newland.ChineseUtils;
import com.sun.javadoc.*;
import org.apache.maven.plugin.logging.Log;
import org.springframework.core.type.classreading.FieldMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by wot_zhengshenming on 2021/3/12.
 */
public class Doclet {

    private static final ThreadLocal<RootDoc> threadLocal =new ThreadLocal<>();

    public static boolean start(RootDoc root){
        if(root instanceof RootDocImpl){
            RootDocImpl rootDoc = (RootDocImpl)root;
            //不显示javadoc警告信息
            rootDoc.env.setSilent(true);
        }
        threadLocal.set(root);
        return true;
    }

    private static void putAndIncMap(Map<String,Integer> sheetNames,String apiName,ApiDescribe apiDescribe){
        if(sheetNames.get(apiName)!=null) {
            Integer index = sheetNames.get(apiName) + 1;
            apiDescribe.setApiName(apiName+index.toString());
            sheetNames.put(apiName,index);
        } else if(StringUtils.hasText(apiName)){
            apiName = apiName.length()>31?apiName.substring(0,30):apiName;//sheetName最长不能超过31
            apiDescribe.setApiName(apiName);
            sheetNames.put(apiName,new Integer("0"));
        }
    }

    public static void mergeJavaDoc(Log log,List<String> cp, String baseDir , List<ApiDescribe> apis,String dtoBaseDir){
        if(apis == null || apis.size() == 0 ) return;
        //用于判断服务名称重复
        final Map<String,Integer> sheetNames = new ConcurrentHashMap<String,Integer>();
        apis.parallelStream().forEach(apiDescribe -> {
            //controller服务名
            String path =baseDir+ "/src/main/java" + File.separator + apiDescribe.getControllerName().replaceAll("\\.","/")+".java";
            ClassDoc[] classs = genClassDoc(log,cp,path,apiDescribe.getControllerName());
            if(classs!=null && classs.length>0){
                MethodDoc[] methods = classs[0].methods();//考虑一般不会有内部的controller类
                MethodDoc md = Stream.of(methods).filter(methodDoc -> methodDoc.name().equals(apiDescribe.getApiName())).findFirst().orElse(null);
                if(md!=null){
                    Tag[] tags = md.tags();
                    JudgeFunctionAndDescribe judge = new JudgeFunctionAndDescribe();
                    //取方法头上的tag信息封装到apiDescribe中去
                    getTagInfo(tags, apiDescribe, judge, sheetNames);
                    //取默认的方法功能中文名称
                    getDefaultFunctionName(judge, md, apiDescribe, sheetNames);
                    //仍然取不到功能中文名称
                    if(!judge.isHasFunction()){
                        log.warn(classs[0].name()+"|"+md.name()+"没有设置功能名称");
                    }
                    //提取功能简述
                    if(!judge.isHasDescribe() && StringUtils.hasText(md.commentText())){
                        int ind = md.commentText().indexOf("\n");
                        if(ind !=-1 && md.commentText().length()>ind+1) {
                            String describe = md.commentText().substring(ind+1);
                            apiDescribe.setDescribe(describe);
                        }
                    }
                }else{
                    log.warn(classs[0].name()+"|"+apiDescribe.getApiName()+"<javadoc没有找到该方法信息");
                    if(methods!=null && methods.length>0){
                        for(MethodDoc doc:methods){
                            log.warn(classs[0].name()+"|"+doc.name()+">javadoc没有找到该方法信息");
                        }
                    }
                }
            }
            //提取DTO源码路径
            String dtoBaseDirPath = getDTOBaseDirPath(baseDir,dtoBaseDir,log);
            //提取controller类的请求和返回对象的javadoc信息
            getReqAndResJavaDoc(apiDescribe, log, cp, baseDir, dtoBaseDirPath);
        });
    }

    /**
     * 如果没有tag (@function) 就默认取第一行的中文注释
     * @param judge
     * @param md
     * @param apiDescribe
     * @param sheetNames
     */
    private static void getDefaultFunctionName(JudgeFunctionAndDescribe judge,MethodDoc md,ApiDescribe apiDescribe, Map<String,Integer> sheetNames){
        if(!judge.isHasFunction()){
            if(StringUtils.hasText(md.commentText())){
                //没有@function 标签取第一行
                int ind = md.commentText().indexOf("\n");
                String apiName = md.commentText();
                if(ind !=-1) {
                    apiName = md.commentText().substring(0,ind);
                }
                if(ChineseUtils.isChinese(apiName)) {
                    judge.setHasFunction(true);
                }else{
                    apiName = apiDescribe.getApiName();
                }
                putAndIncMap(sheetNames,apiName,apiDescribe);
            }else{
                String apiName = apiDescribe.getApiName();
                putAndIncMap(sheetNames,apiName,apiDescribe);
            }
        }
    }

    /**
     * 取得方法头上的tag标签
     * @param tags
     * @param apiDescribe
     * @param judge
     * @param sheetNames
     */
    private static void getTagInfo(Tag[] tags,ApiDescribe apiDescribe,JudgeFunctionAndDescribe judge,Map<String,Integer> sheetNames){
        if(tags!=null){
            StringBuffer changelog = new StringBuffer();
            StringBuffer author = new StringBuffer();
            for(Tag tag:tags){
                if(tag.name().equals("@author")) {
                    if(author.length()>0)
                        author.append(",").append(tag.text());
                    else
                        author.append(tag.text());
                }
                if(tag.name().equals("@date")) apiDescribe.setDate(tag.text());
                if(tag.name().equals("@since")) apiDescribe.setSince(tag.text());
                if(tag.name().equals("@version")) apiDescribe.setVersion(tag.text());
                if(tag.name().equals("@changelog")) {
                    changelog.append(tag.text()).append("\n");
                }
                if(tag.name().equals("@function") && StringUtils.hasText(tag.text())) {
                    judge.setHasFunction(true);
                    String apiName = tag.text();
                    putAndIncMap(sheetNames,apiName,apiDescribe);
                }
                if(tag.name().equals("@describe") && StringUtils.hasText(tag.text())) {
                    judge.setHasDescribe(true);
                    apiDescribe.setDescribe(tag.text());
                }
            }
            apiDescribe.setChangelog(changelog.toString());
            apiDescribe.setAuthor(author.toString());
        }
    }

    /**
     * 提取controller类的请求和返回对象的javadoc信息
     * @param apiDescribe
     * @param log
     * @param cp
     * @param baseDir
     * @param dtoBaseDirPath
     */
    private static void getReqAndResJavaDoc(ApiDescribe apiDescribe,Log log,List<String> cp,String baseDir,String dtoBaseDirPath){
        //请求对象
        if(apiDescribe.getReqModelSuperClasses()!=null && apiDescribe.getReqModelSuperClasses().size()>0){
            apiDescribe.getReqModelSuperClasses().parallelStream().forEach(superClass->{
                //读取父类的字段
                getJavaDoc(log,cp,baseDir,dtoBaseDirPath, ClassUtils.convertResourcePathToClassName(superClass),apiDescribe.getReqFieldMetadatas());
            });
        }
        //读取本类的字段
        getJavaDoc(log,cp,baseDir,dtoBaseDirPath,apiDescribe.getReqModel(),apiDescribe.getReqFieldMetadatas());
        //组合对象的javadoc提取
        if(apiDescribe.getReqFieldCollectionGenericClass()!=null && apiDescribe.getReqFieldCollectionGenericClass().size()>0){
            apiDescribe.getReqFieldCollectionGenericClass().parallelStream().forEach(genericClass->{
                if(genericClass.indexOf("$")==-1) //内部类已经提取过了，无需再提取
                    getJavaDoc(log,cp,baseDir,dtoBaseDirPath, ClassUtils.convertResourcePathToClassName(genericClass),apiDescribe.getReqFieldMetadatas());
            });
        }
        //返回对象
        if(!com.newland.ClassUtils.isPrimaryType(apiDescribe.getResModel())) {//不是基本类型的返回，才去提取javadoc
            if(apiDescribe.getResModelSuperClasses()!=null && apiDescribe.getResModelSuperClasses().size()>0){
                apiDescribe.getResModelSuperClasses().parallelStream().forEach(superClass->{
                    //读取返回对象的父类的字段
                    getJavaDoc(log,cp,baseDir,dtoBaseDirPath, ClassUtils.convertResourcePathToClassName(superClass),apiDescribe.getResFieldMetadatas());
                });
            }
            //读取返回对象本类的字段
            getJavaDoc(log,cp,baseDir,dtoBaseDirPath,apiDescribe.getResModel(),apiDescribe.getResFieldMetadatas());
            //读取返回对象组合字段对象的泛型javadoc提取
            if(apiDescribe.getResFieldCollectionGenericClass()!=null && apiDescribe.getResFieldCollectionGenericClass().size()>0){
                apiDescribe.getResFieldCollectionGenericClass().parallelStream().forEach(genericClass->{
                    if(genericClass.indexOf("$")==-1) //内部类已经提取过了，无需再提取
                        getJavaDoc(log,cp,baseDir,dtoBaseDirPath, ClassUtils.convertResourcePathToClassName(genericClass),apiDescribe.getResFieldMetadatas());
                });
            }
        }
    }

    /**
     * 搜索dto文件夹路径
     * 1、优先以配置的dto路径为准
     * 2、其次找父目录下的api-tool文件夹
     * 3、最后在工程下的目录
     * @param baseDir
     * @param dtoBaseDir
     * @param log
     * @return
     */
    private static String getDTOBaseDirPath(String baseDir,String dtoBaseDir,Log log){
        File file = new File(baseDir+"/../api-tool");
        File file1 = null;//+"/../api-tool"
        if(StringUtils.hasText(dtoBaseDir)) file1 = new File(dtoBaseDir);
        String dtoBaseDirPath = dtoBaseDir;
        if(file1!=null && file1.exists()) {
            dtoBaseDirPath = dtoBaseDir+"/src/main/java";
        }else if(file.exists()) {
            dtoBaseDirPath = baseDir+"/../api-tool/src/main/java";
        }else{
            log.info(baseDir+"/../api-tool:路径不存在");
            dtoBaseDirPath = baseDir+"/src/main/java";
        }
        return dtoBaseDirPath;
    }

    private static void getJavaDoc(Log log,List<String> classpath,String baseDir,String sourcePath,String sourceName,List<FieldMetadata> fields){
        //请求对象
        String path =sourcePath + File.separator + sourceName.replaceAll("\\.","/").replaceAll("\\[\\]","")+".java";
        String path1 =baseDir +"/src/main/java" + File.separator + sourceName.replaceAll("\\.","/").replaceAll("\\[\\]","")+".java";
        String sources = path;
        File file = new File(path);
        if(!file.exists()) {//如果dto目录不存在源码，则在当前项目中查找
            log.debug("检查输入的路径是否正确！"+path);
            sources = path1;
        }
        //log.info("请求对象路径:"+path);
        ClassDoc[] classes = genClassDoc(log,classpath,sources,sourceName);
        if(classes!=null && classes.length>0) {
            for(ClassDoc doc:classes)  {
                //log.info("生成ClassDoc:"+doc.name());
                FieldDoc[]  fieldDocs = doc.fields();
                fields.stream().map(fieldMetadata -> {
                    FieldDoc fieldDoc = Stream.of(fieldDocs).filter(fieldDoc1 ->{
                        return fieldDoc1.name().equals(fieldMetadata.getFieldName());
                    }).findFirst().orElse(null);
                    if(fieldDoc!=null && fieldDoc.commentText()!=null){
                        int index = fieldDoc.commentText().indexOf(" ");
                        String cnname = fieldDoc.commentText();
                        if(index!=-1) cnname = fieldDoc.commentText().substring(0,index);
                        if(StringUtils.hasText(cnname)) fieldMetadata.setFieldCNName(cnname);
                        if(StringUtils.isEmpty(fieldMetadata.getMemo()))
                            fieldMetadata.setMemo(index!=-1?fieldDoc.commentText().substring(index+1).trim():"");
                    } else if(fieldDoc!=null && fieldDoc.commentText() == null){
                        log.warn(fieldMetadata.getDeclaringClassName()+"|"+fieldDoc.name()+"没有javaDoc注释！");
                    }
                    return fieldMetadata;
                }).collect(Collectors.toList());
            }
        }

    }

    public static ClassDoc[] genClassDoc(Log log,List<String> cp,String sources,String className){
        if(className!=null && (className.startsWith("netbank") || className.startsWith("com"))){
            File file = new File(sources);
            if(!file.exists()) {
                log.error("生成javadoc失败，检查输入的路径是否正确！"+sources);
                return null;
            }
            ArrayList<String> list  = new ArrayList<String>();
            list.add("-doclet");
            list.add(Doclet.class.getName());
            list.add("-encoding");
            list.add("utf-8");
            list.add("-private");
            if(!log.isDebugEnabled()) {
                list.add("-quiet");
            }
            list.add("-classpath");
            String paths = cp.stream().reduce(".",(a,b)->a+File.pathSeparator+b);
            list.add(paths);
            list.add(sources);
            Main.execute(list.toArray(new String[list.size()]));
            return Doclet.threadLocal.get().classes();
        }else{
            return null;
        }

    }
}

/**
 * 判断是否有功能名称和简述
 */
class JudgeFunctionAndDescribe{
    boolean hasFunction ;
    boolean hasDescribe ;

    JudgeFunctionAndDescribe(){
        hasFunction = false;
        hasDescribe = false;
    }

    public boolean isHasFunction() {
        return hasFunction;
    }

    public void setHasFunction(boolean hasFunction) {
        this.hasFunction = hasFunction;
    }

    public boolean isHasDescribe() {
        return hasDescribe;
    }

    public void setHasDescribe(boolean hasDescribe) {
        this.hasDescribe = hasDescribe;
    }
}