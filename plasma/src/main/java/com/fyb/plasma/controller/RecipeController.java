package com.fyb.plasma.controller;


import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fyb.plasma.common.CommonPage;
import com.fyb.plasma.common.CommonResult;
import com.fyb.plasma.common.Const;
import com.fyb.plasma.dto.RecipePageParam;
import com.fyb.plasma.entity.DiesawUser;
import com.fyb.plasma.entity.Recipe;
import com.fyb.plasma.listener.UploadRecipeListener;
import com.fyb.plasma.service.IRecipeService;
import com.fyb.plasma.vo.RecipeExcelVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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


    //是否有增删改权限
    private Boolean isAuth(HttpSession session){
        DiesawUser user =(DiesawUser) session.getAttribute(Const.CURRENT_USER);
        if("G3393".equals(user.getBn())){
                return true;
        }else return false;
    }


    //根据waferSource查询对应关系
    @GetMapping("{ws}")
    public CommonResult<Recipe> selectRecipeByWs(@PathVariable String ws){
        Recipe recipe = recipeService.getById(ws);
        return recipe!=null?CommonResult.success(recipe):CommonResult.failed();
    }
    //添加
    @PostMapping("add")
    public CommonResult<Object> addRecipe(@RequestBody Recipe recipe,HttpSession session){
        if(!isAuth(session)){
            return CommonResult.forbidden();
        }
        boolean save = recipeService.save(recipe);
        return save?CommonResult.success(null): CommonResult.failed();
    }
    //更新
    @PutMapping("update")
    public CommonResult<Object> updateRecipeByWs(@RequestBody Recipe recipe,HttpSession session){
        if(!isAuth(session)){
            return CommonResult.forbidden();
        }
        boolean update = recipeService.updateById(recipe);
        return update?CommonResult.success(null):CommonResult.failed();
    }
    //删除
    @DeleteMapping("{ws}")
    public CommonResult<Object> deleteRecipeByWs(@PathVariable String ws,HttpSession session){
        if(!isAuth(session)){
            return CommonResult.forbidden();
        }
        boolean remove = recipeService.removeById(ws);
        return remove?CommonResult.success(null):CommonResult.failed();
    }
    //分页查询
    @GetMapping("/list")
    public CommonResult<CommonPage<Recipe>> queryAllUsers(@Valid RecipePageParam pageParam){
        Page<Recipe> recipePage = new Page<>();
        recipePage.setSize(pageParam.getPageSize());
        recipePage.setCurrent(pageParam.getPageNum());
        QueryWrapper<Recipe> queryWrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(pageParam.getWaferSource())){
            queryWrapper.like("ws",pageParam.getWaferSource());
        }
        if(!StringUtils.isEmpty(pageParam.getRecipeName())){
            queryWrapper.like("recipe_name",pageParam.getRecipeName());
        }
        IPage<Recipe> pageResult = recipeService.page(recipePage,queryWrapper);
        CommonPage<Recipe> commonPage = CommonPage.resetPage(pageResult);
        CommonResult<CommonPage<Recipe>> success = CommonResult.success(commonPage);
        return success;
    }
    /**
     * 文件下载（失败了会返回一个有部分数据的Excel）
     * <p>1. 创建excel对应的实体对象 参照{@link RecipeExcelVo}
     * <p>2. 设置返回的 参数
     * <p>3. 直接写，这里注意，finish的时候会自动关闭OutputStream,当然你外面再关闭流问题不大
     */
    @GetMapping("download")
    public void download(HttpServletResponse response) throws IOException {
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("导入plasma关系的模板", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), RecipeExcelVo.class).sheet("模板").doWrite(new ArrayList());
    }

    /**
     * 文件上传
     * <p>1. 创建excel对应的实体对象 参照{@link RecipeExcelVo}
     * <p>2. 由于默认一行行的读取excel，所以需要创建excel一行一行的回调监听器，参照{@link UploadRecipeListener}
     * <p>3. 直接读即可
     */
    @PostMapping("upload")
    @ResponseBody
    public String upload(MultipartFile file,HttpSession session) throws IOException {
        if(!isAuth(session)){
            return "没有权限";
        }
        EasyExcel.read(file.getInputStream(), RecipeExcelVo.class, new UploadRecipeListener(recipeService)).sheet().doRead();
        return "success";
    }

    //存储需要导出的excel到当前session中
    @GetMapping("store")
    public CommonResult<Object> storeExcelInSession(RecipePageParam pageParam, HttpSession session){
        QueryWrapper<Recipe> queryWrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(pageParam.getWaferSource())){
            queryWrapper.like("ws",pageParam.getWaferSource());
        }
        if(!StringUtils.isEmpty(pageParam.getRecipeName())){
            queryWrapper.like("recipe_name",pageParam.getRecipeName());
        }
        List<Recipe> list = recipeService.list(queryWrapper);
        ArrayList<RecipeExcelVo> recipeExcelVos = new ArrayList<>();
        for (Recipe recipe : list) {
            RecipeExcelVo recipeExcelVo = new RecipeExcelVo();
            recipeExcelVo.setWaferSource(recipe.getWs());
            recipeExcelVo.setRecipe(recipe.getRecipeName());
            recipeExcelVos.add(recipeExcelVo);
        }
        //存储到当前session中
        session.setAttribute(Const.CURRENT_EXPORT_EXCEL,recipeExcelVos);
        return CommonResult.success(null);
    }

    //导出当前查询条件下的所有数据
    @GetMapping("exportExcel")
    public void exportExcel(HttpServletResponse response,HttpSession session) throws IOException {
        List<RecipeExcelVo> recipeExcelVos =(List<RecipeExcelVo>) session.getAttribute(Const.CURRENT_EXPORT_EXCEL);
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode(now.format(formatter)+"plasma关系", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), RecipeExcelVo.class).sheet("模板").doWrite(recipeExcelVos);
    }


}
