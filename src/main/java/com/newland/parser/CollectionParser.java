package com.newland.parser;

import com.newland.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * Created by wot_zhengshenming on 2021/3/24.
 * 集合类解析，包括Set/List/Vector
 */
public class CollectionParser extends AbstractAsmParser {
    @Override
    public boolean canHandle(String className) {
        if(ClassUtils.isCollectionType(className)) return true;
        return false;
    }

    @Override
    public void parser0(ParserContext context) {
        String genericClass = ClassUtils.getCollectionFieldGenericType(context.classSignature);
        String classPath = ClassUtils.convertClassNameToResourcePath(context.className);
        String oldClassName = context.className;
        String oldClassSignature = context.classSignature;
        String shortName = ClassUtils.getShortNameFromClassName(context.className);
        String genericShortName = ClassUtils.getShortNameFromClassPath(genericClass);
        String fieldName = StringUtils.isEmpty(context.prefixName)|| RepeatType.contains(context.prefixName)?shortName:context.prefixName;
        if(!ClassUtils.OBJECT.equals(genericClass)){
            //如果是泛型类需要取出他的子属性
            if(!ClassUtils.isPrimaryType(genericClass)){
                addFakeField(fieldName,classPath+"<"+genericShortName+">",classPath,context,"循环开始",Boolean.TRUE);
                //开始解析泛型类
                context.className = ClassUtils.objectResourcePathConvertToClassName(ClassUtils.getObjectTypeNoIncludingGeneric(genericClass));
                context.genericClasses.add(context.className);
                context.setPrefixName(RepeatType.COLLECTION.name());
                context.classSignature = genericClass;
                context.dispatcher.parser(context);//递归解析
                addFakeField(fieldName,classPath+"<"+genericShortName+">",classPath,context,"循环结束",Boolean.TRUE);
                context.className = oldClassName;
                context.classSignature = oldClassSignature;
            }else{//集合元素已经不可在细分，如String ,Date, BigDecimal之类的
                genericClass = ClassUtils.getShortNameFromClassName(ClassUtils.objectResourcePathConvertToClassName(genericClass));
                addFakeField(fieldName,classPath+"<"+genericClass+">",context.className,context,"",Boolean.TRUE);
            }
        }else{//集合泛型类未指定具体对象的情况
                addFakeField(fieldName,classPath+"<Object>",classPath,context,"",Boolean.TRUE);
        }
    }
}
