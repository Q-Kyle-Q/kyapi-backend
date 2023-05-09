package com.kyle.kyapiinterface;

import com.kyle.kyapiclientsdk.client.KyApiClient;
import com.kyle.kyapiclientsdk.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class KyapiInterfaceApplicationTests {

    @Resource
    private KyApiClient kyApiClient;

    @Test
    void contextLoads() {
        String result = kyApiClient.getNameByGet("kyle");
        User user = new User();
        user.setUsername("kyle666");
        String usernameByPost = kyApiClient.getUsernameByPost(user);
        System.out.println(result);
        System.out.println(usernameByPost);
    }

}
