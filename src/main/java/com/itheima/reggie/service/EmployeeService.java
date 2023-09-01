package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.mapper.EmployeeMapper;

/**
 * @author lh
 * @Date 2023/7/22 16:18
 * @ 意图：
 */
public interface EmployeeService extends IService<Employee>{

    void updateEmployee(Employee employee);
}
