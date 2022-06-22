package com.beim.ruiji.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beim.ruiji.entity.ShoppingCart;
import com.beim.ruiji.mapper.ShoppingCartMapper;
import com.beim.ruiji.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
