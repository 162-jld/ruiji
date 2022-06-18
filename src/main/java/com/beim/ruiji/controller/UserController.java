package com.beim.ruiji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.beim.ruiji.common.R;
import com.beim.ruiji.entity.User;
import com.beim.ruiji.service.UserService;
import com.beim.ruiji.util.SMSUtils;
import com.beim.ruiji.util.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 发送手机短信验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        // 获取手机号
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)){
            // 生成一个随机四位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("发送的验证码为：{}",code);
            // 调用阿里云短信验证服务完成发送短信
            SMSUtils.sendMessage("瑞吉外卖","","","");
            // 将生产的验证码保存起来，保存到session中
            session.setAttribute(phone,code);
            R.success("手机验证码发送成功！");
        }
        return R.error("手机验证码发送失败！");
    }

    /**
     * 移动端用户登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        // 获取手机号、验证码
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();
        // 判断用户输入的验证码是否正确
        String codeInSession = session.getAttribute(phone).toString();
        if (codeInSession != null && codeInSession.equals(code)){
            // 判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            if (user == null){
                // 表明为新用户，则自动完成注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user);
            return R.success(user);
        }
        return R.error("登录失败！");
    }
}
