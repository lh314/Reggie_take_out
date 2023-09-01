package com.itheima.reggie.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lh
 * @Date 2023/7/26 10:40
 * @ 意图：
 */

/**
 * &#064;Configuration
 * 表名其是配置类，交由Spring ioc来管理
 */
@Configuration
public class MyBatisPlusConfig {

/**
 *  &#064;Bean代表spring管理它
 */
   @Bean
   public MybatisPlusInterceptor mybatisPlusInterceptor(){
      MybatisPlusInterceptor mybatisPlusInterceptor =new MybatisPlusInterceptor();
      mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
      return mybatisPlusInterceptor;
   }

}
