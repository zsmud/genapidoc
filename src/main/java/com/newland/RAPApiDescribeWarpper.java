package com.newland;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.core.type.classreading.FieldMetadata;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by wot_zhengshenming on 2021/5/8.
 * RAP数据包装类
 */
public class RAPApiDescribeWarpper {

    Project project;

    public static Project api2project(Project project,List<ApiDescribe> list,List<DocVersion> versions){
        Assert.notNull(project,"需要创建一个RAP project对象");
        if(versions!=null&&versions.size()>0){
            project.setVersion(versions.get(versions.size()-1).getVersionNum());
            project.setLog(versions.get(versions.size()-1).getChangelog());
        }
        project.getModuleList().get(0).setName(project.projectName);
        project.getModuleList().get(0).setIntroduction(project.projectName);
        project.getModuleList().get(0).getPageList().get(0).setIntroduction(project.projectName);
        project.getModuleList().get(0).getPageList().get(0).setName(project.projectName+"服务集");
        List<Action> actions = project.getModuleList().get(0).getPageList().get(0).getActionList();
        list.parallelStream().forEach(apiDescribe -> {
            Action action = new Action();
            action.setName(apiDescribe.getApiName());
            action.setChargeMan(apiDescribe.getAuthor());
            action.setDescription(apiDescribe.getDescribe());
            action.setRequestUrl(apiDescribe.getOpenUrl());
            action.setRequestPublic(1);
            action.setResponsePublic(2);
            action.setRequestType("2");
            action.setRemarks(apiDescribe.getChangelog());
            //输入输出处理
            List<Parameter> requests = new ArrayList<Parameter>();
            List<FieldMetadata> reqs = apiDescribe.getReqFieldMetadatas();
            newParameter(reqs.iterator(),requests);
            action.setRequestParameterList(requests);
            List<Parameter> responses = new ArrayList<Parameter>();
            List<FieldMetadata> ress = apiDescribe.getResFieldMetadatas();
            newParameter(ress.iterator(),responses);
            action.setResponseParameterList(responses);
            //文件处理
            List<Parameter> fileRequests = new ArrayList<Parameter>();
            List<FieldMetadata> fileReqs = apiDescribe.getReqFileMetadatas();
            newParameter(fileReqs.iterator(),fileRequests);
            action.setRequestFileParamList(fileRequests);
            List<Parameter> fileResponses = new ArrayList<Parameter>();
            List<FieldMetadata> fileRess = apiDescribe.getResFileMetadatas();
            newParameter(fileRess.iterator(),fileResponses);
            action.setResponseFileParamList(fileResponses);
            actions.add(action);
        });
        return project;
    }

    public static boolean newParameter(Iterator<FieldMetadata> it,List<Parameter> list){
        while(it.hasNext()){
            FieldMetadata fieldMetadata = it.next();
            String memo = fieldMetadata.getMemo();
            if(fieldMetadata.getColor() && StringUtils.hasText(memo) && memo.contains("结束"))
                return false;
            Parameter parameter = new Parameter();
            parameter.setDataType(fieldMetadata.getFieldTypeShortName());
            parameter.setIdentifier(fieldMetadata.getFieldName());
            parameter.setMustNeed(fieldMetadata.isNotNull());
            parameter.setName(fieldMetadata.getFieldCNName());
            parameter.setRemark(fieldMetadata.getMemo());
            parameter.setValidator(fieldMetadata.getLength());
            if(fieldMetadata.getColor() && StringUtils.hasText(memo) && memo.contains("开始")){
                newParameter(it,parameter.getParameterList());
            }
            list.add(parameter);
        }
        return true;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}

/**
 * RAP工程
 */
class Project{
    @JSONField(ordinal = 1)
    Integer id;
    @JSONField(ordinal = 2)
    String version;
    @JSONField(ordinal = 3)
    String log;
    @JSONField(ordinal = 4)
    List<Module> moduleList;
    @JSONField(serialize = false)
    String projectName;

    public Project(Integer id,String projectName){
        this.id = id;
        this.projectName = projectName;
        Module module = new Module();
        moduleList = new ArrayList<Module>();
        moduleList.add(module);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public List<Module> getModuleList() {
        return moduleList;
    }

    public void setModuleList(List<Module> moduleList) {
        this.moduleList = moduleList;
    }
}

/**
 * 模块如cash-api
 */
class Module{
    @JSONField(ordinal = 1)
    String name;
    @JSONField(ordinal = 2)
    String introduction;
    @JSONField(ordinal = 3)
    List<Page> pageList;

