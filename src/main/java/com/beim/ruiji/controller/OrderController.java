package com.beim.ruiji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.beim.ruiji.common.BaseContext;
import com.beim.ruiji.common.R;
import com.beim.ruiji.entity.Orders;
import com.beim.ruiji.service.OrderDetailService;
import com.beim.ruiji.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping(value = "/order")
public class OrderController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 下订单支付
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单数据为：{}",orders);
        ordersService.submit(orders);
        return R.success("支付成功！");
    }

    /**
     * 查询用户订单列表
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> userPage(Integer page, Integer pageSize){
        Long userId = BaseContext.getCurrentId();
        // 构造分页对象
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId,userId);
        ordersService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }
}
