package com.nguyensao.ecommerce_layered_architecture.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nguyensao.ecommerce_layered_architecture.annotation.AppMessage;
import com.nguyensao.ecommerce_layered_architecture.constant.ApiPathConstant;
import com.nguyensao.ecommerce_layered_architecture.constant.AppMessageConstant;
import com.nguyensao.ecommerce_layered_architecture.dto.AddressDto;
import com.nguyensao.ecommerce_layered_architecture.dto.request.AddressCreateRequest;
import com.nguyensao.ecommerce_layered_architecture.dto.request.AddressUpdateRequest;
import com.nguyensao.ecommerce_layered_architecture.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(ApiPathConstant.API_PREFIX)
public class AddressController {
    private final UserService userService;

    public AddressController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(ApiPathConstant.ADDRESS_CREATE)
    public ResponseEntity<AddressDto> createAddress(@Valid @RequestBody AddressCreateRequest request) {
        return ResponseEntity.ok().body(userService.createAddress(request));
    }

    @GetMapping(ApiPathConstant.ADDRESS_GET_ALL)
    public ResponseEntity<List<AddressDto>> getAllAddress() {
        return ResponseEntity.ok().body(userService.getAllAddress());
    }

    @GetMapping(ApiPathConstant.ADDRESS_ACTIVATE)
    public ResponseEntity<String> changeAddressStatus(@PathVariable String addressId) {
        userService.changeAddressStatus(addressId);
        return ResponseEntity.ok().body(AppMessageConstant.changeAddressStatus);
    }

    @PostMapping(ApiPathConstant.ADDRESS_UPDATE)
    public ResponseEntity<AddressDto> updateAddressByGmail(@Valid @RequestBody AddressUpdateRequest addressDto) {
        return ResponseEntity.ok().body(userService.updateAddress(addressDto));
    }

    @AppMessage(AppMessageConstant.deleteAddress)
    @DeleteMapping(ApiPathConstant.ADDRESS_DELETE)
    public ResponseEntity<String> deleteAddress(@PathVariable String addressId) {
        userService.deleteAddress(addressId);
        return ResponseEntity.ok().body(AppMessageConstant.deleteAddress);
    }
}
