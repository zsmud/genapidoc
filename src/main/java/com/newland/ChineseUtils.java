package com.newland;


import org.springframework.util.StringUtils;

/**
 * Created by wot_zhengshenming on 2021/4/2.
 */
public class ChineseUtils {

    public static boolean isChinese(String str){
        if(StringUtils.isEmpty(str)) return false;
        for(char c : str.toCharArray()){
            if((c >= 0x4e00 && c<= 0x9fa5)|| isChinese(c)){
                return true;
            }
        }
        return false;
    }

    public static boolean isChinese(char c){
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if(ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                ||ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                ||ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                ||ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                ||ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                ||ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS){
            return true;
        }
        return false;
    }

    public static void main(String[] args){
        String str="ad[]fa;";
        System.out.println(isChinese(str));
    }
}
