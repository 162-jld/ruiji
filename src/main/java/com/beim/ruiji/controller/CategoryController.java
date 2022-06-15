package com.beim.ruiji.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.beim.ruiji.common.R;
import com.beim.ruiji.entity.Category;
import com.beim.ruiji.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理
 */

@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("新增的分类信息为：" + category.toString());
        categoryService.save(category);
        return R.success("新增分类成功！");
    }

    /**
     * 分类信息分页展示
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page<Category>> page(int page,int pageSize){
        // 封装分页对象
        Page<Category> pageInfo = new Page<>(page,pageSize);
        // 构造分页对象
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);
        // 查询数据
        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }


    /**
     * 根据ID删除分类信息
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long ids){
        log.info("删除分类的id为：{}",ids);
        categoryService.remove(ids);
        return R.success("删除成功！");
    }

    /**
     * 修改分类数据
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("要修改的分类为：" + category);
        categoryService.updateById(category);
        return R.success("修改成功！");
    }

    /**
     * 添加菜品分类
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> getCategoryList(Category category){
        log.info("添加菜品分类的入参为：{}",category);
        // 条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(category.getType() != null,Category::getType,category.getType());
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list();
        log.info("返回的数据为：{}",list);
        return R.success(list);
    }


}
