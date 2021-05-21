package com.newland.parser;

import org.springframework.core.type.classreading.FieldMetadataReadingVisitor;
import org.springframework.util.StringUtils;

/**
 * Created by wot_zhengshenming on 2021/3/24.
 */
public abstract class AbstractAsmParser implements AsmParser {
    
    public final static String PRIMARY_TYPE="基本类型";

    public abstract boolean canHandle(String className) ;

    public abstract void parser0(ParserContext context) ;

    @Override
    public void parser(ParserContext context) {

        if(canHandle(context.className)){
            parser0(context);
        }
    }

    protected void addFakeField(String shortName,String classPath,String className,ParserContext context,String memo){
        String objectName = PRIMARY_TYPE.equals(memo)?classPath:"L"+classPath+";";
        FieldMetadataReadingVisitor visitor = new FieldMetadataReadingVisitor(
                shortName, 1, objectName,className,
                null, null, null,null,null);
        if(StringUtils.hasText(memo)) visitor.setMemo(memo);
        context.fields.add(visitor);
    }

    protected void addFakeField(String shortName,String classPath,String className,ParserContext context,String memo,Boolean color){
        String objectName = PRIMARY_TYPE.equals(memo)?classPath:"L"+classPath+";";
        FieldMetadataReadingVisitor visitor = new FieldMetadataReadingVisitor(
                shortName, 1, objectName,className,
                null, null, null,null,null);
        if(StringUtils.hasText(memo)) visitor.setMemo(memo);
        visitor.setColor(color);
        context.fields.add(visitor);
    }

    protected void addFakeField(String shortName,String classPath,String className,ParserContext context,String memo,boolean color,boolean isArray){
        String path ="L"+classPath+";";
        if(isArray)
            path = "[L"+classPath+";";
        else if(PRIMARY_TYPE.equals(memo))
            path = classPath;
        FieldMetadataReadingVisitor visitor = new FieldMetadataReadingVisitor(
                shortName, 1,path ,className,
                null, null, null,null,null);
        if(StringUtils.hasText(memo)) visitor.setMemo(memo);
        visitor.setColor(color);
        context.fields.add(visitor);
    }
}
