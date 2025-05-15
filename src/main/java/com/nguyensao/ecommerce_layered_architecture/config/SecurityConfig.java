package com.nguyensao.ecommerce_layered_architecture.config;

import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationFilter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;

import com.nguyensao.ecommerce_layered_architecture.constant.RoleConstant;
import com.nguyensao.ecommerce_layered_architecture.constant.SecurityConstant;
import com.nguyensao.ecommerce_layered_architecture.security.CustomAccessDeniedHandler;
import com.nguyensao.ecommerce_layered_architecture.security.CustomAuthenticationEntryPoint;
import com.nguyensao.ecommerce_layered_architecture.security.CustomOAuth2UserService;
import com.nguyensao.ecommerce_layered_architecture.security.JwtBlacklistFilter;
import com.nguyensao.ecommerce_layered_architecture.security.OAuth2AuthenticationSuccessHandler;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.util.Base64;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String jwtKey;

    @SuppressWarnings("deprecation")
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,
            CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
            CustomAccessDeniedHandler customAccessDeniedHandler,
            JwtBlacklistFilter jwtBlacklistFilter,
            CustomOAuth2UserService customOAuth2UserService,
            OAuth2AuthenticationSuccessHandler oauth2SuccessHandler) throws Exception {
        httpSecurity
                .csrf(c -> c.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(SecurityConstant.ADMIN_URLS)
                        .hasAnyAuthority(RoleConstant.ROLE_ADMIN, RoleConstant.ROLE_STAFF)
                        .requestMatchers(SecurityConstant.PUBLIC_URLS).permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler))
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                                .oidcUserService(customOAuth2UserService::loadUser))
                        .authorizationEndpoint(authz -> authz.baseUri(SecurityConstant.OAUTH2_AUTHORIZATION_URL))
                        .redirectionEndpoint(redir -> redir.baseUri(SecurityConstant.OAUTH2_CALLBACK_URL))
                        .successHandler(oauth2SuccessHandler))

                .addFilterBefore(jwtBlacklistFilter,
                        BearerTokenAuthenticationFilter.class)
                .formLogin(f -> f.disable())

                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(getSecretKey())
                .macAlgorithm(SecurityConstant.JWT_ALGORITHM)
                .build();
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length,
                SecurityConstant.JWT_ALGORITHM.getName());
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            String role = jwt.getClaimAsString("role");
            if (role == null) {
                return List.of();
            }
            String grantedRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;
            return List.of(new SimpleGrantedAuthority(grantedRole));
        });

        return converter;
    }

    @Bean
    public BearerTokenResolver bearerTokenResolver() {
        return new DefaultBearerTokenResolver();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
