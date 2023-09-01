package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Orders;

/**
 * @author lh
 * @Date 2023/8/10 14:40
 * @ 意图：
 */
public interface OrdersService extends IService<Orders> {

    void submit(Orders orders);
}
