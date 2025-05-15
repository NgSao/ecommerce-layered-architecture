
package com.nguyensao.ecommerce_layered_architecture.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.nguyensao.ecommerce_layered_architecture.constant.ApiPathConstant;
import com.nguyensao.ecommerce_layered_architecture.constant.CorsConstant;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(ApiPathConstant.CHAT_WEBSOCKET_ENDPOINT)
                .setAllowedOrigins(CorsConstant.LOCALHOST_FRONTEND.toArray(new String[0]))
                .withSockJS();
        registry.addEndpoint(ApiPathConstant.NOTIFICATION_WEBSOCKET_ENDPOINT)
                .setAllowedOrigins(CorsConstant.LOCALHOST_FRONTEND.toArray(new String[0]))
                .withSockJS();
    }

}
