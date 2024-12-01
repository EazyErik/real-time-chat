package chat.websocket;

import chat.service.MessageService;
import chat.service.UserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final MessageService messageService;
    private final UserService userService;

    // Constructor injection
    public WebSocketConfig(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new WebSocketHandler(messageService,userService), "/ws").setAllowedOrigins("*");
    }


//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry config) {
//        config.enableSimpleBroker("/ws"); // Enable simple in-memory broker for testing
//        config.setApplicationDestinationPrefixes(""); // Prefix for messages sent to @MessageMapping endpoints
//    }
//
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/ws").withSockJS(); // Endpoint for clients to connect
//    }
}

