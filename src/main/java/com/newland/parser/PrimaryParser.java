package com.newland.parser;

import com.newland.ClassUtils;

/**
 * Created by wot_zhengshenming on 2021/3/24.
 * 基本类型解析，该基本类型不单指Java的基本类型，而是指jdk常用类，比如String/Date/BigDecimal这些适合网银的基本类型
 */
public class PrimaryParser extends AbstractAsmParser {
    @Override
    public boolean canHandle(String className) {
        if(ClassUtils.isPrimaryType(className)) return true;
        return false;
    }

    @Override
    public void parser0(ParserContext context) {
        String classPath = ClassUtils.convertClassNameToResourcePath(context.className);
        String shortName = ClassUtils.getShortNameFromClassName(context.className);
        addFakeField(shortName,classPath,context.className,context,PRIMARY_TYPE);
    }
}
