package org.talon.ad.controller;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.talon.ad.exception.AdException;
import org.talon.ad.service.IUserService;
import org.talon.ad.vo.CreateUserRequest;
import org.talon.ad.vo.CreateUserResponse;

/**
 * Created by Zelong
 * On 2022/5/2
 **/

@Slf4j
@RestController
public class UserController {
    private final IUserService userService;

    @Autowired
    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create/user")
    public CreateUserResponse createUser(@RequestBody CreateUserRequest request)
        throws AdException {
        Gson gson = new Gson();
        log.info("ad-sponsor: createUser -> {}", gson.toJson(request));
        return userService.createUser(request);
    }
}
