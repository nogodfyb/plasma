package com.fyb.plasma.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * Plasma 程序关系表
 * </p>
 *
 * @author fyb
 * @since 2020-09-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("plasma_recipe")
public class Recipe implements Serializable {

    private static final long serialVersionUID = 1L;

    private LocalDateTime inTime;

    /**
     * WaferSource
     */
    @TableId(value = "ws")
    private String ws;

    /**
     * 程序名
     */
    private String recipeName;


}
