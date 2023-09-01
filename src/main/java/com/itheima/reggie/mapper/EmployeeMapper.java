package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author lh
 * @Date 2023/7/22 16:14
 * @ 意图：
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

    void updateEmployee(Employee employee);
}
