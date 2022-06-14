package com.beim.ruiji.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beim.ruiji.entity.DishFlavor;
import com.beim.ruiji.mapper.DishFlavorMapper;
import com.beim.ruiji.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
