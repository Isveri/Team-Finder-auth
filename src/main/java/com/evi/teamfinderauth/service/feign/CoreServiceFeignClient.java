package com.evi.teamfinderauth.service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;

@FeignClient("core-service")
public interface CoreServiceFeignClient {

    @DeleteMapping("/api/v1/friends/")
    ResponseEntity<Void> removeAllFriends();
}
