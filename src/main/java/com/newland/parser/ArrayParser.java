package com.newland.parser;

import com.newland.ClassUtils;
import org.springframework.asm.Type;
import org.springframework.util.StringUtils;

/**
 * Created by wot_zhengshenming on 2021/3/26.
 * 数据类型处理
 */
public class ArrayParser extends AbstractAsmParser  {
    @Override
    public boolean canHandle(String classSignature) {
        if(Type.ARRAY==Type.getType(classSignature).getSort()) return true;
        return false;
    }

    @Override
    public void parser0(ParserContext context) {
        String arrayElement = Type.getType(context.classSignature).getElementType().getClassName();
        String classPath = ClassUtils.convertClassNameToResourcePath(context.className);
        String shortName = ClassUtils.getShortNameFromClassName(arrayElement);
        String fieldName = StringUtils.isEmpty(context.prefixName)|| RepeatType.contains(context.prefixName)?shortName:context.prefixName;
        String arrayElementPath = ClassUtils.convertClassNameToResourcePath(arrayElement);
        if(ClassUtils.isPrimaryType(arrayElement)){
            addFakeField(fieldName,classPath+"<"+shortName+">",arrayElement,context,"",true,true);
        }else{
            addFakeField(fieldName,arrayElementPath,arrayElement,context,"循环开始",true,true);
            //开始解析泛型类
            context.className = arrayElement;
            context.genericClasses.add(arrayElement);
            context.classSignature = "L"+arrayElement+";";
            context.setPrefixName(RepeatType.ARRAY.name());
            context.dispatcher.parser(context);//递归解析
            addFakeField(fieldName,arrayElementPath,arrayElement,context,"循环结束",true,true);
        }
    }

    @Override
    public void parser(ParserContext context) {

        if(canHandle(context.classSignature)){
            parser0(context);
        }
    }
}
