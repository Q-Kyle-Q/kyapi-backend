package com.kyle.kyapiclientsdk.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.kyle.kyapiclientsdk.model.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static com.kyle.kyapiclientsdk.utils.SignUtils.genSign;


/**
 *  调用第三方接口的客户端
 *
 * @author kyle
 */
public class KyApiClient {

    private static final String GATEWAY_HOST = "http://localhost:8090";

    private String accessKey;

    private String secretKey;

    public KyApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public String getNameByGet(String name) {
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        String result = HttpUtil.get(GATEWAY_HOST + "/api/name/get/", paramMap);
        System.out.println(result);
        return result;
    }

    public String getNameByPost(String name) {
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        String result = HttpUtil.post(GATEWAY_HOST + "/api/name/post/", paramMap);
        System.out.println(result);
        return result;
    }

    private Map<String, String> getHeaderMap(String body) {
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("accessKey", accessKey);
        // 一定不能直接发送
//        hashMap.put("secretKey", secretKey);
        hashMap.put("nonce", RandomUtil.randomNumbers(4));
        try {
            // 防止中文乱码
            hashMap.put("body", URLEncoder.encode(body,"utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        hashMap.put("sign", genSign(body, secretKey));
        if(body.equals("kyle2")) {
            hashMap.put("Content-Type", "image/*");
        }
        return hashMap;
    }

    public /*String*/ImmutablePair<Integer, String> getUsernameByPost(User user) {
        String json = JSONUtil.toJsonStr(user);
        HttpResponse httpResponse = HttpRequest.post(GATEWAY_HOST + "/api/name/user")
                .addHeaders(getHeaderMap(json))
                .body(json)
                .execute();
        System.out.println(httpResponse.getStatus());
        String result = httpResponse.body();
        System.out.println(result);
//        return result;
        return ImmutablePair.of(httpResponse.getStatus(), httpResponse.body());
    }


    /**
     * 随机输出一句冷笑话
     */
    public ImmutablePair<Integer, String> randomJoke() {
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HttpResponse httpResponse = HttpRequest.get(GATEWAY_HOST + "/api/name/randomJoke")
                .addHeaders(getHeaderMap("kyle"))
                        .body("kyle")
                        .execute();
        String result = httpResponse.body();
        System.out.println(result);
        return ImmutablePair.of(httpResponse.getStatus(), result);
    }

    /**
     * 随机返回二次元图片
     */
    public ImmutablePair<Integer, String> randomACGPictures() {
        HttpResponse httpResponse = HttpRequest.get(GATEWAY_HOST + "/api/name/randomACGPictures")
                .addHeaders(getHeaderMap("kyle2"))
                .body("kyle")
                .execute();
        System.out.println(httpResponse.getStatus());
        String result = httpResponse.body();
        System.out.println(result);
        return ImmutablePair.of(httpResponse.getStatus(), httpResponse.body());
    }

}
