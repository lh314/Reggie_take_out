package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author lh
 * @Date 2023/8/1 10:10
 * @ 意图：
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
