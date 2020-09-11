package com.fyb.plasma.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RecordPageParam {

    @NotNull(message = "pageNum不能为空")
    private Integer pageNum;
    @NotNull(message = "pageSize不能为空")
    private Integer pageSize;
    @NotNull(message = "waferSource不能为空")
    private String waferSource;
    @NotNull(message = "recipeName不能为空")
    private String recipeName;
    @NotNull(message = "waferLot不能为空")
    private String waferLot;
    @NotNull(message = "device不能为空")
    private String device;
    private String startTime;
    private String endTime;

}
