package com.itheima.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author lh
 * @Date 2023/7/30 9:17
 * @ 意图：
 */
@Component
@Slf4j
public class MyMetaObjectHandle implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("自动填充公共数据(insert)...");
        log.info(metaObject.toString());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
        metaObject.setValue("createUser", BaseContext.getCurrentId());
    }


    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("自动填充公共数据（update）----");
        log.info(metaObject.toString());
        long id = Thread.currentThread().getId();
        log.info("updateFill线程id为： {}",id);
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());

    }
}
