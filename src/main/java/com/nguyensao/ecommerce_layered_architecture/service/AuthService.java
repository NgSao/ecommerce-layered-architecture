package com.nguyensao.ecommerce_layered_architecture.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nguyensao.ecommerce_layered_architecture.dto.request.EmailRequest;
import com.nguyensao.ecommerce_layered_architecture.dto.request.UserLoginRequest;
import com.nguyensao.ecommerce_layered_architecture.dto.request.AuthRegisterRequest;
import com.nguyensao.ecommerce_layered_architecture.dto.request.VerifyRequest;
import com.nguyensao.ecommerce_layered_architecture.dto.response.AuthLoginResponse;
import com.nguyensao.ecommerce_layered_architecture.constant.UserConstant;
import com.nguyensao.ecommerce_layered_architecture.enums.ProviderEnum;
import com.nguyensao.ecommerce_layered_architecture.enums.RoleAuthorities;
import com.nguyensao.ecommerce_layered_architecture.enums.StatusEnum;
import com.nguyensao.ecommerce_layered_architecture.event.EventType;
import com.nguyensao.ecommerce_layered_architecture.event.domain.OtpEvent;
import com.nguyensao.ecommerce_layered_architecture.event.publisher.OtpEventPublisher;
import com.nguyensao.ecommerce_layered_architecture.exception.AppException;
import com.nguyensao.ecommerce_layered_architecture.model.Otp;
import com.nguyensao.ecommerce_layered_architecture.model.User;
import com.nguyensao.ecommerce_layered_architecture.repository.OtpRepository;
import com.nguyensao.ecommerce_layered_architecture.repository.UserRepository;
import com.nguyensao.ecommerce_layered_architecture.utils.GenerateOTP;
import com.nguyensao.ecommerce_layered_architecture.utils.GeneratePassword;
import com.nguyensao.ecommerce_layered_architecture.utils.JwtUtil;
import com.nguyensao.ecommerce_layered_architecture.utils.PasswordValidator;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;
    private final OtpEventPublisher otpEventPublisher;

    public AuthService(UserRepository userRepository,
            OtpRepository otpRepository,
            PasswordEncoder passwordEncoder, AuthenticationManagerBuilder authenticationManagerBuilder,
            JwtUtil jwtUtil,
            TokenBlacklistService tokenBlacklistService,
            OtpEventPublisher otpEventPublisher) {
        this.userRepository = userRepository;
        this.otpRepository = otpRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
        this.otpEventPublisher = otpEventPublisher;
    }

    public void registerUser(AuthRegisterRequest request) {
        validateEmailRegister(request.getEmail());
        if (!PasswordValidator.isStrongPassword(request.getPassword())) {
            throw new AppException(UserConstant.PASSWORD_REQUIREMENTS_MESSAGE);
        }
        String verificationCode = GenerateOTP.generate();
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(StatusEnum.INACTIVE)
                .role(RoleAuthorities.CUSTOMER)
                .provider(ProviderEnum.LOCAL)
                .build();
        userRepository.save(user);
        Otp otp = Otp.builder()
                .email(request.getEmail())
                .otp(verificationCode)
                .expiresAt(Instant.now().plus(UserConstant.EXPIRATION_OTP, ChronoUnit.SECONDS))
                .build();
        otpRepository.save(otp);

        OtpEvent otpEvent = OtpEvent.builder()
                .eventType(EventType.REGISTER_OTP)
                .fullName(request.getFullName())
                .email(request.getEmail())
                .otp(verificationCode)
                .build();
        otpEventPublisher.publishOtpEvent(otpEvent);
    }

    public void verifyUser(VerifyRequest request) {
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

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(UserConstant.USER_NOT_FOUND));
        user.setStatus(StatusEnum.ACTIVE);
        userRepository.save(user);
        otpRepository.delete(otp);
    }

    public void sendOtp(EmailRequest request) {
        User user = checkExistsEmail(request.getEmail());
        String optVery = GenerateOTP.generate();
        Otp otp = Otp.builder()
                .email(request.getEmail())
                .otp(optVery)
                .expiresAt(Instant.now().plus(UserConstant.EXPIRATION_OTP, ChronoUnit.SECONDS))
                .build();
        otpRepository.save(otp);
        OtpEvent otpEvent = OtpEvent.builder()
                .eventType(EventType.VERIFY_OTP)
                .fullName(user.getFullName())
                .email(request.getEmail())
                .otp(optVery)
                .build();
        otpEventPublisher.publishOtpEvent(otpEvent);
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

        if (user.getStatus() == StatusEnum.BLOCKED) {
            throw new AppException(UserConstant.ACCOUNT_LOCKED);
        }
        return user;
    }

    public AuthLoginResponse loginUser(UserLoginRequest request) {
        User user = checkUserByEmail(request.getEmail());
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                request.getEmail(), request.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String access_token = jwtUtil.createAccessToken(request.getEmail());

        AuthLoginResponse userLoginResponse = new AuthLoginResponse();
        userLoginResponse.setAccessToken(access_token);
        userLoginResponse.setEmail(request.getEmail());

        user.setLastLoginDate(Instant.now());
        userRepository.save(user);

        return userLoginResponse;
    }

    public String refreshToken(String token) {
        String email = jwtUtil.decodedToken(token);
        String refreshToken = jwtUtil.createRefreshToken(email);
        return refreshToken;
    }

    public void logOut(String token) {
        tokenBlacklistService.blacklist(token);
    }

    @Scheduled(fixedRate = 180000)
    public void deleteExpiredOtps() {
        Instant now = Instant.now();
        otpRepository.deleteAllByExpiresAtBefore(now);
    }

}
