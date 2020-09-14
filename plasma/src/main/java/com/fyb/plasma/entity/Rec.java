package com.fyb.plasma.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * Plasma记录
 * </p>
 *
 * @author fyb
 * @since 2020-09-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("plasma_rec")
public class Rec implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "r_id", type = IdType.AUTO)
    @ExcelIgnore
    private Long rId;

    @ExcelProperty("清洗时间")
    private Date inTime;

    /**
     * 机器号
     */
    @ExcelProperty("机器号")
    private String mc;

    /**
     * 芯片型号
     */
    @ExcelProperty("芯片型号")
    private String tt;

    /**
     * WaferSource
     */
    @ExcelProperty("waferSource")
    private String ws;

    /**
     * WaferLot+片号
     */
    @ExcelProperty("waferLot")
    private String wl;

    /**
     * 工号
     */
    @ExcelProperty("工号")
    private String bn;

    /**
     * 程序名
     */
    @ExcelProperty("plasma程序名")
    private String recipeName;

    /**
     * 班次  A0 =A夜
     */
    @ExcelProperty("班次")
    private String sf;

    /**
     * 本条记录是第几次Plasma
     */
    @ExcelProperty("第几次")
    private Integer plasmaCount;

    /**
     * 是否超次数
     */
    @ExcelIgnore
    private Boolean isOverCount;


}
