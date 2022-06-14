package com.beim.ruiji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.beim.ruiji.common.R;
import com.beim.ruiji.dto.DishDto;
import com.beim.ruiji.entity.Category;
import com.beim.ruiji.entity.Dish;
import com.beim.ruiji.entity.DishFlavor;
import com.beim.ruiji.service.CategoryService;
import com.beim.ruiji.service.DishFlavorService;
import com.beim.ruiji.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value ="/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 保存菜品数据
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info("传递的参数为：{}",dishDto.toString());
        // 保存菜品数据
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功！");
    }

    /**
     * 分页查询数据
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        // 构造分页对象
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>(page,pageSize);
        // 封装查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Dish::getName,name).orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo,queryWrapper);

        // 拷贝对象数据,忽略records
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        List<DishDto> list = pageInfo.getRecords().stream().map((item) -> {
            // 组装DishDTO数据集
            DishDto dishDto = new DishDto();
            // 将DishDTO继承自Dish中的属性填充
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            // 设置分类名称
            Category category = categoryService.getById(categoryId);
            if (category != null){
                dishDto.setCategoryName(category.getName());
            }
            return dishDto;
        }).collect(Collectors.toList());
        // 封装分页数据
        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    /**
     * 根据ID回想菜品数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> EchoById(@PathVariable("id") String id){
        DishDto dishDto = new DishDto();
        // 根据ID查询菜品数据
        Dish dish = dishService.getById(id);
        // 根据菜品ID查询菜品口味数据
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getCategoryId());
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);
        // 封装回显数据
        dishDto.setFlavors(list);
        BeanUtils.copyProperties(dish,dishDto);
        return R.success(dishDto);
    }

    /**
     * 更新数据
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> updateDishWithFlavor(@RequestBody DishDto dishDto){
        log.info("前端传递的参数为：{}",dishDto.toString());
        dishService.updateDishWithFlavor(dishDto);
        return R.success("修改成功！");
    }

}
