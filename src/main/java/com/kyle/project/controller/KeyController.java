package com.kyle.project.controller;


import cn.hutool.core.util.RandomUtil;
import com.kyle.kyapicommon.model.entity.User;
import com.kyle.project.common.BaseResponse;
import com.kyle.project.common.ResultUtils;
import com.kyle.project.service.UserService;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


/**
 *
 * 用户钥匙
 *
 * @author kyle
 *
 */
@RestController
@RequestMapping("/key")
public class KeyController {


    @Resource
    private UserService userService;

    private String SALT = "KyApi_";

    /**
     *
     * 获取用户个人信息
     * @param request
     * @return
     */
   @GetMapping("/get")
    public BaseResponse<User> getUser(HttpServletRequest request){

       User user = userService.getLoginUser(request);
       return ResultUtils.success(user);

   }

   @PostMapping("/modify")
    public BaseResponse<Boolean> updateSetting( HttpServletRequest request){




       User user = userService.getLoginUser(request);
       Long userId = user.getId();
       User realUser = userService.getById(userId);


       String accessKey = DigestUtils.md5DigestAsHex((SALT + System.currentTimeMillis() / 1000 + RandomUtil.randomNumbers(5) ).getBytes());
       String secretKey = DigestUtils.md5DigestAsHex((SALT + System.currentTimeMillis() / 1000 + RandomUtil.randomNumbers(8) ).getBytes());
       realUser.setAccessKey(accessKey);
       realUser.setSecretKey(secretKey);

       boolean result = userService.updateById(realUser);

       return ResultUtils.success(result);

   }








}
