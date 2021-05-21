package com.newland;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by wot_zhengshenming on 2021/3/25.
 */
public class TransferRespItem {
    /**
     * 工作流编号
     */
    private Integer workFlowId;

    /**
     * 网银交易参考号
     */
    private String srvcNo;


    // 下一处理人
    private String nextCaller;
    // 下一处理人名称
    private String nextCallerName;

    // 下一处理岗
    private Integer nextStep;

    // 工作流业务类型
    private String bizCode;

    // 指令处理状态
    private String bizStatus;

    // 执行情况
    private String processResult;

    // 创建时间

    private Date createTime;


    // 付款账户
    private String nraFromAcctNo;
    private String fromAcctName;
    // 收款账户
    private String nraToAcctNo;

    // 收款户名
    private String toAcctName;

    // 转账金额
    private BigDecimal transferAmount;

    // 凭证类型
    private String vochrType;

    // 凭证号
    private String vochrNo;

    // 手续费
    private BigDecimal handleCharge;

    public Integer getWorkFlowId()
    {
        return workFlowId;
    }

    public void setWorkFlowId(Integer workFlowId)
    {
        this.workFlowId = workFlowId;
    }

    public String getSrvcNo()
    {
        return srvcNo;
    }

    public void setSrvcNo(String srvcNo)
    {
        this.srvcNo = srvcNo;
    }


    public String getNextCaller()
    {
        return nextCaller;
    }

    public void setNextCaller(String nextCaller)
    {
        this.nextCaller = nextCaller;
    }

    public Integer getNextStep()
    {
        return nextStep;
    }

    public void setNextStep(Integer nextStep)
    {
        this.nextStep = nextStep;
    }

    public String getBizCode()
    {
        return bizCode;
    }

    public void setBizCode(String bizCode)
    {
        this.bizCode = bizCode;
    }

    public String getBizStatus()
    {
        return bizStatus;
    }

    public void setBizStatus(String bizStatus)
    {
        this.bizStatus = bizStatus;
    }

    public String getProcessResult()
    {
        return processResult;
    }

    public void setProcessResult(String processResult)
    {
        this.processResult = processResult;
    }
    @JsonFormat(pattern = "yyyyMMddHHmmss", timezone = "GMT+8")
    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }


    public String getNraFromAcctNo()
    {
        return nraFromAcctNo;
    }

    public void setNraFromAcctNo(String nraFromAcctNo)
    {
        this.nraFromAcctNo = nraFromAcctNo;
    }

    public String getFromAcctName() {
        return fromAcctName;
    }

    public void setFromAcctName(String fromAcctName) {
        this.fromAcctName = fromAcctName;
    }

    public String getNraToAcctNo()
    {
        return nraToAcctNo;
    }

    public void setNraToAcctNo(String nraToAcctNo)
    {
        this.nraToAcctNo = nraToAcctNo;
    }

    public String getToAcctName()
    {
        return toAcctName;
    }

    public void setToAcctName(String toAcctName)
    {
        this.toAcctName = toAcctName;
    }

    public BigDecimal getTransferAmount()
    {
        return transferAmount;
    }

    public void setTransferAmount(BigDecimal transferAmount)
    {
        this.transferAmount = transferAmount;
    }

    public String getVochrType()
    {
        return vochrType;
    }

    public void setVochrType(String vochrType)
    {
        this.vochrType = vochrType;
    }

    public String getVochrNo()
    {
        return vochrNo;
    }

    public void setVochrNo(String vochrNo)
    {
        this.vochrNo = vochrNo;
    }

    public BigDecimal getHandleCharge()
    {
        return handleCharge;
    }

    public void setHandleCharge(BigDecimal handleCharge)
    {
        this.handleCharge = handleCharge;
    }

    public String getNextCallerName() {
        return nextCallerName;
    }

    public void setNextCallerName(String nextCallerName) {
        this.nextCallerName = nextCallerName;
    }
}
