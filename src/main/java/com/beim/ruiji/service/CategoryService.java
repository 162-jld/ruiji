package com.beim.ruiji.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.beim.ruiji.entity.Category;

public interface CategoryService extends IService<Category> {

    /**
     * 删除分类信息
     * @param id
     */
    void remove(Long id);

}
