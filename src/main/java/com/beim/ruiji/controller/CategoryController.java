package com.beim.ruiji.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.beim.ruiji.common.R;
import com.beim.ruiji.entity.Category;
import com.beim.ruiji.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

        // 执行删除语句
        categoryService.removeById(ids);

        return R.success("删除成功！");
    }









}
