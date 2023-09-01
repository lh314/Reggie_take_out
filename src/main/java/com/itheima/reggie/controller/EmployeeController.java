package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.tomcat.util.digester.Digester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

/**
 * @author lh
 * @Date 2023/7/22 16:23
 * @ 意图：
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @PostMapping("login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
//        获取用户输入的密码
        String password = employee.getPassword();
//        MD5加密
        password = DigestUtils.md5DigestAsHex(password.getBytes());
//        创建LambdaQueryWrapper对象，用于构建查询条件，用于查询数据库内此用户
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
//        使用用户名作为查询条件
//        Employee::getUsername 是一个 Java 8 Lambda 表达式，用于获取 Employee 对象的用户名字段的值。
//        employee.getUsername() 是用于获取实际 employee 对象的用户名的值。
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
//        getOne() 方法是执行查询并返回满足查询条件的单个结果的方法
        Employee emp = employeeService.getOne(queryWrapper);
        // 检查是否找到了匹配的用户信息
        if (emp == null) {
            return R.error("无此用户");
        }
//  检查密码是否正确
        if (!emp.getPassword().equals(password)) {
            return R.error("密码错误");
        }

// 检查用户是否被封禁
        if (emp.getStatus() .equals(0)) {
            return R.error("用户被封禁");
        }

// 登录成功，存储用户ID到会话中
        request.getSession().setAttribute("employee", emp.getId());

// 返回登录成功的响应，携带用户信息
        return R.success(emp);

    }

    @PostMapping("logout")
    public R<String> logout(HttpServletRequest request){
//        清理session
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    @PostMapping
    public R<String> addUser(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工信息，{}",employee.toString());
//        设置默认密码
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        Long id = (Long)request.getSession().getAttribute("employee");
//        employee.setUpdateUser(id);
//        employee.setCreateUser(id);
//        存入数据库
        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize, String name){
        log.info("page={}, pageSize={}, name={}",page,pageSize,name);
//        构造分页构造器
        Page<Employee> pageInfo = new Page<>(page,pageSize);
//        构造查询条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件（当我们没有输入name时，就相当于查询所有了）
        queryWrapper.like(StringUtils.isNotBlank(name),Employee::getName,name);
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
//        queryWrapper.like(!(name==null&&"".equals(name)),Employee::getName,name);
//并对查询的结果进行降序排序，根据更新时间
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    @PutMapping
    public R<String> update(HttpServletRequest request ,@RequestBody Employee employee){
        log.info(employee.toString());
//        Long id = (Long)request.getSession().getAttribute("employee");
//        employee.setUpdateUser(id);
//        employee.setUpdateTime(LocalDateTime.now());
        //获取线程id
        long id = Thread.currentThread().getId();
        log.info("update的线程id为：{}", id);
//        employeeService.updateById(employee);
        employeeService.updateEmployee(employee);
        return R.success("员工信息修改成功");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息..");
        Employee employee = employeeService.getById(id);
        if(employee!=null){
            return R.success(employee);
        }
        return R.error("修改员工失败");
    }
}
