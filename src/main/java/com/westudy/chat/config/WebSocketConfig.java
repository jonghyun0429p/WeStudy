package com.westudy.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 메시지 브로커 (STOMP) 채널 설정
        // /topic: 브로드캐스팅(1:N) 용도
        config.enableSimpleBroker("/topic");
        
        // 클라이언트에서 메시지를 보낼 때 사용하는 prefix
        // 클라이언트가 /app/chat/{studyId} 로 메시지를 보내면 @MessageMapping이 받음
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 웹소켓 연결 엔드포인트 설정
        registry.addEndpoint("/ws-stomp")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
