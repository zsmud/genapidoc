package com.newland;

import java.util.List;

/**
 * 转账指令批量操作接口响应类
 *
 * @author wot_caizhipeng_xdl
 * @version Ver 1.0 2020年10月30日
 * @since Ver 1.0
 */
public class BatchOperTransferResp
{
    private Integer totalCount;

    private List<TransferRespItem> resultItems;


    public Integer getTotalCount()
    {
        return totalCount;
    }


    public void setTotalCount(Integer totalCount)
    {
        this.totalCount = totalCount;
    }


    public List<TransferRespItem> getResultItems()
    {
        return resultItems;
    }


    public void setResultItems(List<TransferRespItem> resultItems)
    {
        this.resultItems = resultItems;
    }

}

