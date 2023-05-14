package com.kyle.kyapiinterface.controller;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.kyle.kyapiclientsdk.client.KyApiClient;
import com.kyle.kyapiclientsdk.model.User;
import com.kyle.kyapiclientsdk.utils.SignUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Random;


/**
 * 名称 API
 *
 * @author kyle
 */
@RestController
@RequestMapping("/name")
public class NameController {

    @Resource
    private KyApiClient kyApiClient;

    @GetMapping("/get")
    public String getNameByGet(String name, HttpServletRequest request) {
        System.out.println(request.getHeader("kyle"));
        return "GET 你的名字是" + name;
    }

    @PostMapping("/post")
    public String getNameByPost(@RequestParam String name) {
        return "POST 你的名字是" + name;
    }

    @PostMapping("/user")
    public String getUsernameByPost(@RequestBody User user, HttpServletRequest request) {
        // 这里将其注释掉，逻辑在gateway中进行，且这里的ak和sk是写死而非在数据库中查，所以在前端不同用户调用时会报错
//        String accessKey = request.getHeader("accessKey");
//        String nonce = request.getHeader("nonce");
//        String timestamp = request.getHeader("timestamp");
//        String sign = request.getHeader("sign");
//        String body = request.getHeader("body");
//
//        // todo 实际情况应该是去数据库中查是否已分配给用户
//        if (!accessKey.equals("kyle")) {
//            throw new RuntimeException("无权限");
//        }
//        if (Long.parseLong(nonce) > 10000) {
//            throw new RuntimeException("无权限");
//        }
//
//        // todo 时间和当前时间不能超过 5 分钟
////        if (timestamp) {
////
////        }
//
//        // todo 实际情况中是从数据库中查出 secretKey
//        String serverSign = SignUtils.genSign(body, "abcdefgh");
//        if (!sign.equals(serverSign)) {
//            throw new RuntimeException("无权限");
//        }

        // todo 调用次数 + 1 invokeCount

        String result = "POST 用户名字是" + user.getUsername();
        return result;
    }


    /**
     * 随机输出一句冷笑话
     *
     * @return 接口封装的返回值，格式为{"状态码":"返回信息"}，并将结果返回给 SDK，交由 SDK 进行解析
     */
    @GetMapping("/randomJoke")
    public String randomJoke() {
        HttpRequest request = HttpRequest.get("https://api.btstu.cn/yan/api.php");
        HttpResponse response = request.execute();
        return response.body();
    }

    /**
     * 随机返回二次元图片
     *
     * @return 接口封装的返回值，格式为{"状态码":"返回信息"}，并将结果返回给 SDK，交由 SDK 进行解析
     */
    @GetMapping("/randomACGPictures")
    public String randomACGPictures() {
        String[] images = new String[]{
                "https://gulimall-kyle.oss-cn-hangzhou.aliyuncs.com/ctzs.jpg",
                "https://gulimall-kyle.oss-cn-hangzhou.aliyuncs.com/purple.jpg",
                "https://gulimall-kyle.oss-cn-hangzhou.aliyuncs.com/desktop.png",
        };
        Random random = new Random();
        int index = random.nextInt(3);
        return images[index];
    }
}
