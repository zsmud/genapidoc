package com.newland;

import org.springframework.core.type.classreading.FieldMetadata;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wot_zhengshenming on 2021/2/20.
 * Api接口描述类
 */
public class ApiDescribe {

    /**
     * 作者 @version
     */
    private String author;

    /**
     * 版本
     */
    private String version;

    /**
     * 日期 @date
     */
    private String date;

    /**
     * 初始版本 @since
     */
    private String since;

    /**
     * 初始版本 @changelog
     */
    private String changelog;

    /**
     * 接口名字 @function
     */
    private String apiName;

    /**
     * 描述 @describe
     */
    private String describe;
    /**
     * controller名称
     */
    private String controllerName;
    /**
     * 接口地址
     */
    private String openUrl;
    /**
     * 请求对象模型，以 . 分隔的className
     */
    private String reqModel;
    /**
     * 请求对象的父类列表
     */
    private List<String> reqModelSuperClasses;
    /**
     * 返回对象的父类列表
     */
    private List<String> resModelSuperClasses;
    /**
     * 返回对象模型，以 . 分隔的className
     */
    private String resModel;
    /**
     * 请求对象的字段列表
     */
    private List<FieldMetadata> reqFieldMetadatas;
    /**
     * 返回对象的字段列表
     */
    private List<FieldMetadata> resFieldMetadatas;
    /**
     * 请求对象中组合字段对象的子对象类名列表，为了提取类似List<TransferItem>中TransferItem的javaDoc信息
     */
    private List<String> reqFieldCollectionGenericClass;

    /**
     * 返回对象中组合字段对象的子对象类名列表
     */

    private List<String> resFieldCollectionGenericClass;

    /**
     * 请求对象中的文件对象描述
     */
    private List<FieldMetadata> reqFileMetadatas;

    /**
     * 返回对象中的文件对象描述
     */
    private List<FieldMetadata> resFileMetadatas;


    public ApiDescribe(String apiName, String openUrl, String reqModel, String resModel, List<FieldMetadata> reqFieldMetadatas,
                       List<FieldMetadata> resFieldMetadatas, String controllerName,List<String> reqModelSuperClasses,List<String> resModelSuperClasses,
                               List<String> reqFieldCollectionGenericClass,List<String> resFieldCollectionGenericClass) {
        this.apiName = apiName;
        this.openUrl = openUrl;
        this.reqModel = reqModel;
        this.resModel = resModel;
        this.reqFieldMetadatas = reqFieldMetadatas;
        this.resFieldMetadatas = resFieldMetadatas;
        this.controllerName = controllerName;
        this.reqModelSuperClasses = reqModelSuperClasses;
        this.resModelSuperClasses = resModelSuperClasses;
        this.reqFieldCollectionGenericClass = reqFieldCollectionGenericClass;
        this.resFieldCollectionGenericClass = resFieldCollectionGenericClass;
        this.reqFileMetadatas = new ArrayList<FieldMetadata>();
        this.resFileMetadatas = new ArrayList<FieldMetadata>();
    }

    public String getOpenUrl() {
        return openUrl;
    }

    public String getReqModel() {
        return reqModel;
    }

    public String getApiName() {
        return apiName;
    }

    public String getResModel() {
        return resModel;
    }

    public List<FieldMetadata> getReqFieldMetadatas() {
        return reqFieldMetadatas;
    }

    public List<FieldMetadata> getResFieldMetadatas() {
        return resFieldMetadatas;
    }

    public List<String> getReqModelSuperClasses() {
        return reqModelSuperClasses;
    }

    public void setReqModelSuperClasses(List<String> reqModelSuperClasses) {
        this.reqModelSuperClasses = reqModelSuperClasses;
    }

    public List<String> getResModelSuperClasses() {
        return resModelSuperClasses;
    }

    public void setResModelSuperClasses(List<String> resModelSuperClasses) {
        this.resModelSuperClasses = resModelSuperClasses;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getControllerName() {
        return controllerName;
    }

    public void setControllerName(String controllerName) {
        this.controllerName = controllerName;
    }

    public List<String> getResFieldCollectionGenericClass() {
        return resFieldCollectionGenericClass;
    }

    public void setResFieldCollectionGenericClass(List<String> resFieldCollectionGenericClass) {
        this.resFieldCollectionGenericClass = resFieldCollectionGenericClass;
    }

    public List<String> getReqFieldCollectionGenericClass() {
        return reqFieldCollectionGenericClass;
    }

    public void setReqFieldCollectionGenericClass(List<String> reqFieldCollectionGenericClass) {
        this.reqFieldCollectionGenericClass = reqFieldCollectionGenericClass;
    }

    public String getSince() {
        return since;
    }

    public void setSince(String since) {
        this.since = since;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getChangelog() {
        return changelog;
    }

    public void setChangelog(String changelog) {
        this.changelog = changelog;
    }

    public List<FieldMetadata> getReqFileMetadatas() {
        return reqFileMetadatas;
    }

    public void setReqFileMetadatas(List<FieldMetadata> reqFileMetadatas) {
        this.reqFileMetadatas = reqFileMetadatas;
    }

    public List<FieldMetadata> getResFileMetadatas() {
        return resFileMetadatas;
    }

    public void setResFileMetadatas(List<FieldMetadata> resFileMetadatas) {
        this.resFileMetadatas = resFileMetadatas;
    }

    @Override
    public String toString() {
        return "ApiDescribe{" +
                "openUrl='" + openUrl + '\'' +
                ", reqModel='" + reqModel + '\'' +
                ", resModel='" + resModel + '\'' +
                ", reqFieldMetadatas=" + reqFieldMetadatas +
                ", resFieldMetadatas=" + resFieldMetadatas +
                '}';
    }
}
