package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.OrdersDto;
import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.service.OrderDetailService;
import com.itheima.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author lh
 * @Date 2023/8/10 14:45
 * @ 意图：
 */
@RestController
@RequestMapping("/order")
@Slf4j
public class OrdersController {

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private OrdersService ordersService;
@PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info(orders.toString());
        ordersService.submit(orders);
        return R.success("下单成功");
    }

    @GetMapping("/userPage")
    public R<Page> listOrders(Integer page, Integer pageSize){
        Page<Orders> ordersPage = new Page<>(page,pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>(page, pageSize);
//条件构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        Long currentId = BaseContext.getCurrentId();
        queryWrapper.eq(currentId!=null,Orders::getUserId,currentId);
        queryWrapper.orderByDesc(Orders::getOrderTime);
        Page<Orders> page1 = ordersService.page(ordersPage, queryWrapper);
        log.info("page1 {}",page1.toString());
        BeanUtils.copyProperties(ordersPage,ordersDtoPage,"records");
        log.info("ordersPage {}", ordersPage);
        log.info("ordersDtoPage {}", ordersDtoPage);
        List<OrdersDto> list=ordersPage.getRecords().stream().map((item)->{
            log.info("records {}",ordersPage.getRecords().toString());
            OrdersDto ordersDto = new OrdersDto();
            Long orderId = item.getId();
            BeanUtils.copyProperties(item,ordersDto);
            LambdaQueryWrapper<OrderDetail> queryWrapperOrderDetail = new LambdaQueryWrapper<>();
            queryWrapperOrderDetail.eq(orderId!=null,OrderDetail::getOrderId,orderId);
            List<OrderDetail> orderDetails = orderDetailService.list(queryWrapperOrderDetail);
            ordersDto.setOrderDetails(orderDetails);
            return ordersDto;
        }).collect(Collectors.toList());
        ordersDtoPage.setRecords(list);
        log.info("list:{}", list);
        return R.success(ordersDtoPage);
    }

    @GetMapping("/page")
    public R<Page> get(Integer page,Integer pageSize,String beginTime,String endTime){
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>(page, pageSize);
//        构建条件查询语句
        LambdaQueryWrapper<Orders> ordersWrapper = new LambdaQueryWrapper<>();
        ordersWrapper.orderByDesc(Orders::getOrderTime);
        ordersWrapper.gt(StringUtils.isNotEmpty(beginTime),Orders::getOrderTime,beginTime)
                .lt(StringUtils.isNotEmpty(endTime),Orders::getOrderTime,endTime);
//        分页page 方法用于执行分页查询，并将查询结果填充到 ordersPage 对象中，以满足分页需求。
        ordersService.page(ordersPage, ordersWrapper);
       List<OrdersDto> listDto = ordersPage.getRecords().stream().map((item)->{
            OrdersDto ordersDto = new OrdersDto();
            LambdaQueryWrapper<OrderDetail> orderDetailWrapper = new LambdaQueryWrapper<>();
            Long id = item.getId();
            orderDetailWrapper.eq(id!=null,OrderDetail::getOrderId,id);
            List<OrderDetail> orderDetailList = orderDetailService.list(orderDetailWrapper);
            ordersDto.setOrderDetails(orderDetailList);
            BeanUtils.copyProperties(item,ordersDto);
            return ordersDto;
//      查询orderDetails并存入orderDto
//       将分页中的内容存入ordersDto中
        }).collect(Collectors.toList());
        ordersDtoPage.setRecords(listDto);
        BeanUtils.copyProperties(ordersPage,ordersDtoPage,"records");
        log.info("listDto: {}",listDto);
        return R.success(ordersDtoPage);
    }

    @PutMapping
    public R<String> updateStatus(@RequestBody Map<String, Object> map){
        Long id = Long.valueOf((String) map.get("id"));
        Integer status = (Integer) map.get("status");
        log.info("id: {}, status: {}",id,status);
        LambdaUpdateWrapper<Orders> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(id!=null,Orders::getId,id);
        updateWrapper.set(status!=null,Orders::getStatus,status);
        ordersService.update(updateWrapper);
        return R.success("状态修改完成");
    }
}
