package com.fyb.plasma.controller;


import com.fyb.plasma.common.CommonResult;
import com.fyb.plasma.entity.Recipe;
import com.fyb.plasma.service.IRecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * Plasma 程序关系表 前端控制器
 * </p>
 *
 * @author fyb
 * @since 2020-09-08
 */
@RestController
@RequestMapping("/plasma/recipe")
public class RecipeController {

    @Autowired
    private IRecipeService recipeService;


    //根据waferSource查询对应关系
    @GetMapping("{ws}")
    public CommonResult<Recipe> selectRecipeByWs(@PathVariable String ws){
        Recipe recipe = recipeService.getById(ws);
        return recipe!=null?CommonResult.success(recipe):CommonResult.failed();
    }

}