    public Module(){
        Page page = new Page();
        pageList = new ArrayList<Page>();
        pageList.add(page);
    }

    public Module(String name,String introduction){
        this.name = name;
        this.introduction = introduction;
        pageList = new ArrayList<Page>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public List<Page> getPageList() {
        return pageList;
    }

    public void setPageList(List<Page> pageList) {
        this.pageList = pageList;
    }
}

/**
 * 接口服务集，如对外转账服务集、权限服务集、理财服务集
 */
class Page{
    @JSONField(ordinal = 1)
    String name;
    @JSONField(ordinal = 2)
    String introduction;
    @JSONField(ordinal = 3)
    List<Action> actionList;

    public Page(){
        actionList = new ArrayList<Action>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public List<Action> getActionList() {
        return actionList;
    }

    public void setActionList(List<Action> actionList) {
        this.actionList = actionList;
    }
}

/**
 * 接口
 */
class Action{
    @JSONField(ordinal = 1)
    String name;
    @JSONField(ordinal = 2)
    String description;
    @JSONField(ordinal = 3)
    String requestType;
    @JSONField(ordinal = 4)
    String requestUrl;
    @JSONField(ordinal = 5)
    Integer requestPublic;
    @JSONField(ordinal = 6)
    Integer responsePublic;
    @JSONField(ordinal = 7)
    String remarks;
    @JSONField(ordinal = 8)
    String chargeMan;
    /**
     * 请求字段
     */
    @JSONField(ordinal = 9)
    List<Parameter> requestParameterList;
    /**
     * 返回字段
     */
    @JSONField(ordinal = 10)
    List<Parameter> responseParameterList;
    /**
     * 请求文件字段
     */
    @JSONField(ordinal = 11)
    List<Parameter> requestFileParamList;

    public List<Parameter> getResponseFileParamList() {
        return responseFileParamList;
    }

    public void setResponseFileParamList(List<Parameter> responseFileParamList) {
        this.responseFileParamList = responseFileParamList;
    }

    public List<Parameter> getRequestFileParamList() {
        return requestFileParamList;
    }

    public void setRequestFileParamList(List<Parameter> requestFileParamList) {
        this.requestFileParamList = requestFileParamList;
    }

    /**
     * 返回文件字段
     */
    @JSONField(ordinal = 12)
    List<Parameter> responseFileParamList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public Integer getRequestPublic() {
        return requestPublic;
    }

    public void setRequestPublic(Integer requestPublic) {
        this.requestPublic = requestPublic;
    }

    public Integer getResponsePublic() {
        return responsePublic;
    }

    public void setResponsePublic(Integer responsePublic) {
        this.responsePublic = responsePublic;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getChargeMan() {
        return chargeMan;
    }

    public void setChargeMan(String chargeMan) {
        this.chargeMan = chargeMan;
    }

    public List<Parameter> getRequestParameterList() {
        return requestParameterList;
    }

    public void setRequestParameterList(List<Parameter> requestParameterList) {
        this.requestParameterList = requestParameterList;
    }

    public List<Parameter> getResponseParameterList() {
        return responseParameterList;
    }

    public void setResponseParameterList(List<Parameter> responseParameterList) {
        this.responseParameterList = responseParameterList;
    }
}
/**
 * 字段元数据描述
 */
class Parameter{
    @JSONField(ordinal = 1)
    String identifier;
    @JSONField(ordinal = 2)
    String name;
    @JSONField(ordinal = 3)
    String dataType;
    @JSONField(ordinal = 4)
    String validator;
    @JSONField(ordinal = 5)
    Boolean mustNeed;
    @JSONField(ordinal = 6)
    String remark;
    @JSONField(ordinal = 7)
    List<Parameter> parameterList;

    public Parameter(){
        parameterList = new ArrayList<Parameter>();
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getValidator() {
        return validator;
    }

    public void setValidator(String validator) {
        this.validator = validator;
    }

    public Boolean getMustNeed() {
        return mustNeed;
    }

    public void setMustNeed(Boolean mustNeed) {
        this.mustNeed = mustNeed;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<Parameter> getParameterList() {
        return parameterList;
    }

    public void setParameterList(List<Parameter> parameterList) {
        this.parameterList = parameterList;
    }
}