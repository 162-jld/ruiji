package com.beim.ruiji.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.beim.ruiji.dto.DishDto;
import com.beim.ruiji.entity.Dish;

public interface DishService extends IService<Dish> {

    /**
     * 新增菜品数据，同时将菜品所对应的口味数据
     */
    void saveWithFlavor(DishDto dishDto);

    /**
     * 同时更新菜品数据以及口味数据
     * @param dishDto
     */
    void updateDishWithFlavor(DishDto dishDto);
}
