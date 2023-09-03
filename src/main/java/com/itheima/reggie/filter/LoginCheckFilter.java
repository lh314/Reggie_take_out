package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author lh
 * @Date 2023/7/23 10:10
 * @ 意图：
 */

@Slf4j
@Component
@WebFilter(filterName = "LoginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    @Autowired
    private RedisTemplate redisTemplate;
//    用于路径匹配
    public final static AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        强转为http
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
//          获取本次请求的URI
        String requestURI = request.getRequestURI();
        log.info("成功拦截{}",requestURI);
//          定义不需要拦截请求
        String [] urls =new String[]
//                        {"/employee/login"
//                        ,"/employee/logout"
//                        , "/backend/**"
//                        ,"/front1/**"};
                {
        "/employee/login",
                "/employee/logout",
                "/backend/**",
                        "/front/**",
                "/common/**",
                //对用户登陆操作放行
                "/common/**",
                "/user/login",
                "/front/page/login",
                 "/user/sendMsg"};

        boolean check = check(urls, requestURI);
        if (check){
            log.info("放行拦截{}",requestURI);
//            放行
            filterChain.doFilter(request,response);
            return;
        }
        Object employee = request.getSession().getAttribute("employee");
        System.out.println(employee);
//        判断登录状态，已登录直接放行
        if(employee!=null){
            log.info("登录账号id{}",request.getSession().getAttribute("employee"));
            Long empId = (Long) request.getSession().getAttribute("employee");
            long id = Thread.currentThread().getId();
            log.info("doFile线程id为： {}",id);
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request,response);
            return;
        }
        Object user = request.getSession().getAttribute("user");

        if(user!=null){
            log.info("登录账号id{}",request.getSession().getAttribute("user"));
            Long userId = (Long) request.getSession().getAttribute("user");
            long id = Thread.currentThread().getId();
            log.info("doFile线程id为： {}",id);
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request,response);
            return;
        }
        log.info("用户未登录，用户id{}",employee);
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
//        response.getWriter().write("NOTLOGIN");
    }
    //判断此次请求是否需要处理
//    private boolean check(String[] urls,String requestURI){
//        for (String url: urls){
//            boolean match = PATH_MATCHER.match(url, requestURI);
//            if(match){
//                return true;
//            }
//        }
//        return false;
//    }

    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                //匹配
                return true;
            }
        }
        //不匹配
        return false;
    }

}
