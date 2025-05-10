package com.nguyensao.ecommerce_layered_architecture.service;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nguyensao.ecommerce_layered_architecture.dto.AddressDto;
import com.nguyensao.ecommerce_layered_architecture.dto.AdminUserCreateDto;
import com.nguyensao.ecommerce_layered_architecture.dto.UserAdminDto;
import com.nguyensao.ecommerce_layered_architecture.dto.UserDto;
import com.nguyensao.ecommerce_layered_architecture.event.EventType;
import com.nguyensao.ecommerce_layered_architecture.event.domain.OtpEvent;
import com.nguyensao.ecommerce_layered_architecture.event.publisher.OtpEventPublisher;
import com.nguyensao.ecommerce_layered_architecture.dto.request.AddressCreateRequest;
import com.nguyensao.ecommerce_layered_architecture.dto.request.AddressUpdateRequest;
import com.nguyensao.ecommerce_layered_architecture.dto.request.OAuth2LinkRequest;
import com.nguyensao.ecommerce_layered_architecture.dto.request.ResetPasswordRequest;
import com.nguyensao.ecommerce_layered_architecture.dto.request.RoleChangeRequest;
import com.nguyensao.ecommerce_layered_architecture.dto.request.StatusChangeRequest;
import com.nguyensao.ecommerce_layered_architecture.dto.request.UserUpdateRequest;
import com.nguyensao.ecommerce_layered_architecture.dto.request.VerifyRequest;
import com.nguyensao.ecommerce_layered_architecture.dto.response.AdminUserDto;
import com.nguyensao.ecommerce_layered_architecture.dto.response.SimplifiedPageResponse;
import com.nguyensao.ecommerce_layered_architecture.dto.response.UserCustomerResponse;
import com.nguyensao.ecommerce_layered_architecture.constant.UserConstant;
import com.nguyensao.ecommerce_layered_architecture.enums.ProviderEnum;
import com.nguyensao.ecommerce_layered_architecture.enums.RoleAuthorities;
import com.nguyensao.ecommerce_layered_architecture.enums.StatusEnum;
import com.nguyensao.ecommerce_layered_architecture.exception.AppException;
import com.nguyensao.ecommerce_layered_architecture.mapper.UserAdminMapper;
import com.nguyensao.ecommerce_layered_architecture.mapper.UserMapper;
import com.nguyensao.ecommerce_layered_architecture.model.Address;
import com.nguyensao.ecommerce_layered_architecture.model.Otp;
import com.nguyensao.ecommerce_layered_architecture.model.Provider;
import com.nguyensao.ecommerce_layered_architecture.model.User;
import com.nguyensao.ecommerce_layered_architecture.repository.AddressRepository;
import com.nguyensao.ecommerce_layered_architecture.repository.OtpRepository;
import com.nguyensao.ecommerce_layered_architecture.repository.ProviderRepository;
import com.nguyensao.ecommerce_layered_architecture.repository.UserRepository;
import com.nguyensao.ecommerce_layered_architecture.utils.GeneratePassword;
import com.nguyensao.ecommerce_layered_architecture.utils.JwtUtil;
import com.nguyensao.ecommerce_layered_architecture.utils.PasswordValidator;

@Service
public class UserService {

    private final UserMapper mapper;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;
    private final OtpEventPublisher otpEventPublisher;
    private final ProviderRepository providerRepository;
    private final FileService fileService;
    private final UserAdminMapper adminMapper;

    public UserService(UserMapper mapper, UserRepository userRepository, AddressRepository addressRepository,
            OtpRepository otpRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            TokenBlacklistService tokenBlacklistService,
            OtpEventPublisher otpEventPublisher,
            ProviderRepository providerRepository,
            FileService fileService,
            UserAdminMapper adminMapper) {
        this.mapper = mapper;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.otpRepository = otpRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
        this.otpEventPublisher = otpEventPublisher;
        this.providerRepository = providerRepository;
        this.fileService = fileService;
        this.adminMapper = adminMapper;

    }

    public String refreshToken(String token) {
        String email = jwtUtil.decodedToken(token);
        tokenBlacklistService.blacklist(token);
        String refreshToken = jwtUtil.createRefreshToken(email);
        return refreshToken;
    }

    public void forgotPassword(VerifyRequest request) {
        Otp otp = otpRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(UserConstant.OTP_NOT_FOUND));
        if (otp.getExpiresAt().isBefore(Instant.now())) {
            throw new AppException(UserConstant.OTP_EXPIRED);
        }
        if (!otp.getOtp().equals(request.getOtp())) {
            int newAttempts = otp.getAttempts() + 1;
            otp.setAttempts(newAttempts);

            if (newAttempts >= 3) {
                otpRepository.delete(otp);
                throw new AppException(UserConstant.OTP_RETRY_LIMIT_EXCEEDED);
            }
            otpRepository.save(otp);
            throw new AppException(String.format(UserConstant.OTP_INVALID_REMAINING_ATTEMPTS, (3 - newAttempts)));
        }
        User existingUser = checkUserByEmailNotActive(request.getEmail());
        String newPassword = GeneratePassword.generate();
        existingUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(existingUser);
        otpRepository.delete(otp);

