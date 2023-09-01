package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.mapper.OrdersMapper;
import com.itheima.reggie.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author lh
 * @Date 2023/8/10 14:42
 * @ 意图：
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private UserService userService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;
    @Transactional
    @Override
    public void submit(Orders orders) {
//        获取当前用户id
        Long userId = BaseContext.getCurrentId();
//        查询当前用户购物车
        LambdaQueryWrapper<ShoppingCart> shoppingCartQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartQueryWrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(shoppingCartQueryWrapper);
        if(shoppingCarts==null||shoppingCarts.size()==0){
            throw new CustomException("购物车为空，无法结算");
        }
//        查询地址数据
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if(addressBook==null){
            throw new CustomException("用户地址信息有误，无法下单");
        }
        User user = userService.getById(userId);
        long orderId = IdWorker.getId();
        AtomicInteger amount = new AtomicInteger(0);

        //向订单细节表设置属性
        List<OrderDetail> orderDetails= shoppingCarts.stream().map((item)->{
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setImage(item.getImage());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setAmount(item.getAmount());
            orderDetail.setName(item.getName());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());
//        向订单表设置参数
        orders.setId(orderId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserId(userId);
        orders.setStatus(2);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setUserName(user.getName());
        orders.setAddress(
                (addressBook.getProvinceName()==null?null:addressBook.getProvinceName())
                        +(addressBook.getCityName()==null?null:addressBook.getCityName())
                +(addressBook.getDistrictName()==null?null:addressBook.getDistrictName())
                +(addressBook.getDetail()==null?null:addressBook.getDetail())
        );
//        保存到订单表
        ordersService.save(orders);

//        保存到到明细表
        orderDetailService.saveBatch(orderDetails);
//        清空购物车
        shoppingCartService.remove(shoppingCartQueryWrapper);
    }
}
