package com.newland;

/**
 * Created by wot_zhengshenming on 2021/5/6.
 * 文档修订记录
 */
public class DocVersion {

    private String versionDate;

    private String versionNum;

    private String changelog;

    public String getChangelog() {
        return changelog;
    }

    public void setChangelog(String changelog) {
        this.changelog = changelog;
    }

    public String getVersionDate() {
        return versionDate;
    }

    public void setVersionDate(String versionDate) {
        this.versionDate = versionDate;
    }

    public String getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(String versionNum) {
        this.versionNum = versionNum;
    }
}
