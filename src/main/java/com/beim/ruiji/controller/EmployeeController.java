package com.beim.ruiji.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.beim.ruiji.common.R;
import com.beim.ruiji.entity.Employee;
import com.beim.ruiji.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;


@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {


    @Autowired
    private EmployeeService employeeService;


    /**
     * 员工登录
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> admin(HttpServletRequest request, @RequestBody Employee employee) {
        // 1、加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 2、根据用户提交的username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        // 3、如果没有查询到结果则返回登录失败
        if (emp == null) {
            return R.error("用户不存在！");
        }

        // 4、密码对比
        if (!emp.getPassword().equals(password)) {
            return R.error("密码错误！");
        }

        // 5、查看员工状态，如果员工账号被冻结，则返回被禁用结果
        if (emp.getStatus() == 0){
            return R.error("账号已被禁用！");
        }

        // 6、登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());

        return R.success(emp);
    }

    /**
     * 员工退出登录
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功！");
    }

    /**
     * 添加员工信息
     * @param employee
     * @param session
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Employee employee, HttpSession session){
        log.info("添加的员工信息：{}",employee.toString());

        // 设置密码
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        // 设置创建时间、更新时间
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//
//        // 设置创建人、更新人
//        employee.setCreateUser((Long) session.getAttribute("employee"));
//        employee.setUpdateUser((Long) session.getAttribute("employee"));

        // 添加入库
        boolean save = employeeService.save(employee);
        if (save){
            log.info("添加成功！");
            return R.success("添加成功！");
        }

        log.info("添加失败！");
        return R.error("添加失败！");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<Employee>> page(int page,int pageSize,String name){
        log.info("page = {}，pageSize = {}，name = {}",page,pageSize,name);

        // 构造分页构造器
        Page<Employee> pageInfo = new Page<>(page,pageSize);

        // 构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        // 构造过滤条件
        queryWrapper.like(!StringUtils.isEmpty(name),Employee::getName,name);
        // 构造排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        // 执行查询
        employeeService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 员工数据修改
     * @param session
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpSession session,@RequestBody Employee employee){
        log.info(employee.toString());

        // 封装employee数据
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser((Long) session.getAttribute("employee"));
        employee.setStatus(employee.getStatus());

        // 执行更新操作
        employeeService.updateById(employee);
        return R.success("员工信息修改成功！");
    }


    /**
     * 根据员工ID查询员工信息
     * @param id
     * @return
     */
    @RequestMapping("/{id}")
    public R<Employee> findById(@PathVariable("id") Long id){
        log.info("接收到的员工ID为：" + id);

        Employee byId = employeeService.getById(id);

        return R.success(byId);

    }
}
