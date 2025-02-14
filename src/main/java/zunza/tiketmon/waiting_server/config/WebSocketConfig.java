package zunza.tiketmon.waiting_server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import zunza.tiketmon.waiting_server.handler.QueueWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(queueWebSocketHandler(), "/waiting-ws")
			.setAllowedOrigins("*");
	}

	@Bean
	public WebSocketHandler queueWebSocketHandler() {
		return new QueueWebSocketHandler();
	}
}
