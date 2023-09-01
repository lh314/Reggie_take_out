package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author lh
 * @Date 2023/8/10 8:08
 * @ 意图：
 */
@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {

    void cleanAll(ShoppingCart shoppingCart);
}
