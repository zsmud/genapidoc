package com.newland;

import com.newland.parser.ParserContext;
import org.springframework.asm.Type;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.ContainFieldAnnotationMetadataReadingVisitor;
import org.springframework.core.type.classreading.ContainFieldSimpleMetadataReader;
import org.springframework.core.type.classreading.FieldMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.stereotype.Controller;

import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.*;

/**
 * Created by wot_zhengshenming on 2021/3/25.
 */
@Controller
public class TestAsmParser {

    private List<?> list;

    public static void main(String[] args){
//        String sign = "[[Ljava/lang/List;";
//        System.out.println(Type.getType(sign).getSort());
//        String test = "(Lnetbank/firm/api/model/RestfulRequest<Lnetbank/firm/api/dto/core/operator/UpdateOperatorReq;>;)V";
//        System.out.println(ClassUtils.getMethodReturnType(test));

         String str = "Ljava/lang/String;";
        System.out.println(str.substring(11,str.length()-1));
    }



    public static void testParser(){
        //加载项目的类路径和本插件类路径
        MyClassLoader urlClassLoader = new MyClassLoader();
//        urlClassLoader.addURL(file);
        //插件工程自己的类路径
        urlClassLoader.addURL(((URLClassLoader)Thread.currentThread().getContextClassLoader()).getURLs());
        //设置类扫描提供器，只过滤Controller类
        ClassPathScanningCandidateApiDocProvider provider = new ClassPathScanningCandidateApiDocProvider(false);
        TypeFilter filter = new AnnotationTypeFilter(Controller.class);
        provider.addIncludeFilter(filter);
        ResourceLoader resourceLoader =  new PathMatchingResourcePatternResolver(urlClassLoader);
        provider.setResourceLoader(resourceLoader);
        ParserContext context = new ParserContext(provider.getMetadataReaderFactory(),"java.util.Map","Ljava/util/Map<Ljava/util/Map<Lcom/newland/SendBackTransferReq;Ljava/util/List<Lcom/newland/TransferRespItem;>;>;Ljava/util/Map<Ljava/lang/String;Ljava/util/List<Lcom/newland/BatchOperTransferResp;>;>;>;");
        context.getDispatcher().parser(context);
        List<FieldMetadata> fields = context.getFields();
        for(FieldMetadata field :fields){
            System.out.println(field);

        }
        System.out.println("===superClass===");
        for(String field :context.getSuperClasses()){
            System.out.println(field);

        }

        System.out.println("===genericClass===");
        for(String field :context.getGenericClasses()){
            System.out.println(field);

        }
    }

    public static void testMethodSign(){
        try{
            //加载项目的类路径和本插件类路径
            MyClassLoader urlClassLoader = new MyClassLoader();
            //插件工程自己的类路径
            urlClassLoader.addURL(((URLClassLoader)Thread.currentThread().getContextClassLoader()).getURLs());
            //设置类扫描提供器，只过滤Controller类
            ClassPathScanningCandidateApiDocProvider provider = new ClassPathScanningCandidateApiDocProvider(false);
            TypeFilter filter = new AnnotationTypeFilter(Controller.class);
            provider.addIncludeFilter(filter);
            ResourceLoader resourceLoader =  new PathMatchingResourcePatternResolver(urlClassLoader);
            provider.setResourceLoader(resourceLoader);

            ContainFieldSimpleMetadataReader resReader =(ContainFieldSimpleMetadataReader)provider.getMetadataReaderFactory().getMetadataReader("com.newland.TestAsmParser");
            ContainFieldAnnotationMetadataReadingVisitor resVisitor = (ContainFieldAnnotationMetadataReadingVisitor)resReader.getClassMetadata();
//            Set<MethodMetadata> set = resVisitor.getMethodMetadataSet();
//            for(MethodMetadata data:set){
//                ExMethodMetadataReadingVisitor visitor = (ExMethodMetadataReadingVisitor)data;
//                System.out.println(visitor.getSignature());
//            }
            List<FieldMetadata> fieldMetadatas = resVisitor.getFieldMetadataSet();
            for(FieldMetadata field:fieldMetadatas){

                System.out.println(field.getSignature());
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void parserClassSignature(String signature,List<String> types){
        char[] buf = signature.toCharArray();
        int off=0;
        Stack<Character> stack =new Stack<Character>();
        int start=0,end=0,count=0;
        for(int i=0;i<buf.length;i++){
            int j = i==0?i:i-1;
            if(buf[i]=='L'&& (i==0||buf[j]==';'||buf[j]=='<')) {
                if(count==0) start=i;
                count++;
            }else if(buf[i]==';'){
                count--;
                if(count==0){
                    end=i;
                    break;
                }
            }
        }
        if(start!=end){
            String type = new String(buf,start,end-start+1);
            System.out.println(type);
            types.add(type);
            if(buf.length>end+1){
                parserClassSignature(signature.substring(end+1),types);
            }
        }
    }

    public static void  testType(){
        Class cls = TestAsmParser.class;
        try {
            Method method = cls.getMethod("testMethod",Map.class,List.class);
            String methodDes = Type.getMethodDescriptor(method);
            System.out.println(methodDes);
            Type[] types = Type.getArgumentTypes(methodDes);
            for(Type type:types)
                System.out.println(type.getClassName());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    @Deprecated
    public String[] testMethodSign1(String str){
        return null;
    }

}
