package com.wheelpicker.controller;

import com.wheelpicker.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/exceptionHandler/ok")
    public String exceptionHandlerOkEndpoint() {
        return "OK";
    }

    @GetMapping("/exceptionHandler/throws")
    public String exceptionHandlerThrowsEndpoint() {
        throw new RuntimeException("Simulated exception");
    }

    @GetMapping("/jwt")
    public String jwtTestEndpoint() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        return user.getEmail();
    }

    @GetMapping("/isUserBanned")
    public String isUserBannedTestEndpoint() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        return user.getEmail();
    }

    @GetMapping("/originCheck")
    public String originCheckEndpoint(@RequestHeader("Origin") String origin) {
        return origin;
    }
}