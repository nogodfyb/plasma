package com.fyb.plasma.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fyb.plasma.common.CommonPage;
import com.fyb.plasma.common.CommonResult;
import com.fyb.plasma.common.Const;
import com.fyb.plasma.dto.RecordPageParam;
import com.fyb.plasma.dto.WaferPlasma;
import com.fyb.plasma.entity.DiesawUser;
import com.fyb.plasma.entity.Rec;
import com.fyb.plasma.service.IRecService;
import com.fyb.plasma.util.ShiftUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * Plasma记录 前端控制器
 * </p>
 *
 * @author fyb
 * @since 2020-09-09
 */
@RestController
@RequestMapping("/plasma/rec")
public class RecController {

    @Autowired
    private IRecService recService;

    //添加清洗记录
    @PostMapping("/add")
    public CommonResult<Object> addRecord(@RequestBody List<WaferPlasma> list, HttpSession session){
        for (WaferPlasma waferPlasma : list) {
            String waferLot = waferPlasma.getWaferLot();
            QueryWrapper<Rec> recQueryWrapper = new QueryWrapper<>();
            recQueryWrapper.eq("wl",waferLot);
            int count = recService.count(recQueryWrapper);
            count++;
            Rec rec = new Rec();
            DiesawUser user = (DiesawUser)session.getAttribute(Const.CURRENT_USER);
            rec.setBn(user.getBn());
            rec.setTt(waferPlasma.getDevice());
            rec.setWs(waferPlasma.getWaferSource());
            rec.setWl(waferPlasma.getWaferLot());
            rec.setRecipeName(waferPlasma.getRecipeName());
            rec.setPlasmaCount(count);
            rec.setIsOverCount(count>Const.OVER_TIMES);
            rec.setSf(ShiftUtils.getShiftByNow());
            recService.save(rec);
        }
        return CommonResult.success(null);
    }

    //判断当前waferLot是否超次清洗
    @GetMapping("/isOverCount")
    public CommonResult<Integer> isOverCount(String waferLot){
        QueryWrapper<Rec> recQueryWrapper = new QueryWrapper<>();
        recQueryWrapper.eq("wl",waferLot);
        int count = recService.count(recQueryWrapper);
        return count>=Const.OVER_TIMES?CommonResult.success(count):CommonResult.failed(count);
    }
    //分页查询
    @GetMapping("list")
    public CommonResult<CommonPage<Rec>> list(@Valid RecordPageParam pageParam){
        Page<Rec> recordPage = new Page<>();
        recordPage.setSize(pageParam.getPageSize());
        recordPage.setCurrent(pageParam.getPageNum());
        QueryWrapper<Rec> queryWrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(pageParam.getWaferSource())){
            queryWrapper.like("ws",pageParam.getWaferSource());
        }
        if(!StringUtils.isEmpty(pageParam.getRecipeName())){
            queryWrapper.like("recipe_name",pageParam.getRecipeName());
        }
        if(!StringUtils.isEmpty(pageParam.getDevice())){
            queryWrapper.like("tt",pageParam.getDevice());
        }
        if(!StringUtils.isEmpty(pageParam.getWaferLot())){
            queryWrapper.like("wl",pageParam.getWaferLot());
        }
        if(!StringUtils.isEmpty(pageParam.getStartTime())){
            queryWrapper.between("in_time",pageParam.getStartTime(),pageParam.getEndTime());
        }
        IPage<Rec> pageResult = recService.page(recordPage,queryWrapper);
        CommonPage<Rec> commonPage = CommonPage.resetPage(pageResult);
        CommonResult<CommonPage<Rec>> success = CommonResult.success(commonPage);
        return success;
    }

}
