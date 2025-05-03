package com.nguyensao.ecommerce_layered_architecture.security;

import jakarta.servlet.http.HttpServletRequest;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.nguyensao.ecommerce_layered_architecture.enums.ProviderEnum;
import com.nguyensao.ecommerce_layered_architecture.enums.RoleAuthorities;
import com.nguyensao.ecommerce_layered_architecture.enums.StatusEnum;
import com.nguyensao.ecommerce_layered_architecture.exception.AppException;
import com.nguyensao.ecommerce_layered_architecture.model.User;
import com.nguyensao.ecommerce_layered_architecture.model.Provider;

import com.nguyensao.ecommerce_layered_architecture.repository.ProviderRepository;
import com.nguyensao.ecommerce_layered_architecture.repository.UserRepository;
import com.nguyensao.ecommerce_layered_architecture.utils.JwtUtil;

@Component
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    private final ProviderRepository userProviderRepository;
    private final OidcUserService oidcUserService;
    private final JwtUtil jwtUtil;

    public CustomOAuth2UserService(UserRepository userRepository, ProviderRepository userProviderRepository,
            JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.userProviderRepository = userProviderRepository;
        this.oidcUserService = new OidcUserService();
        this.jwtUtil = jwtUtil;
    }

    // Facebook
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        System.out.println(">>> CustomOAuth2UserService loadUser (OAuth2) được gọi");
        OAuth2User oauth2User = super.loadUser(userRequest);
        return processUser(userRequest, oauth2User);
    }

    // Google
    public OidcUser loadUser(OidcUserRequest userRequest) {
        System.out.println(">>> CustomOAuth2UserService loadUser (OIDC) được gọi");
        OidcUser oidcUser = oidcUserService.loadUser(userRequest);
        return (OidcUser) processUser(userRequest, oidcUser);
    }

    private OAuth2User processUser(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String providerId;
        String email;
        String name;
        String avatarUrl = null;
        if ("google".equals(registrationId)) {
            providerId = oauth2User.getAttribute("sub");
            email = oauth2User.getAttribute("email");
            if (email == null)
                throw new AppException("Không thể lấy email từ Google");
            name = oauth2User.getAttribute("name");
            avatarUrl = oauth2User.getAttribute("picture");
        } else if ("facebook".equals(registrationId)) {
            providerId = oauth2User.getAttribute("id");
            email = oauth2User.getAttribute("email");
            if (email == null)
                throw new AppException("Không thể lấy email từ Facebook");
            name = oauth2User.getAttribute("name");
            Object pictureObj = oauth2User.getAttribute("picture");
            if (pictureObj instanceof Map<?, ?>) {
                Map<?, ?> pictureData = (Map<?, ?>) ((Map<?, ?>) pictureObj).get("data");
                if (pictureData != null) {
                    avatarUrl = pictureData.get("url").toString();
                }
            }
        } else {
            throw new AppException("Nhà cung cấp OAuth2 không được hỗ trợ: " + registrationId);
        }

        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                    .getRequest();
            String token = null;

            if (request.getCookies() != null) {
                for (var cookie : request.getCookies()) {
                    if (cookie.getName().equals("_tk")) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }
            // System.out.println(">>> BATDAUNE ");

            if (token != null) {
                // Có token → lấy email hiện tại từ token
                String jwtEmail = jwtUtil.decodedToken(token);
                var existingUser = userRepository.findByEmail(jwtEmail);
                if (existingUser.isPresent()) {
                    User user = existingUser.get();
                    String provider = registrationId.toUpperCase();
                    boolean alreadyLinked = userProviderRepository.existsByProviderAndProviderId(provider, providerId);

                    if (!alreadyLinked) {
                        Provider userProvider = Provider.builder()
                                .user(user)
                                .provider(provider)
                                .providerId(providerId)
                                .active(false)
                                .build();
                        userProviderRepository.save(userProvider);
                        // System.out.println(">>> Đã thêm liên kết nhà cung cấp " + provider);
                    } else {
                        // System.out.println(">>> Nhà cung cấp " + provider + " đã liên kết, không thêm
                        // lại.");

                    }

                    return buildOAuth2User(user, oauth2User);
                }
            }
            final String finalAvatarUrl = avatarUrl;
            final ProviderEnum providerEnum = ProviderEnum.valueOf(registrationId);
            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> {
                        User newUser = User.builder()
                                .fullName(name)
                                .email(email)
                                .profileImageUrl(finalAvatarUrl)
                                .password("")
                                .role(RoleAuthorities.CUSTOMER)
                                .provider(providerEnum)
                                .status(StatusEnum.INACTIVE)
                                .lastLoginDate(Instant.now())

                                .build();
                        User savedUser = userRepository.saveAndFlush(newUser);
                        Provider userProvider = Provider.builder()
                                .user(savedUser)
                                .provider(registrationId.toUpperCase())
                                .providerId(providerId)
                                .active(true)
                                .build();
                        userProviderRepository.save(userProvider);
                        return savedUser;
                    });

            return buildOAuth2User(user, oauth2User);

        } catch (Exception e) {
            throw new AppException("Lỗi khi xử lý người dùng: " + e.getMessage());
        }
    }

    private OAuth2User buildOAuth2User(User user, OAuth2User oauth2User) {
        if (oauth2User instanceof OidcUser) {
            OidcUser oidcUser = (OidcUser) oauth2User;
            return new DefaultOidcUser(
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole())),
                    oidcUser.getIdToken(),
                    oidcUser.getUserInfo(),
                    "email");
        } else {
            return new org.springframework.security.oauth2.core.user.DefaultOAuth2User(
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole())),
                    oauth2User.getAttributes(),
                    "email");
        }
    }

}
