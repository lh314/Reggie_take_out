package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.mapper.EmployeeMapper;
import com.itheima.reggie.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author lh
 * @Date 2023/7/22 16:20
 * @ 意图：
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
   @Autowired
   private EmployeeMapper employeeMapper;
    @Override
    public void updateEmployee(Employee employee) {
        employeeMapper.updateEmployee(employee);
    }
}
