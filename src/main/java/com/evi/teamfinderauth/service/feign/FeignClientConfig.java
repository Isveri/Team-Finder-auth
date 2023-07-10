package com.evi.teamfinderauth.service.feign;

import feign.RequestInterceptor;
import org.apache.http.entity.ContentType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import static com.evi.teamfinderauth.utils.UserDetailsHelper.getCurrentUser;
import static org.springframework.cloud.openfeign.security.OAuth2AccessTokenInterceptor.BEARER;


@Configuration
public class FeignClientConfig {


    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("Accept", ContentType.APPLICATION_JSON.getMimeType());
            requestTemplate.header(HttpHeaders.AUTHORIZATION, BEARER + " " + getCurrentUser().getToken());
        };
    }

}
