package com.nguyensao.ecommerce_layered_architecture.utils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import com.nguyensao.ecommerce_layered_architecture.constant.SecurityConstant;
import com.nguyensao.ecommerce_layered_architecture.exception.AppException;
import com.nguyensao.ecommerce_layered_architecture.model.User;
import com.nguyensao.ecommerce_layered_architecture.repository.UserRepository;

@Component
public class JwtUtil {
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final UserRepository userRepository;

    public JwtUtil(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder, UserRepository userRepository) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.userRepository = userRepository;
    }

    @Value("${jwt.secret}")
    private String jwtKey;

    /**
     * Tạo Access Token cho người dùng dựa trên email.
     */

    public String createAccessToken(String email) {
        Instant now = Instant.now();
        Instant validity = now.plus(SecurityConstant.EXPIRATION_TIME, ChronoUnit.SECONDS);
        User user = userRepository.findByEmail(email).orElse(null);
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(email)
                .claim("uuid", user.getId())
                .claim("role", user.getRole())
                .build();
        JwsHeader jwsHeader = JwsHeader.with(SecurityConstant.JWT_ALGORITHM).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claimsSet)).getTokenValue();
    }

    /**
     * Tạo Refresh Token cho người dùng.
     */
    public String createRefreshToken(String email) {
        Instant now = Instant.now();
        Instant validity = now.plus(SecurityConstant.REFRESH_TOKEN_EXP, ChronoUnit.SECONDS);
        User user = userRepository.findByEmail(email).orElse(null);
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(email)
                .claim("uuid", user.getId())
                .claim("role", user.getRole())
                .build();
        JwsHeader jwsHeader = JwsHeader.with(SecurityConstant.JWT_ALGORITHM).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claimsSet)).getTokenValue();

    }

    /**
     * Kiểm tra Refresh Token hợp lệ hay không.
     */
    public Jwt checkValidRefreshToken(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            if (jwt.getExpiresAt() == null || jwt.getExpiresAt().isBefore(Instant.now())) {
                throw new JwtException("Refresh token đã hết hạn.");
            }
            return jwt;
        } catch (JwtException e) {
            System.out.println(">>> Lỗi khi refresh token: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Giải mã token để lấy subject
     */
    public String decodedToken(String token) {
        try {
            Jwt decodedJwt = jwtDecoder.decode(token);
            return decodedJwt.getSubject();
        } catch (JwtException e) {
            throw new AppException("Token không hợp lệ hoặc đã hết hạn.");
        }
    }

    /**
     * Giải mã token để lấy claim
     */
    public String decodedTokenClaimEmail(String token) {
        try {
            Jwt decodedJwt = jwtDecoder.decode(token);
            return decodedJwt.getClaimAsString("email");
        } catch (JwtException e) {
            System.out.println(">>> Lỗi khi giải mã token: " + e.getMessage());
            return null;
        }
    }

    /**
     * Lấy thông tin người dùng hiện tại từ Security Context.
     */
    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }

    private static String extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            return springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        } else if (authentication.getPrincipal() instanceof String s) {
            return s;
        }
        return null;
    }

}
