package com.newland.parser;

import org.springframework.core.type.classreading.FieldMetadata;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wot_zhengshenming on 2021/3/24.
 * 解析类的上下文
 */
public class ParserContext {

    /**
     * 要解析的类，具体格式为，java.util.List
     */
    String className;

    /**
     * 通过asm解析处理的类字段
     */
    List<FieldMetadata> fields;

    /**
     * 类签名，类似 Ljava/util/List<Ljava.util.String;>;
     */
    String classSignature;

    /**
     * className 的超类
     */
    List<String> superClasses;

    /**
     * 类字段中的泛型类
     */
    List<String> genericClasses;

    /**
     * asm资源工厂
     */
    MetadataReaderFactory factory;

    /**
     * 类型解析器调度器
     */
    DispatcherParser dispatcher;

    /**
     * 前缀属性名称
     */
    String prefixName;

    String memo;

    public ParserContext(MetadataReaderFactory factory,String className,String classSignature) {
        this.factory = factory;
        this.className = className;
        this.dispatcher = new DispatcherParser();
        this.fields = new ArrayList<FieldMetadata>();
        this.superClasses = new ArrayList<String>();
        this.genericClasses = new ArrayList<String>();
        this.classSignature = classSignature;
    }


    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassSignature() {
        return classSignature;
    }

    public void setClassSignature(String classSignature) {
        this.classSignature = classSignature;
    }

    public List<String> getSuperClasses() {
        return superClasses;
    }

    public void setSuperClasses(List<String> superClasses) {
        this.superClasses = superClasses;
    }

    public List<FieldMetadata> getFields() {
        return fields;
    }

    public void setFields(List<FieldMetadata> fields) {
        this.fields = fields;
    }

    public List<String> getGenericClasses() {
        return genericClasses;
    }

    public void setGenericClasses(List<String> genericClasses) {
        this.genericClasses = genericClasses;
    }

    public MetadataReaderFactory getFactory() {
        return factory;
    }

    public void setFactory(MetadataReaderFactory factory) {
        this.factory = factory;
    }

    public DispatcherParser getDispatcher() {
        return dispatcher;
    }

    public void setDispatcher(DispatcherParser dispatcher) {
        this.dispatcher = dispatcher;
    }

    public String getPrefixName() {
        return prefixName;
    }

    public void setPrefixName(String prefixName) {
        this.prefixName = prefixName;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
