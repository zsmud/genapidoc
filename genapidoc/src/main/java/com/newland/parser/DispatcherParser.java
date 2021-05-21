package com.newland.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wot_zhengshenming on 2021/3/24.
 */
public class DispatcherParser implements AsmParser {

    private List<AsmParser> list = new ArrayList<AsmParser>();
    public DispatcherParser() {
        list.add(new PrimaryParser());
        list.add(new BusinessObjectParser());
        list.add(new CollectionParser());
        list.add(new MapParser());
        list.add(new ArrayParser());
    }

    @Override
    public boolean canHandle(String className) {
        return false;
    }

    @Override
    public void parser(ParserContext context) {
        for (AsmParser parser:list){
            parser.parser(context);
        }
    }

    public void addParser(AsmParser parser){
        this.list.add(parser);
    }
}
