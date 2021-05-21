package com.newland;

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wot_zhengshenming on 2021/3/9.
 */
public class TestTemplate {
    public static void  main(String[] args) throws  Exception {

        testFileInput();

    }

    public static void testTemplate()throws Exception{
        //模板文件
        Configuration cfg = new Configuration(Configuration.getVersion());
        cfg.setDefaultEncoding("utf-8");
        cfg.setClassForTemplateLoading(cfg.getClass(),"/templates");
        Template template = cfg.getTemplate("test.ftl");

        //输出文件
        Writer writer = new OutputStreamWriter(new FileOutputStream(new File("d:/excel.xls")),"utf-8");
        //数据
        List<ApiDescribe> list = new ArrayList<ApiDescribe>();
        ApiDescribe api = new ApiDescribe("查询指令","apiurl","com.newland.req","com.newland.res",null,null, "",null,null,null,null);
        list.add(api);

        api = new ApiDescribe("MAP-KEY","apiurl","com.newland.req","com.newland.res",null,null, "",null,null,null,null);
        list.add(api);
        api = new ApiDescribe(null,"apiurl","com.newland.req","com.newland.res",null,null, "",null,null,null,null);
        list.add(api);
        Map datas = new HashMap<String,List>();
        datas.put("apiDescribes",list);
        //输出文件
        template.process(datas,writer);

    }

    public static void testFileInput() throws Exception{
        InputStream fis = TestTemplate.class.getClassLoader().getResourceAsStream("templates/template-excel.ftl");
        System.out.println("shuchu:"+fis.available());
        //insert into t_firm from db.t_firm
        //put oncid,nncid into map
        //for(onidKey : map)
        //backup
        //update t_firm where ncid=oncid from edip.dat

    }
}
