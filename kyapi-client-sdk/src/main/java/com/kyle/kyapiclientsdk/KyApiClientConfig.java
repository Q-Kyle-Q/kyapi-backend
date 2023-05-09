package com.kyle.kyapiclientsdk;

import com.kyle.kyapiclientsdk.client.KyApiClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("kyapi.client")
@Data
@ComponentScan
public class KyApiClientConfig {
    private String accessKey;

    private String secretKey;

    @Bean
    public KyApiClient kyApiClient() {
        return new KyApiClient(accessKey, secretKey);
    }
}
