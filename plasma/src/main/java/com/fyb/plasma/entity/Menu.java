package com.fyb.plasma.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class Menu {

    private Integer id;

    private Integer parentId;

    private String authName;

    private String path;

    private List<Menu> children;


}
