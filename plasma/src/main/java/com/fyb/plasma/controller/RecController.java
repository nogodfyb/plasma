package com.fyb.plasma.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fyb.plasma.common.CommonResult;
import com.fyb.plasma.common.Const;
import com.fyb.plasma.dto.WaferPlasma;
import com.fyb.plasma.entity.DiesawUser;
import com.fyb.plasma.entity.Rec;
import com.fyb.plasma.service.IRecService;
import com.fyb.plasma.util.ShiftUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
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
            rec.setPlasmaCount(count);
            rec.setIsOverCount(count>Const.OVER_TIMES);
            rec.setSf(ShiftUtils.getShiftByNow());
            recService.save(rec);
        }
        return CommonResult.success(null);
    }

    //判断当前waferLot是否超次清洗
    @GetMapping("/isOverCount")
    public CommonResult<Boolean> isOverCount(String waferLot){
        QueryWrapper<Rec> recQueryWrapper = new QueryWrapper<>();
        recQueryWrapper.eq("wl",waferLot);
        int count = recService.count(recQueryWrapper);
        return CommonResult.success(count>=Const.OVER_TIMES);
    }

}
