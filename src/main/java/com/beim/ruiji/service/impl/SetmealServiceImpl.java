package com.beim.ruiji.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beim.ruiji.entity.Setmeal;
import com.beim.ruiji.mapper.SetmealMapper;
import com.beim.ruiji.service.SetmealService;
import org.springframework.stereotype.Service;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
}
