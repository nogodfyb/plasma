package com.fyb.plasma.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fyb.plasma.common.CommonResult;
import com.fyb.plasma.common.Const;
import com.fyb.plasma.dto.AdminParam;
import com.fyb.plasma.entity.DiesawUser;
import com.fyb.plasma.service.IDiesawUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private IDiesawUserService userService;

    //扫工号登录
    @PostMapping("login")
    public CommonResult<Object> login(@RequestBody DiesawUser user, HttpSession session){
        if(!StringUtils.isEmpty(user.getBn())){
            session.setAttribute(Const.CURRENT_USER,user);
            return CommonResult.success(null);
        }else return CommonResult.failed();
    }

    //验证是否具有超次权限
    @GetMapping("auth")
    public CommonResult<Object> auth(HttpSession session){
        DiesawUser user = (DiesawUser) session.getAttribute(Const.CURRENT_USER);
        String bn = user.getBn();
        DiesawUser diesawUser = userService.getById(bn);
        if(diesawUser==null){
            return CommonResult.failed();
        }
        Boolean auth1 = diesawUser.getAuth1();
        return auth1?CommonResult.success(null):CommonResult.failed();
    }

    //具有超次权限的用户输入密码
    @PostMapping("loginAuth")
    public CommonResult<Object> loginAuth(@RequestBody DiesawUser user,HttpSession session){
        DiesawUser diesawUser = (DiesawUser) session.getAttribute(Const.CURRENT_USER);
        QueryWrapper<DiesawUser> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("bn",diesawUser.getBn()).eq("pw",user.getPw()).eq("auth1",1);
        DiesawUser one = userService.getOne(userQueryWrapper);
        return one!=null?CommonResult.success(null):CommonResult.failed();
    }

    //后台管理系统登录
    @PostMapping("adminLogin")
    public CommonResult<Object> adminLogin(@RequestBody AdminParam adminParam, HttpSession session){
        boolean status1 = "admin".equals(adminParam.getUsername());
        boolean status2 = "admin".equals(adminParam.getPassword());
        if(status1&&status2){
            // 登录成功存储在session中
            session.setAttribute(Const.CURRENT_USER,adminParam);
            return CommonResult.success(null);
        }
        return CommonResult.failed();
    }

    //检验用户是否已登录
    @GetMapping("isLogin")
    public CommonResult<Object> isLogin(HttpSession session){
        Object user = session.getAttribute(Const.CURRENT_USER);
        if (user==null) {
            return  CommonResult.failed();
        }else return CommonResult.success(null);
    }



}
