package com.newland;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by wot_zhengshenming on 2021/3/4.
 */
public class MyClassLoader extends URLClassLoader {
    /**
     * 返回对象接口
     */
    private String resModel;

    public MyClassLoader(){
        super(new URL[]{});
    }

    public void addURL(File file){
        try{
            this.addURL(file.toURI().toURL());
        }catch(Exception e){

        }

    }

    public void addURL(URL[] urls){
        try{
            if(urls!=null){
                for(URL url:urls){
                    this.addURL(url);
                }
            }
        }catch(Exception e){

        }

    }
}
