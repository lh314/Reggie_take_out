package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author lh
 * @Date 2023/7/30 17:15
 * @ 意图：
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
