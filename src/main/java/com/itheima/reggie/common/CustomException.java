package com.itheima.reggie.common;

/**
 * @author lh
 * @Date 2023/8/2 18:12
 * @ 意图：
 */

public class CustomException extends RuntimeException{
    public CustomException(String smg){
        super(smg);
    }
}
