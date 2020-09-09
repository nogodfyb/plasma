package com.fyb.plasma.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * cm	diesaw 用户表
 * </p>
 *
 * @author fyb
 * @since 2020-09-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DiesawUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工号
     */

    @TableId(value = "bn")
    private String bn;

    /**
     * 密码
     */
    private String pw;

    /**
     * 角色
     */
    private String role;

    /**
     * 放行超Plasma次数Wafer权限
     */
    private Boolean auth1;


}
