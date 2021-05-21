package com.newland1;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by wot_zhengshenming on 2021/4/25.
 */
public class SendBackRespTest extends ParentBean{
    /**
     * 字符串
     */
    @NotNull
    @Length(max=10)
    private String str;
    /**
     * 日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date date;
    /**
     * 金额
     */
    private BigDecimal bg;
    /**
     * 复合对象
     */
    private SonBean sb;
    /**
     * 数组测试
     */
    private SonBean[] asb;
    /**
     * List测试
     */
    private List<SonBean> lsb;
    /**
     * Map测试
     */
    private Map<Date,List<SonBean>> msb;
}
