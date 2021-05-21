package com.newland1;


import netbank.firm.api.dto.cash.transfer.CreateTransferReq;
import netbank.firm.api.dto.cash.transfer.CreateTransferResp;
import netbank.firm.api.model.RestfulRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by wot_zhengshenming on 2021/2/19.
 */
@Controller
@RequestMapping("/transfer")
public class CreateTransferController<T> {
    /**
     * 生成指令
     * @param restfulRequest 生成指令需要的参数
     * @return 生成指令结果
     * @throws Exception
     * @author wot_chentao_xdl
     * @version Ver 1.0 2020/10/29
     * @since Ver 1.0
     * @changelog 增加了方法变更日志信息
     */
    @ResponseBody
    @RequestMapping(value = "/createTransfer1", method = RequestMethod.POST)
    public List<CreateTransferResp> createTransfer(@RequestBody @Valid RestfulRequest<CreateTransferReq> restfulRequest) throws Exception{
        return null;

    }
}
