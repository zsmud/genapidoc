package com.newland1;

import netbank.firm.api.annotation.FileEntityFormat;
import netbank.firm.api.annotation.FileProperties;
import netbank.firm.api.dto.cash.transfer.CreateTransferReq;
import netbank.firm.api.dto.cash.transfer.CreateTransferResp;
import netbank.firm.api.model.RestfulRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

/**
 * Created by wot_zhengshenming on 2021/4/12.
 */
@Controller
public class CreateTransferSonController extends  CreateTransferController<CreateTransferResp>{

    @ResponseBody
    @RequestMapping(value = "/createSonTransfer", method = RequestMethod.POST)
    @FileEntityFormat(files={@FileProperties(name="fileName",direct = FileProperties.Direct.REEUEST,cls=FileEntityTest.class),
            @FileProperties(name="respName",direct = FileProperties.Direct.RESPONSE,cls=FileEntityTestResp.class)})
    public CreateTransferResp createSonTransfer(@RequestBody @Valid RestfulRequest<CreateTransferReq> restfulRequest) throws Exception{
        return null;

    }

}
