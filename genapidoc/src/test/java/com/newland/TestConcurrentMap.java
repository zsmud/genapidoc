package com.newland;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wot_zhengshenming on 2021/4/19.
 */
public class TestConcurrentMap {
    public static void main(String[] args){
        final Map<String,Integer> sheetNames = new ConcurrentHashMap<String,Integer>();
        List<String> apiNames = new ArrayList<String>();
        for(int i=0;i<20;i++){
            Random rand = new Random(i);
            String name = "name"+rand.nextInt(10);
            apiNames.add(name);
            System.out.println("gen:"+name);
        }
        apiNames.parallelStream().forEach(apiName -> {
            if(sheetNames.get(apiName)!=null) {
                Integer index = sheetNames.get(apiName) + 1;
                System.out.println("repeat:"+apiName+index.toString());
                sheetNames.put(apiName,index);
            } else if(StringUtils.hasText(apiName)){
                apiName = apiName.length()>31?apiName.substring(0,30):apiName;//sheetName最长不能超过31
                System.out.println("first put int map:"+apiName);
                sheetNames.put(apiName,new Integer("0"));
            }
        });
    }
}
