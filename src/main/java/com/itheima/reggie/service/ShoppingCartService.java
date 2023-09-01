package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;

/**
 * @author lh
 * @Date 2023/8/10 8:10
 * @ 意图：
 */


public interface ShoppingCartService extends IService<ShoppingCart> {

 void cleanAll(ShoppingCart shoppingCart);
}
