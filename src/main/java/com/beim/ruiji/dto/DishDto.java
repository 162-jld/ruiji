package com.beim.ruiji.dto;

import com.beim.ruiji.entity.Dish;
import com.beim.ruiji.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
