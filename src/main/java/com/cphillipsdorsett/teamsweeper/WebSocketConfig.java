package com.cphillipsdorsett.teamsweeper;

import com.cphillipsdorsett.teamsweeper.game.websocket.GameSocketHandler;
import com.cphillipsdorsett.teamsweeper.game.websocket.GameSocketInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    public final GameSocketHandler gameSocketHandler;

    public WebSocketConfig(GameSocketHandler gameSocketHandler) {
        this.gameSocketHandler = gameSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // TODO add/remove the appropriate origins during production and
        //  development, e.g., add cphillipsdorsett.com on production.
        registry
            .addHandler(gameSocketHandler, "/game/publish")
            .setAllowedOrigins("http://localhost:4000")
            .addInterceptors(new GameSocketInterceptor());
    }
}