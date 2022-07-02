package com.beim.ruiji.dto;


import com.beim.ruiji.entity.Setmeal;
import com.beim.ruiji.entity.SetmealDish;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
