package com.newland1;


import netbank.firm.api.dto.cash.transfer.SendBackTransferReq;
import netbank.firm.api.model.RestfulRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

/**
 * @function 转账指令退回经办
 *
 * @author wot_caizhipeng_xdl
 * @version Ver 1.0 2020年10月30日
 * @since Ver 1.0
 */
@RequestMapping("/transfer")
@Controller
public class SendBackTransferController {

    /**
     * @function 转账指令退回经办
     * @author zhengshenming
     * @author zhangsan
     * @date 2021-03-17
     * @since V1.0
     * @param request
     * @return
     */
    @RequestMapping(value = "sendBackTransfer", method = RequestMethod.POST)
    @ResponseBody
    public SendBackRespTest sendBackTransfer(@Valid @RequestBody RestfulRequest<SendBackTransferReq> request)
    {
//        return  false;
        return null;
    }

    @RequestMapping
    public static void main(String[] args){

    }
}
