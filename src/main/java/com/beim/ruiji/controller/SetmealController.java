package com.beim.ruiji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.beim.ruiji.common.R;
import com.beim.ruiji.dto.SetmealDto;
import com.beim.ruiji.entity.Setmeal;
import com.beim.ruiji.entity.SetmealDish;
import com.beim.ruiji.service.SetmealDishService;
import com.beim.ruiji.service.SetmealService;
import com.beim.ruiji.util.ListUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private SetmealService setmealService;


    /**
     * 新增套餐
     *
     * @param setmealDto
     * @return
     */
    @Transactional
    @PostMapping
    public R<String> saveSetmealWithDish(@RequestBody SetmealDto setmealDto) {
        log.info("传递的参数为：{}", setmealDto.toString());
        // 新增套餐
        setmealService.save(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        // 新增套餐与菜品关联表
        setmealDishService.saveBatch(setmealDishes);
        return R.success("新增成功！");
    }

    /**
     * 分页展示数据
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<Setmeal>> page(int page, int pageSize, String name) {
        log.info("page = {}，pageSize = {}，name = {}", page, pageSize, name);
        // 构造分页构造器
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        // 构造条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        // 构造过滤条件
        queryWrapper.like(!StringUtils.isEmpty(name), Setmeal::getName, name);
        // 构造排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        // 执行查询
        setmealService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 根据ID查询套餐数据
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> selectById(@PathVariable("id") String id) {
        log.info("传递的ID值为：{}", id);
        SetmealDto setmealDto = new SetmealDto();
        // 根据ID查询套餐数据
        Setmeal setmeal = setmealService.getById(id);
        // 根据套餐ID查询套餐菜品数据
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(id != null, SetmealDish::getSetmealId, id);
        List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper);
        // 封装回显数据
        setmealDto.setSetmealDishes(setmealDishes);
        BeanUtils.copyProperties(setmeal, setmealDto);
        log.info("返回的结果为：{}", setmealDto.toString());
        return R.success(setmealDto);
    }

    /**
     * 更新套餐
     * @param setmealDto
     * @return
     */
    @Transactional
    @PutMapping
    public R<String> updateSetmeal(@RequestBody SetmealDto setmealDto){
        log.info("传递的参数为：{}",setmealDto.toString());

        // 先更新套餐关联表的数据
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmealDto.getId() != null,SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(queryWrapper);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);

        // 再更新套餐数据
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDto,setmeal);
        setmealService.updateById(setmeal);
        return R.success("修改成功！");
    }


    /**
     * 删除套餐数据，包括单个删除以及批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @Transactional
    public R<String> delete(String ids){
        log.info("传递的参数为：{}",ids);
        // 先删除套餐菜品关联表中的数据
        List<String> idList = ListUtil.transList(ids);
        for(String setmealId : idList){
            LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(setmealId != null,SetmealDish::getSetmealId,setmealId);
            setmealDishService.remove(queryWrapper);
        }
        // 再删除套餐表中的数据
        setmealService.removeByIds(idList);
        return R.success("删除成功！");
    }

    /**
     * 更新套餐状态
     * @param ids
     * @param status
     * @return
     */
    @Transactional
    @PostMapping("/status/{status}")
    public R<String> status(String ids, @PathVariable String status) {
        log.info("传入的id值为：{}，状态值为：{}", ids, status);
        // 更新套餐表的状态
        setmealService.updateBatchById(ListUtil.transEntityList(ids, status));
        return R.success("修改状态成功！");
    }

    /**
     * 根据分类ID列表查询套餐数据
     * @param categoryId
     * @param status
     * @return
     */
    // Cacheable  注解的作用，当redis中有数据时，则直接展示redis中的数据，如果没有数据，则调用方法将数据缓存进redis中
    @Cacheable(value = "setMealCache",key = "#categoryId + '_' + #status")
    @GetMapping("/list")
    public R<List<Setmeal>> list(Long categoryId,Integer status){
        log.info("分类ID为：{}，状态为{}：",categoryId,status);
        // 封装条件
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(categoryId != null,Setmeal::getCategoryId,categoryId);
        queryWrapper.eq(status != null,Setmeal::getStatus,status);
        return R.success(setmealService.list(queryWrapper));
    }
}
