package com.newland.parser;

import com.newland.ClassUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.classreading.ContainFieldAnnotationMetadataReadingVisitor;
import org.springframework.core.type.classreading.ContainFieldSimpleMetadataReader;
import org.springframework.core.type.classreading.FieldMetadata;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wot_zhengshenming on 2021/3/24.
 * 业务对象解析，一般是dto的请求和返回对象的解析
 */
public class BusinessObjectParser extends AbstractAsmParser {

    public final static String FILE_ENTITY = "com.cib.cap4i.file.text.annotation.FileEntity";

    @Override
    public boolean canHandle(String className) {
        if(!className.endsWith("[]")&&(className.startsWith("netbank")||className.startsWith("com")))
            return true;
        return false;
    }

    @Override
    public void parser0(ParserContext context) {
        try{
            ContainFieldSimpleMetadataReader resReader =(ContainFieldSimpleMetadataReader)context.factory.getMetadataReader(context.className);
            ContainFieldAnnotationMetadataReadingVisitor resVisitor = (ContainFieldAnnotationMetadataReadingVisitor)resReader.getClassMetadata();
            context.superClasses.addAll(resVisitor.getSuperClasses());
            context.genericClasses.addAll(resVisitor.getFieldCollectionGenericClass());
            List<FieldMetadata> set = resVisitor.getFieldMetadataSet();
            String classPath = ClassUtils.convertClassNameToResourcePath(context.className);
            if(StringUtils.hasText(context.prefixName) && !RepeatType.contains(context.prefixName)){
                addFakeField(context.prefixName,classPath,context.className,context,"复合对象开始",true,false);
            }
            //如果是文件处理类
            if(resVisitor.hasAnnotation(FILE_ENTITY)){
                AnnotationAttributes aas = resVisitor.getAnnotationAttributes(FILE_ENTITY);
                String[] fields = aas.getStringArray("fields");
                if(fields!=null && set!=null ){
                    StringBuffer memo = new StringBuffer("文件开始");
                    if(!StringUtils.isEmpty(context.getMemo())) memo.append("("+context.memo+")");
                    addFakeField("File","java/io/File"+"<"+ ClassUtils.getShortNameFromClassName(context.className)+">","java/io/File",context,memo.toString(),Boolean.TRUE);
                    Arrays.stream(fields).forEach(field->{
                        set.stream().forEach(fieldMetadata -> {
                            if(field.equals(fieldMetadata.getFieldName())){
                                context.fields.add(fieldMetadata);
                            }
                        });
                    });
                    memo = new StringBuffer("文件结束");
                    if(!StringUtils.isEmpty(context.getMemo())) memo.append("("+context.memo+")");
                    addFakeField("File","java/io/File"+"<"+ClassUtils.getShortNameFromClassName(context.className)+">","java/io/File",context,memo.toString(),Boolean.TRUE);
                }
            }else{
                context.fields.addAll(set);
            }
            if(StringUtils.hasText(context.prefixName) && !RepeatType.contains(context.prefixName)){
                addFakeField(context.prefixName,classPath,context.className,context,"复合对象结束",true,false);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
