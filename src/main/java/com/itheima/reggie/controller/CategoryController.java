package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author lh
 * @Date 2023/7/30 17:24
 * @ 意图：
 */
@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> add(@RequestBody Category category){
        log.info("新增类别: {}",category);
        categoryService.save(category);
        return R.success("新增类别成功");
    }

    @GetMapping("/page")
    public R<Page> page (Integer page,Integer pageSize){
//        创建分页构造器，使用给定的页码和每页数量
        Page<Category> pageInfo = new Page<>(page, pageSize);
//        创建条件查询器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
//       添加条件，按照Category类的"sort"字段进行升序排序
        queryWrapper.orderByAsc(Category::getSort);
//      // 执行分页查询，使用categoryService进行查询，将查询结果保存在pageInfo对象中
        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    @DeleteMapping
    public R<String> delete(Long id){
        log.info("将被删除分类信息的id：{}",id);
        categoryService.remove(id);
        return R.success("分类信息删除成功");
    }

    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改分类信息为: {}",category);
        categoryService.updateById(category);
        return R.success("修改分类信息成功");
    }

    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq((category.getType()!=null),Category::getType,category.getType());
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> categoryList = categoryService.list(queryWrapper);
        return R.success(categoryList);
    }

}
