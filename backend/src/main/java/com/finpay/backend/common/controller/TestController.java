package com.finpay.backend.common.controller;

import com.finpay.backend.common.dto.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/v1/test")
    public ApiResponse<String> test() {
        return ApiResponse.ok(
                "OK",
                "Protected API accessed successfully"
        );
    }
}
