package com.newland.parser;

/**
 * Created by wot_zhengshenming on 2021/4/25.
 */
public enum RepeatType {
    COLLECTION,MAP,ARRAY;
    public static boolean contains(String name){
        for(RepeatType type : RepeatType.values()){
            if(type.name().equals(name))
                return true;
        }
        return false;
    }
}
