package com.itheima.reggie.service;

/**
 * @author lh
 * @Date 2023/9/3 9:32
 * @ 意图：
 */
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class RedisTest {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisTest(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    public void doSomethingWithRedis() {
        redisTemplate.opsForValue().set("key", "value");
        String value = (String) redisTemplate.opsForValue().get("key");
        System.out.println(value);
    }
}
