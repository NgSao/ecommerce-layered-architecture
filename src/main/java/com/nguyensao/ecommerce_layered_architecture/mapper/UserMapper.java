package com.nguyensao.ecommerce_layered_architecture.mapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.nguyensao.ecommerce_layered_architecture.dto.AddressDto;
import com.nguyensao.ecommerce_layered_architecture.dto.UserDto;
import com.nguyensao.ecommerce_layered_architecture.dto.request.AddressCreateRequest;
import com.nguyensao.ecommerce_layered_architecture.dto.request.AddressUpdateRequest;
import com.nguyensao.ecommerce_layered_architecture.dto.request.AuthRegisterRequest;
import com.nguyensao.ecommerce_layered_architecture.dto.request.UserUpdateRequest;
import com.nguyensao.ecommerce_layered_architecture.dto.response.UserCustomerResponse;
import com.nguyensao.ecommerce_layered_architecture.model.Address;
import com.nguyensao.ecommerce_layered_architecture.model.User;

@Component
public class UserMapper {

    public User toUserUpdatedRequest(UserUpdateRequest request) {
        return User.builder()
                .fullName(request.getFullName())
                .password(request.getPhone())
                .birthday(request.getBirthday())
                .gender(request.getGender())
                .build();
    }

    public User toUserRegisterRequest(AuthRegisterRequest request) {
        return User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(request.getPassword())
                .build();
    }

    public UserCustomerResponse toUserCustomerResponse(User user) {
        if (user == null)
            return null;
        Set<AddressDto> addressDtos = null;
        if (user.getAddresses() != null) {
            addressDtos = user.getAddresses().stream()
                    .map(this::addressToDto) // hoặc bạn map thủ công
                    .collect(Collectors.toSet());
        }

        return UserCustomerResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .profileImageUrl(user.getProfileImageUrl())
                .birthday(user.getBirthday())
                .role(user.getRole())
                .status(user.getStatus())
                .gender(user.getGender())
                .lastLoginDate(user.getLastLoginDate())
                .createdAt(user.getCreatedAt())
                .addresses(addressDtos)

                .build();
    }

    public UserDto userToDto(User user) {
        if (user == null)
            return null;
        return UserDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .password(user.getPassword())
                .profileImageUrl(user.getProfileImageUrl())
                .birthday(user.getBirthday())
                .gender(user.getGender())
                .role(user.getRole())
                .status(user.getStatus())
                .lastLoginDate(user.getLastLoginDate())
                .createdAt(user.getCreatedAt())
                .createdBy(user.getCreatedBy())
                .updatedAt(user.getUpdatedAt())
                .updatedBy(user.getUpdatedBy())
                .build();
    }

    public User userToEntity(UserDto dto) {
        if (dto == null)
            return null;
        return User.builder()
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .birthday(dto.getBirthday())
                .gender(dto.getGender())
                .profileImageUrl(dto.getProfileImageUrl() != null ? dto.getProfileImageUrl() : "khachhang.png")
                .lastLoginDate(dto.getLastLoginDate())
                .role(dto.getRole())
                .status(dto.getStatus())
                .build();
    }

    public List<UserDto> userToDtoList(List<User> users) {
        return users.stream().map(this::userToDto).collect(Collectors.toList());
    }

    // --------------------
    public AddressDto addressToDto(Address address) {
        if (address == null)
            return null;
        return AddressDto.builder()
                .id(address.getId())
                .fullName(address.getFullName())
                .phone(address.getPhone())
                .city(address.getCity())
                .district(address.getDistrict())
                .street(address.getStreet())
                .addressDetail(address.getAddressDetail())
                .active(address.getActive())
                .build();
    }

    public Address toAddressCreateRequest(AddressCreateRequest dto) {
        if (dto == null)
            return null;
        return Address.builder()
                .fullName(dto.getFullName())
                .phone(dto.getPhone())
                .city(dto.getCity())
                .district(dto.getDistrict())
                .street(dto.getStreet())
                .addressDetail(dto.getAddressDetail())
                .active(dto.getActive() != null ? dto.getActive() : false)
                .build();
    }

    public Address toAddresUpdatedRequest(AddressUpdateRequest dto, User user) {
        if (dto == null)
            return null;

        Address address = new Address();

        address.setId(dto.getId());
        if (dto.getFullName() != null)
            address.setFullName(dto.getFullName());
        if (dto.getPhone() != null)
            address.setPhone(dto.getPhone());
        if (dto.getCity() != null)
            address.setCity(dto.getCity());
        if (dto.getDistrict() != null)
            address.setDistrict(dto.getDistrict());
        if (dto.getStreet() != null)
            address.setStreet(dto.getStreet());
        if (dto.getAddressDetail() != null)
            address.setAddressDetail(dto.getAddressDetail());
        if (dto.getActive() != null)
            address.setActive(dto.getActive());
        address.setUser(user);

        return address;
    }

    public List<AddressDto> addressToDtoList(List<Address> addresses) {
        return addresses.stream().map(this::addressToDto).collect(Collectors.toList());
    }

}
