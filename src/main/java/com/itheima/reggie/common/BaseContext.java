package com.itheima.reggie.common;

import org.springframework.stereotype.Component;

/**
 * @author lh
 * @Date 2023/7/30 11:28
 * @ 意图：
 */
@Component
public class BaseContext {

    private static ThreadLocal<Long> threadLocal =  new ThreadLocal<>();
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    public static Long getCurrentId(){
      return threadLocal.get();
    }
}
