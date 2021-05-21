package com.newland;

/**
 * Created by wot_zhengshenming on 2021/3/16.
 */
/**
 * 转账指令退回接口请求类
 * 〈功能详细描述〉
 *
 * @author wot_caizhipeng_xdl
 * @version Ver 1.0 2020年10月30日
 * @since Ver 1.0
 */
public class SendBackTransferReq  extends BatchOperTransferReq
{


    /**
     * （用一句话描述这个变量表示什么）
     */

    private static final long serialVersionUID = 1L;

    @Override
    public String content()
            throws IllegalAccessException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String apiUrl()
    {
        // TODO Auto-generated method stub
        return "/firm/cashapi/transfer/sendBackTransfer";
    }
}
