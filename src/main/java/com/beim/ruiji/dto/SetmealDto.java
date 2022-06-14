package com.beim.ruiji.dto;


import com.beim.ruiji.entity.Setmeal;
import com.beim.ruiji.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
