package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author lh
 * @Date 2023/8/10 14:39
 * @ 意图：
 */
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
