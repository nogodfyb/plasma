package com.fyb.plasma.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

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
    private Long rId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime inTime;

    /**
     * 机器号
     */
    private String mc;

    /**
     * 芯片型号
     */
    private String tt;

    /**
     * WaferSource
     */
    private String ws;

    /**
     * WaferLot+片号
     */
    private String wl;

    /**
     * 工号
     */
    private String bn;

    /**
     * 程序名
     */
    private String recipeName;

    /**
     * 班次  A0 =A夜
     */
    private String sf;

    /**
     * 本条记录是第几次Plasma
     */
    private Integer plasmaCount;

    /**
     * 是否超次数
     */
    private Boolean isOverCount;


}
