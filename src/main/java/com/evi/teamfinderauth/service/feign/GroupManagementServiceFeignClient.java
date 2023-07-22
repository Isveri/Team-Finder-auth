package com.evi.teamfinderauth.service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "group-management-service")
public interface GroupManagementServiceFeignClient {

    @RequestMapping(method = RequestMethod.DELETE, value = "/api/v1/user-groups/all")
    String exitAllGroups();

    @PutMapping(value = "/api/v1/user-groups/")
    ResponseEntity<Void> rollbackExit(@RequestBody String groupBackupJSON);
}
