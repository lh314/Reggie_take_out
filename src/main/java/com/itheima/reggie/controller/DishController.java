package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.impl.DishServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author lh
 * @Date 2023/8/5 17:48
 * @ 意图：
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService  dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info("菜品：{}",dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        //        清理缓存
        String key = "dish_"+dishDto.getCategoryId()+"_"+dishDto.getStatus();
        redisTemplate.delete(key);
        return R.success("新增菜品成功");
    }

    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize, String name){
//        创建分页构造器，使用给定的页码和每页数量
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> pageDto = new Page<>(page, pageSize);

//        创建条件查询器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.like(StringUtils.isNotBlank(name),Dish::getName,name);
//         添加条件，按照Category类的"sort"字段进行升序排序
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        // 执行分页查询，使用categoryService进行查询，将查询结果保存在pageInfo对象
        dishService.page(pageInfo, queryWrapper);
//        拷贝
        BeanUtils.copyProperties(pageInfo,pageDto,"records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list =  records.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category!=null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        pageDto.setRecords(list);
        return R.success(pageDto);
    }

    @GetMapping("{id}")
    public R<DishDto> getByIdWithFlavor(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> updateWithFlavor(@RequestBody DishDto dishDto){
        log.info("修改菜品信息 {}",dishDto.toString());
        dishService.updateWithFlavor(dishDto);
//        清理缓存
        String key = "dish_"+dishDto.getCategoryId()+"_"+dishDto.getStatus();
        redisTemplate.delete(key);
        return R.success("修改菜品成功");
    }

   /* @GetMapping("/list")
    public R<List<Dish>> get(Dish dish){
        Long categoryId = dish.getCategoryId();
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(categoryId!=null,Dish::getCategoryId,categoryId);
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishList = dishService.list(queryWrapper);
        return R.success(dishList);
    }*/

    @GetMapping("/list")
    public R<List<DishDto>> get(DishDto dishDto){
        List<DishDto> dishDtoList = null;
        String key = "dish_"+dishDto.getCategoryId()+"_"+dishDto.getStatus();
        dishDtoList= (List<DishDto>) redisTemplate.opsForValue().get(key);
        if(dishDtoList!=null){
            return R.success(dishDtoList);
        }
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        Long categoryId = dishDto.getCategoryId();
        queryWrapper.eq(categoryId!=null, Dish::getCategoryId,categoryId);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishList = dishService.list(queryWrapper);
        log.info("查询到的菜单消息：{}",dishList);
        dishDtoList = dishList.stream().map((item)->{
            DishDto dto = new DishDto();
            BeanUtils.copyProperties(item,dto,"categoryId");
            LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
            Long itemCategoryId = item.getCategoryId();
            if(categoryId!=null){
                Category category = categoryService.getById(itemCategoryId);
                String categoryName = category.getName();
                dto.setCategoryName(categoryName);
            }
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> flavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            flavorLambdaQueryWrapper.eq(dishId!=null,DishFlavor::getDishId,dishId);
            List<DishFlavor> dishFlavors = dishFlavorService.list(flavorLambdaQueryWrapper);
            dto.setFlavors(dishFlavors);
            return dto;
        }).collect(Collectors.toList());
//        添加flavors属性
        redisTemplate.opsForValue().set(key,dishDtoList,2, TimeUnit.HOURS);
        return R.success(dishDtoList);
    }

    @PostMapping("/status/{status}")
    public R<String> statusStop( @PathVariable Integer status,@RequestParam List<Long> ids){
        log.info("status={},ids={}",status,ids);
        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(ids!=null,Dish::getId,ids);
        updateWrapper.set(Dish::getStatus,status);
        dishService.update(updateWrapper);
        //        清理缓存
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);
        return R.success("菜品售卖与停售状态修改成功");
    }

    @DeleteMapping
    public R<String> removes(@RequestParam List<Long> ids){
//        批量删除如何删
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids!=null,Dish::getId,ids);
        queryWrapper.eq(Dish::getStatus,1);
        int count = dishService.count(queryWrapper);
        if(count>=1){
            throw new RuntimeException("有菜品仍起售中，无法下架");
        }
            dishService.removeByIds(ids);
            return R.success("下架删除成功");

    }

}
