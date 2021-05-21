package com.newland;

/**
 * Created by wot_zhengshenming on 2021/3/16.
 */

import netbank.firm.api.model.RestfulParamBase;
import netbank.firm.api.util.ChineseLength;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 转账指令接口请求类
 * 〈功能详细描述〉
 *
 * @author wot_caizhipeng_xdl
 * @version Ver 1.0 2020年10月30日
 * @since Ver 1.0
 */
public abstract class BatchOperTransferReq implements RestfulParamBase
{
    /**
     * （用一句话描述这个变量表示什么）
     */

    private static final long serialVersionUID = 1L;

    /**
     * 网银客户号
     */
    @NotBlank(message = "网银客户号不能为空")
    @Length(max = 10, message = "网银客户号最大长度不能超过10")
    private String ncid;

    /**
     * 操作员ID
     */
    @NotBlank(message = "操作员ID不能为空")
    @Length(max = 6, message = "操作员ID最大长度不能超过6")
    private String oid;

    @NotNull(message = "工作流编号不能为空")
    @Size(min=1,max=30,message="工作流数量范围为1至30")
    private List<Integer> workFlowIds;

    /** 渠道类型    */
    @Length(max = 1, message = "渠道类型最大长度不能超过1")
    private String terminalType;


    @ChineseLength(max = 120, message = "理由最大长度不能超过120")
    private String cancelReason;

    public String getCancelReason()
    {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason)
    {
        this.cancelReason = cancelReason;
    }

    public String getNcid()
    {
        return ncid;
    }

    public void setNcid(String ncid)
    {
        this.ncid = ncid;
    }

    public String getOid()
    {
        return oid;
    }

    public void setOid(String oid)
    {
        this.oid = oid;
    }

    public List<Integer> getWorkFlowIds()
    {
        return workFlowIds;
    }

    public void setWorkFlowIds(List<Integer> workFlowIds)
    {
        this.workFlowIds = workFlowIds;
    }

    public String getTerminalType()
    {
        return terminalType;
    }

    public void setTerminalType(String terminalType)
    {
        this.terminalType = terminalType;
    }
}

