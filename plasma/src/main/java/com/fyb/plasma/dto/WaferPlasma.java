package com.fyb.plasma.dto;

import lombok.Data;

@Data
public class WaferPlasma {
    private String device;
    private String recipeName;
    private String waferLot;
    private String waferSource;
}
