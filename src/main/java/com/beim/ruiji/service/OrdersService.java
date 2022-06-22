package com.beim.ruiji.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.beim.ruiji.entity.Orders;

public interface OrdersService extends IService<Orders> {

    /**
     * 下订单支付
     * @param orders
     */
    void submit(Orders orders);
}
