package com.beim.ruiji.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beim.ruiji.entity.Dish;
import com.beim.ruiji.mapper.DishMapper;
import com.beim.ruiji.service.DishService;
import org.springframework.stereotype.Service;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
}
