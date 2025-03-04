package com.wheel.wheelPicker.controller;

import com.wheel.wheelPicker.dto.UserCreateDto;
import com.wheel.wheelPicker.dto.UserDto;
import com.wheel.wheelPicker.dto.UserLoginDto;
import com.wheel.wheelPicker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
//@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String homePage() {
        return "home"; // Returns "home.html" or "home.jsp" (depending on the view resolver)
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup"; // Returns "home.html" or "home.jsp" (depending on the view resolver)
    }

    @PostMapping("/signup")
    public ResponseEntity<?> createUser(@RequestBody @Valid UserCreateDto userCreateDto, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        UserDto savedUser = userService.createUser(userCreateDto);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // Returns "home.html" or "home.jsp" (depending on the view resolver)
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody @Valid UserLoginDto userLoginDto, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

        boolean isAuthenticated = userService.LoginUser(userLoginDto);

        if (isAuthenticated) {
            return ResponseEntity.ok("User logged in successfully");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }

}