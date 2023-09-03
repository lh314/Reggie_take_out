package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.Utils.MailUtil;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.mapper.UserMapper;
import com.itheima.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author lh
 * &#064;Date  2023/8/7 15:18
 * @ 意图：
 */
@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) throws MessagingException {
        String phone = user.getPhone();
        if(StringUtils.isNotEmpty(phone)){
//            随机生成验证码
            String code = MailUtil.achieveCode();
            log.info("验证码code={}",code);
            session.setAttribute(phone,code);

//            用redis缓存
            redisTemplate.opsForValue().set("phone",code,5, TimeUnit.MINUTES);
            log.info("redis存储phone对应code={}", redisTemplate.opsForValue().get("phone"));
            log.info("session存储phone对应code={}",session.getAttribute(phone));
//            发送短信
            MailUtil.sendTestMail(phone,code);
            return R.success("已发送验证码");
        }
        return R.error("验证码发送失败");
    }
@PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();
        Object sessionAttribute = session.getAttribute(phone);
        if (sessionAttribute!=null){
//            String sessionCode = session.getAttribute(phone).toString();
            String redisCode = (String)redisTemplate.opsForValue().get("phone");
            if(code.equals(redisCode)){
//                LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
//                queryWrapper.eq(User::getPhone,phone);
//                User user = userService.getOne(queryWrapper);

                User userTest = new User();
                userTest.setPhone(phone);
                User user = userMapper.getOneByUser(userTest);

                if(user==null){
                    user=new User();
                    user.setPhone(phone);
                    user.setStatus(1);
//                    userService.save(user);
                    userMapper.saveUser(user);
            }
                redisTemplate.delete("phone");
            session.setAttribute("user",user.getId());
            return R.success(user);
        }
    }
        return R.error("登录失败");
    }

    @PostMapping("/loginout")
    public R<String> loginout(HttpSession session){
        session.removeAttribute("user");
        return R.success("退出登录成功");
    }

}
