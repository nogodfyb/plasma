package com.fyb.plasma.controller;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
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

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    public CommonResult<CommonPage<Rec>> list(@Valid RecordPageParam pageParam,HttpSession session){
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
        //转换
        convert(pageResult.getRecords());
        CommonPage<Rec> commonPage = CommonPage.resetPage(pageResult);
        CommonResult<CommonPage<Rec>> success = CommonResult.success(commonPage);
        //存储当前查询的所有数据到当前session中
        List<Rec> list = recService.list(queryWrapper);
        convert(list);
        session.setAttribute(Const.CURRENT_EXPORT_RECORD,list);
        return success;
    }

    private void convert(List<Rec> list){
        for (Rec rec : list) {
            String sf = rec.getSf();
            String start = sf.substring(0, 1);
            String end = sf.substring(1);
            if(end.equals("0")){
                rec.setSf(start+"夜");
            }else rec.setSf(start+"白");
        }
    }

    //导出当前查询条件下的所有数据
    @GetMapping("exportExcel")
    public void exportExcel(HttpServletResponse response,HttpSession session) throws IOException {
        List<Rec> excleVos =(List<Rec>) session.getAttribute(Const.CURRENT_EXPORT_RECORD);
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode(now.format(formatter)+"plasma清洗记录", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), Rec.class).registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).sheet("plasma记录").doWrite(excleVos);
    }

}
