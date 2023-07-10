package com.evi.teamfinderauth.service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "group-management-service")
public interface GroupManagementServiceFeignClient {

    @RequestMapping(method = RequestMethod.DELETE, value = "/api/v1/user-groups/all")
    ResponseEntity<Long> exitAllGroups();

}
