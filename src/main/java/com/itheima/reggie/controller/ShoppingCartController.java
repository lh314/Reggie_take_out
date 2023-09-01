package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author lh
 * @Date 2023/8/10 8:56
 * @ 意图：
 */
@RequestMapping("/shoppingCart")
@Slf4j
@RestController
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        log.info(shoppingCart.toString());

//        设置用户id
        shoppingCart.setUserId(BaseContext.getCurrentId());
//        判断所添加菜是否已存在,存在number+1
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        Long dishId = shoppingCart.getDishId();
        if(dishId!=null){
            queryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }else{
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        ShoppingCart oneBefore = shoppingCartService.getOne(queryWrapper);
        if(oneBefore!=null){
            Integer number = oneBefore.getNumber();
            oneBefore.setNumber(number+1);
            shoppingCartService.updateById(oneBefore);
        }else {
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            oneBefore=shoppingCart;
        }

//        不存在再添加,number默认1
        return R.success(oneBefore);
    }

    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        log.info("查看购物车");
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }


    @DeleteMapping("/clean")
    public R<String> clean(){
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
//        shoppingCartService.remove(queryWrapper);
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(currentId);
        shoppingCartService.cleanAll(shoppingCart);

        return R.success("购物车已清空");
    }

    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        if (dishId!=null){
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
            ShoppingCart one = shoppingCartService.getOne(queryWrapper);
            one.setNumber(one.getNumber()-1);
            if(one.getNumber()>0){
                shoppingCartService.updateById(one);
            }else if(one.getNumber()==0){
                shoppingCartService.removeById(one.getId());
            }
            return R.success(one);
        }
        if(setmealId!=null){
            queryWrapper.eq(ShoppingCart::getSetmealId,setmealId);
            ShoppingCart shoppingCart1 = shoppingCartService.getOne(queryWrapper);
            shoppingCart1.setNumber(shoppingCart1.getNumber()-1);
            Integer number = shoppingCart1.getNumber();
            if(number>0){
                shoppingCartService.updateById(shoppingCart1);
            }else if(number==0){
                shoppingCartService.removeById(shoppingCart1.getId());
            }
            return R.success(shoppingCart1);
        }
        return R.error("系统异常");
    }

}
