package com.beim.ruiji.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beim.ruiji.dto.DishDto;
import com.beim.ruiji.entity.Dish;
import com.beim.ruiji.entity.DishFlavor;
import com.beim.ruiji.mapper.DishMapper;
import com.beim.ruiji.service.DishFlavorService;
import com.beim.ruiji.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private DishService dishService;

    @Override
    public void saveWithFlavor(DishDto dishDto) {
        // 保存菜品数据
        this.save(dishDto);
        // 获取菜品ID
        Long categoryId = dishDto.getCategoryId();
        // 设置口味数据所对应的菜品ID
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
           item.setDishId(categoryId);
           return item;
        }).collect(Collectors.toList());

        // 保存口味数据
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 同时更新菜品数据以及口味数据
     * @param dishDto
     */
    @Override
    public void updateDishWithFlavor(DishDto dishDto) {
        Dish dish = new Dish();
        // 更新菜品表的数据
        BeanUtils.copyProperties(dishDto,dish);
        dishService.update(dish,new LambdaQueryWrapper<Dish>().eq(Dish::getId,dish.getId()));
        // 更新口味表的数据
        dishFlavorService.updateBatchById(dishDto.getFlavors());
    }
}
