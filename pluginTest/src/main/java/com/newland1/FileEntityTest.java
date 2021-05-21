package com.newland1;

import com.cib.cap4i.file.text.annotation.DataColumn;
import com.cib.cap4i.file.text.annotation.FileEntity;
import com.cib.cap4i.formater.BigDecimalFormatter;

import java.math.BigDecimal;

/**
 * Created by wot_zhengshenming on 2021/4/20.
 */
@FileEntity(delimiter = "|",rowSize=530,isFix=true,fields = {"hxjylsbh", "je", "tradeDateString"})
public class FileEntityTest {
    @DataColumn(name = "核心交易流水编号30", fixLength = 30)
    private String hxjylsbh;// 核心交易流水编号30
    @DataColumn(name = "账户余额", fixLength = 15, formatter = BigDecimalFormatter.class)
    private BigDecimal je;// 传票组序号10
	@DataColumn(name = "交易日期", fixLength=10, pattern = "yyyyMMdd")
    private String tradeDateString;// 交易日期8
    @DataColumn(name = "交易时间6", fixLength = 6)
    private String tradeTimeString;// 交易时间6
    @DataColumn(name = "交易地区代号2", fixLength = 2)
    private String areaCode;// 交易地区代号2
    @DataColumn(name = "交易机构代号3", fixLength = 3)
    private String branchNo;// 交易机构代号3
    @DataColumn(name = "交易代码4", fixLength = 4)
    private String jydm;// 交易代码4
    @DataColumn(name = "记账交易代码4", fixLength = 4)
    private String jzjydm;// 记账交易代码4
}
