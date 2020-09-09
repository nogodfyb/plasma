package com.fyb.plasma.service.impl;

import com.fyb.plasma.entity.Recipe;
import com.fyb.plasma.mapper.RecipeMapper;
import com.fyb.plasma.service.IRecipeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Plasma 程序关系表 服务实现类
 * </p>
 *
 * @author fyb
 * @since 2020-09-08
 */
@Service
public class RecipeServiceImpl extends ServiceImpl<RecipeMapper, Recipe> implements IRecipeService {

}
