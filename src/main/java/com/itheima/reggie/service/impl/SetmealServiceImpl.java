package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lh
 * @Date 2023/8/1 10:18
 * @ 意图：
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setMealDishService;
    @Override
    @Transactional
    public void setWithMeatDish(SetmealDto setmealDto) {
        this.save(setmealDto);
        List<SetmealDish> setMealDishes = setmealDto.getSetmealDishes();
            setMealDishes = setMealDishes.stream().map((item) -> {
                item.setSetmealId(setmealDto.getId());
                return item;
            }).collect(Collectors.toList());
        setMealDishService.saveBatch(setMealDishes);
    }

    @Override
    public void deleteWithDish(List<Long> ids) {
//        查询待删套餐及是否出于停售状态
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        int count = this.count(queryWrapper);
        if(count>0){
            throw new CustomException("套餐在售，需停售方能删除");
        }
//        删除套餐
        this.removeByIds(ids);
//        删除套餐菜品关系
        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getSetmealId,ids);
        setMealDishService.remove(queryWrapper1);
    }

}
