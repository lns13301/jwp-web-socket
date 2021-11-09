package com.websocket.lns13301.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // 웹 소켓 메시지 처리 활성화
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(final MessageBrokerRegistry registry) {
        // 메세지 브로커는 아래 주소로 시작하는 주소의 Subscriber 에게 메시지를 전달하는 역할을 한다.
        registry.enableSimpleBroker("/sub", "/subscribe", "/queue", "/topic");
        // 클라이언트가 서버로 메시지를 보낼 때 아래 주소를 붙인다.
        registry.setApplicationDestinationPrefixes("/pub", "/publish", "/app");
    }

    @Override
    public void registerStompEndpoints(final StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-connection") // 클라이언트가 웹 소켓 요청을 하고 싶을 때 해당 End point 로 요청을 보낸다.
            .setAllowedOrigins("*")
            .withSockJS(); // fallback
    }
}
