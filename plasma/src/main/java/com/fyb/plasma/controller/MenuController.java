package com.fyb.plasma.controller;


import com.fyb.plasma.common.CommonResult;
import com.fyb.plasma.entity.Menu;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class MenuController {

    private static final Menu menu1=new Menu(1,0,"用户管理","",null);
    private static final Menu menu2=new Menu(2,1,"recipe关系维护","recipes",null);
    private static final Menu menu3=new Menu(3,1,"plasma记录","records",null);

    @GetMapping("plasma/menu/list")
    public CommonResult<List<Menu>> listAll(){
        ArrayList<Menu> childrenList = new ArrayList<>();
        childrenList.add(menu2);
        childrenList.add(menu3);
        menu1.setChildren(childrenList);
        ArrayList<Menu> menuList = new ArrayList<>();
        menuList.add(menu1);
        return CommonResult.success(menuList);
    }
}
