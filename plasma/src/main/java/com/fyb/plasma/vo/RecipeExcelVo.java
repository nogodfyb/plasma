package com.fyb.plasma.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class RecipeExcelVo {
    @ExcelProperty("waferSource")
    private String waferSource;
    @ExcelProperty("plasma程序")
    private String recipe;
}
