package com.beim.ruiji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.beim.ruiji.common.BaseContext;
import com.beim.ruiji.common.R;
import com.beim.ruiji.entity.ShoppingCart;
import com.beim.ruiji.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(value = "/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加菜品或套餐到购物车
     * @param shoppingCart
     * @return
     */
    @Transactional
    @PostMapping("/add")
    public R<String> add(@RequestBody ShoppingCart shoppingCart){
        log.info("传递的数据为：{}",shoppingCart);
        Long userId = BaseContext.getCurrentId();
        Long dishId = shoppingCart.getDishId();

        // 设置用户ID
        shoppingCart.setUserId(userId);
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,shoppingCart.getUserId());
        // 判断要添加的是菜品还是套餐
        if (dishId != null){
            // 表明是要添加菜品到购物车
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            // 表明是要添加套餐到购物车
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        // 查询数据库中是否已经存在相同类型的菜品或者套餐
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);
        if (cartServiceOne != null){
            cartServiceOne.setNumber(cartServiceOne.getNumber() + 1);
            shoppingCartService.updateById(cartServiceOne);
        }else {
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
        }
        return R.success("添加成功！");
    }


    /**
     * 查询购物车数据
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        log.info("查询购物车数据...");
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        queryWrapper.orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(queryWrapper);
        log.info("购物车中的数据为：{}",shoppingCartList);
        return R.success(shoppingCartList);
    }

    /**
     * 取消菜品数据
     * @param shoppingCart
     * @return
     */
    @Transactional
    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart){
        log.info("传递的参数为：{}",shoppingCart);
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        // 判断要取消的是菜品还是套餐
        if (dishId != null){
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);
        cartServiceOne.setNumber(cartServiceOne.getNumber() - 1);
        shoppingCartService.updateById(cartServiceOne);
        return R.success("取消成功！");
    }

    /**
     * 清空购物车数据
     * @return
     */
    @Transactional
    @DeleteMapping("/clean")
    public R<String> clean(){
        log.info("清空购物车中的数据...");
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartService.remove(queryWrapper);
        return R.success("清空成功！");
    }

}
