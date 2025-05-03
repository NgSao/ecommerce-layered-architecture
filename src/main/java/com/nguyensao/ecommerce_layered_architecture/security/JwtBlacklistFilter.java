package com.nguyensao.ecommerce_layered_architecture.security;

import java.io.IOException;

import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nguyensao.ecommerce_layered_architecture.dto.response.DataResponse;
import com.nguyensao.ecommerce_layered_architecture.constant.SecurityConstant;
import com.nguyensao.ecommerce_layered_architecture.service.TokenBlacklistService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtBlacklistFilter extends OncePerRequestFilter {

    private final TokenBlacklistService blacklistService;
    private final BearerTokenResolver tokenResolver;

    public JwtBlacklistFilter(TokenBlacklistService blacklistService,
            BearerTokenResolver tokenResolver) {
        this.blacklistService = blacklistService;
        this.tokenResolver = tokenResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String token = tokenResolver.resolve(request);
        if (token != null && blacklistService.isBlacklisted(token)) {
            response.setContentType("application/json;charset=UTF-8");
            DataResponse<Object> res = new DataResponse<>();
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setMessage(SecurityConstant.TOKEN_REVOKED);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
