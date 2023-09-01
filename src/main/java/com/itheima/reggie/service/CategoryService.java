package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Category;

/**
 * @author lh
 * @Date 2023/7/30 17:16
 * @ 意图：
 */
public interface CategoryService extends IService<Category> {

    /**
     * 根据分类id删除分类
     * @param id
     */
    public void remove(Long id);
}
