package com.finpay.backend.admin.controller;

import com.finpay.backend.common.dto.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @GetMapping("/test")
    public ApiResponse<Void> adminTest() {

        return ApiResponse.ok(
                "Admin access successful",
                null
        );
    }
}
