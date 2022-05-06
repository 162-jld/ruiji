package com.beim.ruiji.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beim.ruiji.common.CustomException;
import com.beim.ruiji.entity.Category;
import com.beim.ruiji.entity.Dish;
import com.beim.ruiji.entity.Setmeal;
import com.beim.ruiji.mapper.CategoryMapper;
import com.beim.ruiji.service.CategoryService;
import com.beim.ruiji.service.DishService;
import com.beim.ruiji.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService{

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据ID删除分类信息
     * @param id
     */
    @Override
    public void remove(Long id) {
        log.info("删除分类信息的ID为：" + id);

        // 根据category_id查询是否关联了菜品
        LambdaQueryWrapper<Dish> categoryQueryWrapper = new LambdaQueryWrapper<>();
        categoryQueryWrapper.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(categoryQueryWrapper);

        if (count1 > 0){
            // 表明分类关联了菜品，此时，不能删除分类
            throw new CustomException("当前分类下关联了菜品，不能直接删除！");
        }


        // 根据category_id查询是否关联了套餐
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);

        if (count2 > 0){
            // 表明分类信息关联了套餐信息，不能直接删除分类信息
            throw new CustomException("当前分类下关联了套餐，不能直接删除！");
        }

        super.removeById(id);
    }
}
