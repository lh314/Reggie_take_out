package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author lh
 * @Date 2023/8/7 15:14
 * @ 意图：
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    List<User> getAllUser();

    User getOneByUser(User user);

    void saveUser(User user);
}
