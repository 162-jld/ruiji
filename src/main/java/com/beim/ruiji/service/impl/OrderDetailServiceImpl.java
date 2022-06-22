package com.beim.ruiji.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beim.ruiji.entity.OrderDetail;
import com.beim.ruiji.mapper.OrderDetailMapper;
import com.beim.ruiji.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
