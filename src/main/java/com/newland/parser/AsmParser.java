package com.newland.parser;

/**
 * Created by wot_zhengshenming on 2021/3/24.
 */
public interface AsmParser {

    public boolean canHandle(String className);

    public void parser(ParserContext context);
}
