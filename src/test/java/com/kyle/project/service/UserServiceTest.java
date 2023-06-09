package com.kyle.project.service;

import com.kyle.kyapicommon.model.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * 用户服务测试
 *
 * @author kyle
 */
@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    void testAddUser() {
        User user = new User();
        boolean result = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(result);
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        boolean result = userService.updateById(user);
        Assertions.assertTrue(result);
    }

    @Test
    void testDeleteUser() {
        boolean result = userService.removeById(1L);
        Assertions.assertTrue(result);
    }

    @Test
    void testGetUser() {
        User user = userService.getById(1L);
        Assertions.assertNotNull(user);
    }

    @Test
    void userRegister() {
        String userAccount = "kyle";
        String userPassword = "";
        String checkPassword = "123456";
        String userPhone = "13245678901";
        try {
            long result = userService.userRegister(userAccount, userPassword, checkPassword, userPhone);
            Assertions.assertEquals(-1, result);
            userAccount = "ky";
            result = userService.userRegister(userAccount, userPassword, checkPassword, userPhone);
            Assertions.assertEquals(-1, result);
            userAccount = "kyle";
            userPassword = "123456";
            result = userService.userRegister(userAccount, userPassword, checkPassword, userPhone);
            Assertions.assertEquals(-1, result);
            userAccount = "ky le";
            userPassword = "12345678";
            result = userService.userRegister(userAccount, userPassword, checkPassword, userPhone);
            Assertions.assertEquals(-1, result);
            checkPassword = "123456789";
            result = userService.userRegister(userAccount, userPassword, checkPassword, userPhone);
            Assertions.assertEquals(-1, result);
            userAccount = "dogkyle";
            checkPassword = "12345678";
            result = userService.userRegister(userAccount, userPassword, checkPassword, userPhone);
            Assertions.assertEquals(-1, result);
            userAccount = "kyle";
            result = userService.userRegister(userAccount, userPassword, checkPassword, userPhone);
            Assertions.assertEquals(-1, result);
        } catch (Exception e) {

        }
    }
}