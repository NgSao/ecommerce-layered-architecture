package com.nguyensao.ecommerce_layered_architecture.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nguyensao.ecommerce_layered_architecture.annotation.AppMessage;
import com.nguyensao.ecommerce_layered_architecture.dto.request.AuthRegisterRequest;
import com.nguyensao.ecommerce_layered_architecture.dto.request.EmailRequest;
import com.nguyensao.ecommerce_layered_architecture.dto.request.UserLoginRequest;
import com.nguyensao.ecommerce_layered_architecture.dto.request.VerifyRequest;
import com.nguyensao.ecommerce_layered_architecture.dto.response.AuthLoginResponse;
import com.nguyensao.ecommerce_layered_architecture.constant.ApiPathConstant;
import com.nguyensao.ecommerce_layered_architecture.constant.AppMessageConstant;
import com.nguyensao.ecommerce_layered_architecture.constant.UserConstant;
import com.nguyensao.ecommerce_layered_architecture.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(ApiPathConstant.API_PREFIX)
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(ApiPathConstant.AUTH_REGISTER)
    public ResponseEntity<String> registerUser(@Valid @RequestBody AuthRegisterRequest request) {
        authService.registerUser(request);
        return ResponseEntity.ok().body(UserConstant.REGISTER_SUCCESS);
    }

    @AppMessage(AppMessageConstant.loginAuth)
    @PostMapping(ApiPathConstant.AUTH_LOGIN)
    public ResponseEntity<AuthLoginResponse> loginAuth(@Valid @RequestBody UserLoginRequest request) {
        return ResponseEntity.ok().body(authService.loginUser(request));
    }

    @GetMapping(ApiPathConstant.LOGOUT)
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        authService.logOut(token);
        return ResponseEntity.ok().body(AppMessageConstant.logout);
    }

    @PostMapping(ApiPathConstant.AUTH_VERIFY)
    public ResponseEntity<String> verifyUser(@Valid @RequestBody VerifyRequest request) {
        authService.verifyUser(request);
        return ResponseEntity.ok().body(UserConstant.VERIFY_SUCCESS);
    }

    @AppMessage(AppMessageConstant.sendOtp)
    @PostMapping(ApiPathConstant.AUTH_SEND_OTP)
    public ResponseEntity<String> sendOtp(@Valid @RequestBody EmailRequest request) {
        authService.sendOtp(request);
        return ResponseEntity.ok().body(UserConstant.VERIFY_CODE_SENT);
    }

    @AppMessage(AppMessageConstant.verifyPassword)
    @PostMapping(ApiPathConstant.AUTH_FORGOT_PASSWORD)
    public ResponseEntity<String> verifyPassword(@Valid @RequestBody VerifyRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok().body(UserConstant.VERIFY_PASSWORD_RESET_SUCCESS);
    }

}
