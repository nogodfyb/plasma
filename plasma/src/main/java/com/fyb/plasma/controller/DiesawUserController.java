package com.fyb.plasma.controller;


import com.fyb.plasma.common.CommonResult;
import com.fyb.plasma.common.Const;
import com.fyb.plasma.entity.DiesawUser;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * <p>
 * cm	diesaw 用户表 前端控制器
 * </p>
 *
 * @author fyb
 * @since 2020-09-08
 */
@RestController
@RequestMapping("/plasma/user")
public class DiesawUserController {


    //扫工号登录
    @PostMapping("login")
    public CommonResult<Object> login(@RequestBody DiesawUser user, HttpSession session){
        if(!StringUtils.isEmpty(user.getBn())){
            session.setAttribute(Const.CURRENT_USER,user);
            return CommonResult.success(null);
        }else return CommonResult.failed();
    }


}
