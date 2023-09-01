package com.itheima.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;


/**
 * @author lh
 * @Date 2023/7/24 15:50
 * @ 意图：
 */
@Slf4j
@RestControllerAdvice(annotations={RestController.class, Controller.class})
public class GlobalExceptionHandle {
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandle(@RequestBody SQLIntegrityConstraintViolationException ex){
        log.info(ex.getMessage());
        if(ex.getMessage().contains("Duplicate entry")){
            String[] s = ex.getMessage().split(" ");
            String username = s[2];
            return R.error("用户名"+username+"已存在");
        }
        return R.error("未知错误");
    }

    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandle(CustomException ex){
        log.info(ex.getMessage());
        return R.error(ex.getMessage());
    }
}
