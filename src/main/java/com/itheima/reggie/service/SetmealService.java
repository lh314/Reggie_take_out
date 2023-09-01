package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;

import java.util.List;

/**
 * @author lh
 * @Date 2023/8/1 10:16
 * @ 意图：
 */
public interface SetmealService extends IService<Setmeal> {
    /**
     * 新增套餐
     */
    void setWithMeatDish(SetmealDto setmealDto);

    /**
     * 删除套餐
     */
    void deleteWithDish(List<Long>ids);
}
