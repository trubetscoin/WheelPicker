package com.wheelpicker.controller;

import com.wheelpicker.dto.ApiSuccessResponseDto;
import com.wheelpicker.dto.UserDto;
import com.wheelpicker.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers(@RequestParam(required = false) String query) {
        List<UserDto> users = adminService.findUsers(query);

        ApiSuccessResponseDto<List<UserDto>> success = new ApiSuccessResponseDto<>(
                HttpStatus.OK.value(),
                "Successfully retrieved the requested users",
                users
        );

        return ResponseEntity.ok(success);
    }

    @PostMapping("/ban/{userId}")
    public ResponseEntity<?> banUser(@PathVariable UUID userId) {
        String email = adminService.banUser(userId);

        ApiSuccessResponseDto<String> success = new ApiSuccessResponseDto<>(
                HttpStatus.OK.value(),
                "Successfully banned the user",
                email
        );

        return ResponseEntity.ok(success);
    }

    @PostMapping("/unban/{userId}")
    public ResponseEntity<?> unbanUser(@PathVariable UUID userId) {
        String email = adminService.unbanUser(userId);

        ApiSuccessResponseDto<String> success = new ApiSuccessResponseDto<>(
                HttpStatus.OK.value(),
                "Successfully unbanned the user",
                email
        );

        return ResponseEntity.ok(success);
    }
}
