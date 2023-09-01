package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lh
 * @Date 2023/8/6 16:09
 * @ 意图：
 */
@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetMealController {

    @Autowired
    private SetmealService setMealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishService dishService;
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("新增套餐信息{}",setmealDto);
        setMealService.setWithMeatDish(setmealDto);
        return R.success("新添套餐成功");
    }
@GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize, String name){
    Page<Setmeal> pageInfo = new Page<>(page, pageSize);
    Page<SetmealDto> pageDto = new Page<>(page, pageSize);
    LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(StringUtils.isNotBlank(name),Setmeal::getName,name);
    queryWrapper.orderByDesc(Setmeal::getUpdateTime);
    setMealService.page(pageInfo,queryWrapper);
    BeanUtils.copyProperties(pageInfo,pageDto,"records");
    List<Setmeal> records = pageInfo.getRecords();

    List<SetmealDto> list = records.stream().map((item)->{
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(item,setmealDto);
        Long categoryId = item.getCategoryId();
        Category category = categoryService.getById(categoryId);
        setmealDto.setCategoryName(category.getName());
        return setmealDto;
    }).collect(Collectors.toList());
    pageDto.setRecords(list);

    return R.success(pageDto);
    }

    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("需要删除的套餐ids {}",ids);
        setMealService.deleteWithDish(ids);
        return R.success("勾选删除套餐成功");
    }

    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        Long categoryId = setmeal.getCategoryId();
        queryWrapper.eq(categoryId!=null,Setmeal::getCategoryId,categoryId);
        queryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,1);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> setmealList = setMealService.list(queryWrapper);
        return R.success(setmealList);
    }

    @GetMapping("/dish/{id}")
    public R<List<DishDto>> showSetmeatDish(@PathVariable Long id){
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> records = setmealDishService.list(queryWrapper);
       List<DishDto>  dtoList =records.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Dish dish = dishService.getById(item.getDishId());
            Long categoryId = dish.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);
            BeanUtils.copyProperties(dish,dishDto);
            return dishDto;
        }).collect(Collectors.toList());
        return R.success(dtoList);
    }

    @GetMapping("{id}")
    public R<SetmealDto> get(@PathVariable Long id){
        SetmealDto setmealDto = new SetmealDto();
        Setmeal setmeal = setMealService.getById(id);
        BeanUtils.copyProperties(setmeal,setmealDto);
        LambdaQueryWrapper<SetmealDish> setMealDishQueryWrapper = new LambdaQueryWrapper<>();
        setMealDishQueryWrapper.eq(id!=null,SetmealDish::getSetmealId,id);
        List<SetmealDish> setmealDishes = setmealDishService.list(setMealDishQueryWrapper);
        setmealDto.setSetmealDishes(setmealDishes);
        Category category = categoryService.getById(setmeal.getCategoryId());
        String categoryName = category.getName();
        setmealDto.setCategoryName(categoryName);
        return R.success(setmealDto);
    }

    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable Integer status,@RequestParam List<Long> ids){
        LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Setmeal::getId,ids);
        updateWrapper.set(Setmeal::getStatus,status);
        setMealService.update(updateWrapper);
        return R.success("修改套餐起售状态成功");
    }

    @PutMapping
    public R<SetmealDto> update(@RequestBody SetmealDto setmealDto){
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
//        清理套餐中彩品
        Long setmealDtoId = setmealDto.getId();
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDtoId);
        setmealDishService.remove(queryWrapper);


        setmealDishes= setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDtoId);
            return item;
        }).collect(Collectors.toList());
//        更新套餐数据
        setMealService.updateById(setmealDto);
        setmealDishService.saveBatch(setmealDishes);
        return R.success(setmealDto);
    }


}
