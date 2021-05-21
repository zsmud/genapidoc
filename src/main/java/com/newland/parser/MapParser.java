package com.newland.parser;

import com.newland.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * Created by wot_zhengshenming on 2021/3/24.
 */
public class MapParser extends AbstractAsmParser {
    @Override
    public boolean canHandle(String className) {
        if(ClassUtils.isMapType(className)) return true;
        return false;
    }

    @Override
    public void parser0(ParserContext context) {
        String[] keyValue = ClassUtils.getMapKeyValueFromSignature(context.classSignature);
        String oldClassName = context.className;
        String oldClassSignature = context.classSignature;
        String classPath = ClassUtils.convertClassNameToResourcePath(context.className);
        String shortName = ClassUtils.getShortNameFromClassName(context.className);
        String fieldName = StringUtils.isEmpty(context.prefixName)|| RepeatType.contains(context.prefixName)?shortName:context.prefixName;
        //对key做处理
        if(keyValue!=null && !ClassUtils.OBJECT.equals(keyValue[0])){
            addFakeField(fieldName,classPath,classPath,context,"循环开始",Boolean.TRUE);
            context.setPrefixName(RepeatType.MAP.name());
            //对key做处理
            String keyClass = ClassUtils.objectResourcePathConvertToClassName(ClassUtils.getObjectTypeNoIncludingGeneric(keyValue[0]));
            addFakeField(ClassUtils.getShortNameFromClassName(keyClass),ClassUtils.convertClassNameToResourcePath(keyClass),keyClass,context,"MAP-KEY开始",Boolean.TRUE);
            context.className = keyClass;
            context.classSignature = keyValue[0];
            context.dispatcher.parser(context);//递归处理
            addFakeField(ClassUtils.getShortNameFromClassName(keyClass),ClassUtils.convertClassNameToResourcePath(keyClass),keyClass,context,"MAP-KEY结束",Boolean.TRUE);
            //对value做处理
            String valueClass = ClassUtils.objectResourcePathConvertToClassName(ClassUtils.getObjectTypeNoIncludingGeneric(keyValue[1]));
            addFakeField(ClassUtils.getShortNameFromClassName(valueClass),ClassUtils.convertClassNameToResourcePath(valueClass),valueClass,context,"MAP-VALUE开始",Boolean.TRUE);
            context.className = valueClass;
            context.classSignature = keyValue[1];
            context.dispatcher.parser(context);//递归处理
            addFakeField(ClassUtils.getShortNameFromClassName(valueClass),ClassUtils.convertClassNameToResourcePath(valueClass),valueClass,context,"MAP-VALUE结束",Boolean.TRUE);

            context.className = oldClassName;
            context.classSignature = oldClassSignature;
            addFakeField(fieldName,classPath,classPath,context,"循环结束",Boolean.TRUE);
        }else {
            addFakeField(fieldName,classPath,classPath,context,"",Boolean.TRUE);
        }
    }
}
