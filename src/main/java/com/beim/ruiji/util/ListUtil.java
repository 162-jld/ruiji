package com.beim.ruiji.util;

import com.beim.ruiji.entity.Dish;
import com.beim.ruiji.entity.Setmeal;

import java.util.ArrayList;
import java.util.List;

/**
 * 集合工具类
 */
public class ListUtil {

    /**
     * 将字符串转化为集合
     *
     * @param ids
     * @return
     */
    public static List transList(String ids) {
        String[] split = ids.split(",");
        List idLids = new ArrayList();
        for (int i = 0; i < split.length; i++) {
            idLids.add(split[i]);
        }
        return idLids;
    }

    /**
     * 封装ID值与status集合，菜品管理
     *
     * @param ids
     * @param status
     * @return
     */
    public static List transEntityList(String ids, String status) {
        String[] split1 = ids.split(",");
        List<Dish> dishList = new ArrayList();
        for (int i = 0; i < split1.length; i++) {
            Dish dish = new Dish();
            dish.setId(Long.valueOf(split1[i]));
            dish.setStatus(Integer.valueOf(status));
            dishList.add(dish);
        }
        return dishList;
    }

    /**
     * 封装ID与status集合,套餐管理
     * @param ids
     * @param status
     * @return
     */
    public static List transEntityList1(String ids, String status) {
        String[] split1 = ids.split(",");
        List<Setmeal> setmealList = new ArrayList();
        for (int i = 0; i < split1.length; i++) {
            Setmeal setmeal = new Setmeal();
            setmeal.setId(Long.valueOf(split1[i]));
            setmeal.setStatus(Integer.valueOf(status));
            setmealList.add(setmeal);
        }
        return setmealList;
    }
}
