package com.cphillipsdorsett.teamsweeper.config;

import com.cphillipsdorsett.teamsweeper.GameSocketHandler;
import com.cphillipsdorsett.teamsweeper.GameSocketInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // TODO add/remove the appropriate origins during production and
        //  development, e.g., cphillipsdorsett.com on production.
        registry.addHandler(new GameSocketHandler(), "/publish")
                .setAllowedOrigins("http://localhost:4000")
                .addInterceptors(new GameSocketInterceptor());
    }
}