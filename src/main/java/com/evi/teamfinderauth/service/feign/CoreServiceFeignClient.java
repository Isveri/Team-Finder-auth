package com.evi.teamfinderauth.service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("core-service")
public interface CoreServiceFeignClient {

    @DeleteMapping("/api/v1/friends/")
    List<Long> removeAllFriends();

    @PutMapping("/api/v1/friends")
    ResponseEntity<Void> rollbackDelete(@RequestParam("removedIds") List<Long> removedIds);
}
