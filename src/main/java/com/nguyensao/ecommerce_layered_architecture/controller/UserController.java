package com.nguyensao.ecommerce_layered_architecture.controller;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.nguyensao.ecommerce_layered_architecture.annotation.AppMessage;
import com.nguyensao.ecommerce_layered_architecture.dto.AdminUserCreateDto;
import com.nguyensao.ecommerce_layered_architecture.dto.UserAdminDto;
import com.nguyensao.ecommerce_layered_architecture.dto.UserDto;
import com.nguyensao.ecommerce_layered_architecture.dto.request.OAuth2LinkRequest;
import com.nguyensao.ecommerce_layered_architecture.dto.request.ResetPasswordRequest;
import com.nguyensao.ecommerce_layered_architecture.dto.request.RoleChangeRequest;
import com.nguyensao.ecommerce_layered_architecture.dto.request.StatusChangeRequest;
import com.nguyensao.ecommerce_layered_architecture.dto.request.UserUpdateRequest;
import com.nguyensao.ecommerce_layered_architecture.dto.response.AdminUserDto;
import com.nguyensao.ecommerce_layered_architecture.dto.response.SimplifiedPageResponse;
import com.nguyensao.ecommerce_layered_architecture.dto.response.UserCustomerResponse;
import com.nguyensao.ecommerce_layered_architecture.exception.AppException;
import com.nguyensao.ecommerce_layered_architecture.constant.ApiPathConstant;
import com.nguyensao.ecommerce_layered_architecture.constant.AppMessageConstant;
import com.nguyensao.ecommerce_layered_architecture.service.UserService;

@RestController
@RequestMapping(ApiPathConstant.API_PREFIX)
public class UserController {
        private final UserService userService;

        public UserController(UserService userService) {
                this.userService = userService;
        }

        // Admin
        @GetMapping(ApiPathConstant.REFRESH)
        public ResponseEntity<String> refeshTk(@RequestHeader("Authorization") String authHeader) {
                String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
                return ResponseEntity.ok().body(userService.refreshToken(token));
        }

        @AppMessage(AppMessageConstant.createUser)
        @PostMapping(ApiPathConstant.ADMIN_CREATE)
        public ResponseEntity<String> createUser(@RequestBody AdminUserCreateDto request) {
                userService.createUser(request);
                return ResponseEntity.ok().body(AppMessageConstant.createUser);
        }

        @AppMessage(AppMessageConstant.changeRole)
        @PostMapping(ApiPathConstant.CHANGE_ROLE)
        public ResponseEntity<String> changeRole(@Valid @RequestBody RoleChangeRequest request) {
                userService.changeRole(request);
                return ResponseEntity.ok().body(AppMessageConstant.changeRole);
        }

        @PostMapping(ApiPathConstant.CHANGE_STATUS)
        public ResponseEntity<String> changeStatus(@Valid @RequestBody StatusChangeRequest request) {
                userService.changeStatus(request);
                return ResponseEntity.ok().body(AppMessageConstant.changeStatus);
        }

        @GetMapping(ApiPathConstant.GET_ALL_USERS)
        public ResponseEntity<SimplifiedPageResponse<UserAdminDto>> getAllUsers(
                        @RequestParam(defaultValue = "1") int page,
                        @RequestParam(defaultValue = "10") int limit) {
                Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("id"));
                SimplifiedPageResponse<UserAdminDto> userPage = userService.getAllUsers(pageable);
                return ResponseEntity.ok().body(userPage);
        }

        @GetMapping(ApiPathConstant.GET_USER_BY_ID)
        public ResponseEntity<UserDto> getUserById(@PathVariable String id) {
                UserDto user = userService.getUserById(id);
                return ResponseEntity.ok().body(user);
        }

        @GetMapping(ApiPathConstant.GET_USER_ORDER)
        public ResponseEntity<List<AdminUserDto>> getUserOrder(
                        @RequestParam(value = "keyword", required = false) String keyword) {
                List<AdminUserDto> userOrders = userService.getAllUsersOrder(keyword);
                return ResponseEntity.ok().body(userOrders);
        }

        @AppMessage(AppMessageConstant.deleteUser)
        @DeleteMapping(ApiPathConstant.DELETE_USERS)
        public ResponseEntity<String> deleteUsers(@RequestBody List<String> userIds) {
                userService.deleteUsers(userIds);
                return ResponseEntity.ok().body(AppMessageConstant.deleteUser);
        }

        // Customer
        @GetMapping(ApiPathConstant.CUSTOMER_INFO)
        public ResponseEntity<UserCustomerResponse> getUserByToken() {
                return ResponseEntity.ok().body(userService.getUserByToken());
        }

        @PostMapping(ApiPathConstant.CUSTOMER_UPDATE)
        public ResponseEntity<UserCustomerResponse> updateAccount(@RequestBody UserUpdateRequest request) {
                return ResponseEntity.ok().body(userService.updateAccount(request));
        }

        @PostMapping(ApiPathConstant.RESET_PASSWORD)
        public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
                userService.resetPassword(request);
                return ResponseEntity.ok().body(AppMessageConstant.resetPassword);
        }

        @PostMapping(ApiPathConstant.OAUTH2_UNLINK)
        public ResponseEntity<String> unLink(@RequestBody OAuth2LinkRequest request) {
                userService.unlinkOAuth2Account(request);
                return ResponseEntity.ok("Tài khoản đã được hủy liên kết!");
        }

        @PostMapping(value = ApiPathConstant.FILE_UPLOAD_AVATAR, consumes = "multipart/form-data")
        public ResponseEntity<?> updateAvatar(@RequestParam("file") MultipartFile file) {
                try {
                        userService.updateAvatar(file);
                        return ResponseEntity.ok("Tải ảnh thanh công");
                } catch (AppException e) {
                        return ResponseEntity.badRequest().body(e.getMessage());
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body("Lỗi hệ thống: " + e.getMessage());
                }
        }

}

// Test Cookies
// @GetMapping("/public/test")
// @AppMessage("Đăng nhập tài khoản thành công.")
// public void Test() {
// Jwt jwt = (Jwt)
// SecurityContextHolder.getContext().getAuthentication().getPrincipal();
// System.out.println("Test" + jwt.getTokenValue());
// String email = jwt.getSubject();
// System.out.println("Test1" + email);
// String uuid = jwt.getClaimAsString("uuid");
// System.out.println("Test2" + uuid);

// }