        OtpEvent otpEvent = OtpEvent.builder()
                .eventType(EventType.FORGOT_PASSWORD)
                .fullName(existingUser.getFullName())
                .email(request.getEmail())
                .otp(newPassword)
                .build();
        otpEventPublisher.publishOtpEvent(otpEvent);
    }

    public void validateEmailRegister(String email) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (user.getStatus() == StatusEnum.INACTIVE) {
                throw new AppException(UserConstant.ACCOUNT_INACTIVE);
            }
            if (user.getStatus() == StatusEnum.BLOCKED) {
                throw new AppException(UserConstant.ACCOUNT_LOCKED);
            }
            throw new AppException(UserConstant.EMAIL_ALREADY_USED);
        }
    }

    // CheckEmail ch kích hoạt
    public User checkUserByEmailNotActive(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(UserConstant.EMAIL_NOT_FOUND));
        if (user.getStatus() == StatusEnum.BLOCKED) {
            throw new AppException(UserConstant.ACCOUNT_LOCKED);
        }

        return user;
    }

    // Check Email có trả về
    public User checkUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(UserConstant.EMAIL_NOT_FOUND));

        if (user.getStatus() == StatusEnum.INACTIVE) {
            throw new AppException(UserConstant.ACCOUNT_INACTIVE);
        }

        if (user.getStatus() == StatusEnum.BLOCKED) {
            throw new AppException(UserConstant.ACCOUNT_LOCKED);
        }
        return user;
    }

    // Check Email trả về
    public User checkExistsEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(UserConstant.EMAIL_NOT_FOUND));

        if (user.getStatus() == StatusEnum.ACTIVE) {
            throw new AppException(UserConstant.ACCOUNT_ACTIVE);
        }
        if (user.getStatus() == StatusEnum.BLOCKED) {
            throw new AppException(UserConstant.ACCOUNT_LOCKED);
        }
        return user;
    }

    // Check uuid
    public User findUserById(String uuid) {
        return userRepository.findById(uuid).orElseThrow(() -> new AppException(UserConstant.USER_NOT_FOUND));
    }

    // Admin
    public void createUser(AdminUserCreateDto request) {
        validateEmailRegister(request.getEmail());
        User user = adminMapper.dtoAdminToUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(StatusEnum.ACTIVE);
        user.setRole(RoleAuthorities.CUSTOMER);
        user.setProvider(ProviderEnum.LOCAL);
        userRepository.save(user);
    }

    public void changeRole(RoleChangeRequest request) {
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new AppException(UserConstant.USER_NOT_FOUND));
        user.setRole(request.getRoleAuthorities());
        userRepository.save(user);
    }

    public void changeStatus(StatusChangeRequest request) {
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new AppException(UserConstant.USER_NOT_FOUND));
        user.setStatus(request.getStatus());
        userRepository.save(user);
    }

    public List<AdminUserDto> getAllUsersOrder(String keyword) {
        List<User> users;
        if (keyword == null || keyword.trim().isEmpty()) {
            users = userRepository.findAllByStatus(StatusEnum.ACTIVE);
        } else {
            users = userRepository.searchActiveUsersByKeyword(StatusEnum.ACTIVE, keyword);
        }
        return users.stream()
                .map(adminMapper::adminToDtoUser)
                .collect(Collectors.toList());
    }

    public SimplifiedPageResponse<UserAdminDto> getAllUsers(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);
        List<UserAdminDto> userDtoList = userPage.getContent().stream()
                .map(adminMapper::userAdminToDto)
                .collect(Collectors.toList());

        Page<UserAdminDto> userDtoPage = new PageImpl<>(
                userDtoList,
                pageable,
                userPage.getTotalElements());
        return new SimplifiedPageResponse<>(userDtoPage);
    }

    public UserDto getUserById(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(UserConstant.USER_NOT_FOUND));
        return mapper.userToDto(user);
    }

    public void deleteUsers(List<String> userIds) {
        List<String> existingUserIds = userRepository.findAllById(userIds)
                .stream()
                .map(User::getId)
                .collect(Collectors.toList());

        if (existingUserIds.isEmpty()) {
            throw new AppException(UserConstant.DELETE_NOT_USERS);
        }
        userRepository.deleteAllById(existingUserIds);
    }

    // ------------------------
    public UserCustomerResponse getUserByToken() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String uuid = jwt.getClaimAsString(UserConstant.UUID);
        User user = findUserById(uuid);
        return mapper.toUserCustomerResponse(user);
    }

    public UserCustomerResponse updateAccount(UserUpdateRequest request) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String token = jwt.getSubject();
        User user = checkUserByEmail(token);
        if (user == null) {
            throw new AppException(UserConstant.USER_NOT_FOUND);
        }
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setBirthday(request.getBirthday());
        user.setGender(request.getGender());
        userRepository.save(user);
        return mapper.toUserCustomerResponse(user);

    }

    public void resetPassword(ResetPasswordRequest request) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = jwt.getSubject();
        User user = checkUserByEmail(email);

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AppException(UserConstant.PASSWORD_OLD_INCORRECT);
        }

        if (!PasswordValidator.isStrongPassword(request.getNewPassword())) {
            throw new AppException(UserConstant.PASSWORD_REQUIREMENTS_MESSAGE);

        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new AppException(UserConstant.PASSWORD_NEW_SAME_AS_OLD);
        }

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new AppException(UserConstant.PASSWORD_NEW_MISMATCH);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        OtpEvent otpEvent = OtpEvent.builder()
                .eventType(EventType.RESET_PASSWORD)
                .fullName(user.getFullName())
                .email(email)
                .otp(null)
                .build();
        otpEventPublisher.publishOtpEvent(otpEvent);

    }

    public void unlinkOAuth2Account(OAuth2LinkRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException("Không tìm thấy người dùng"));

        Provider provider = providerRepository.findById(request.getId())
                .orElseThrow(() -> new AppException("Không tìm thấy liên kết OAuth2"));

        if (!provider.getUser().getId().equals(user.getId())) {
            throw new AppException("Liên kết không thuộc người dùng này");
        }
        providerRepository.delete(provider);
    }

    public void updateAvatar(MultipartFile file) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = jwt.getSubject();

        User user = checkUserByEmail(email);
        if (user == null) {
            throw new AppException(UserConstant.USER_NOT_FOUND);
        }

        try {
            String imageUrl = fileService.uploadImage(file);
            user.setProfileImageUrl(imageUrl);
            userRepository.save(user);
        } catch (IOException e) {
        }
    }

    public AddressDto createAddress(AddressCreateRequest addressCreateRequest) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String uuid = jwt.getClaimAsString(UserConstant.UUID);
        User user = findUserById(uuid);
        Address address = mapper.toAddressCreateRequest(addressCreateRequest);
        address.setUser(user);
        if (Boolean.TRUE.equals(addressCreateRequest.getActive())) {
            user.getAddresses().forEach(a -> a.setActive(false));
            address.setActive(true);
        }

        addressRepository.save(address);
        return mapper.addressToDto(address);
    }

    public List<AddressDto> getAllAddress() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String uuid = jwt.getClaimAsString(UserConstant.UUID);
        List<Address> addressPage = addressRepository.findAllByUserId(uuid);
        return addressPage.stream()
                .map(mapper::addressToDto)
                .collect(Collectors.toList());
    }

    public void changeAddressStatus(String addressId) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String uuid = jwt.getClaimAsString(UserConstant.UUID);
        User user = findUserById(uuid);

        Address selectedAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException(UserConstant.ADDRESS_NOT_FOUND));

        if (!selectedAddress.getUser().getId().equals(user.getId())) {
            throw new AppException(UserConstant.ADDRESS_NOT_BELONG_TO_USER);
        }
        user.getAddresses().forEach(address -> address.setActive(false));
        selectedAddress.setActive(true);

        addressRepository.saveAll(user.getAddresses());
    }

    public AddressDto updateAddress(AddressUpdateRequest addressDto) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String uuid = jwt.getClaimAsString(UserConstant.UUID);
        User user = findUserById(uuid);

        Address address = addressRepository.findById(addressDto.getId())
                .orElseThrow(() -> new AppException(UserConstant.ADDRESS_NOT_FOUND));

        if (address.getUser() == null || !address.getUser().getId().equals(user.getId())) {
            throw new AppException(UserConstant.ADDRESS_NOT_BELONG_TO_USER_OR_USER_NOT_LINKED);
        }

        address = mapper.toAddresUpdatedRequest(addressDto, user);

        if (Boolean.TRUE.equals(addressDto.getActive())) {
            user.getAddresses().forEach(a -> a.setActive(false));
            address.setActive(true);
        }

        addressRepository.save(address);

        return mapper.addressToDto(address);
    }

    public void deleteAddress(String addressId) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String uuid = jwt.getClaimAsString(UserConstant.UUID);
        User user = findUserById(uuid);
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException(UserConstant.ADDRESS_NOT_FOUND));
        if (!address.getUser().getId().equals(user.getId())) {
            throw new AppException(UserConstant.ADDRESS_NOT_BELONG_TO_USER);
        }
        addressRepository.delete(address);
    }

    // public void unlinkOAuth2Account(OAuth2LinkRequest request) {
    // User user = userRepository.findByEmail(request.getEmail())
    // .orElseThrow(() -> new AppException("Không tìm thấy người dùng"));

    // UserProvider provider = userProviderRepository.findById(request.getId())
    // .orElseThrow(() -> new AppException("Không tìm thấy liên kết OAuth2"));

    // // Optional: check xem provider này có thuộc về user đó không
    // if (!provider.getUser().getId().equals(user.getId())) {
    // throw new AppException("Liên kết không thuộc người dùng này");
    // }
    // userProviderRepository.delete(provider);
    // }

}
