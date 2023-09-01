package com.itheima.reggie.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lh
 * @Date 2023/7/22 16:31
 * @ 意图：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class R <T>{

        private Integer code;  // 编码：1成功。0和其他数字失败
        private String msg;  // 错误信息
        private T data; // 数据
        private Map map = new HashMap();  // 动态数据

        public static <T> R<T> success(T data) {
            R<T> r = new R<>();
//            r.code = 1;  //成功状态码
            r.setCode(1);
            r.setData(data);
            return r;
        }

        public static <T> R<T> error(String msg) {
            R<T> r = new R<>();
            r.setMsg(msg);
            r.setCode(0);
//            r.msg = msg; //设置错误信息
//            r.code = 0;  //默认失败状态码，后期我们可以根据自己的需求来设置其他状态码
            return r;
        }

        public R<T> add(String msg, String value) {
            this.map.put(msg, value);
            return this;
        }

}
