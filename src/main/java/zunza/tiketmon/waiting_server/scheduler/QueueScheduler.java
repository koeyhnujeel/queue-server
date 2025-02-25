package zunza.tiketmon.waiting_server.scheduler;

import java.util.Map;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import zunza.tiketmon.waiting_server.QueueConstants;
import zunza.tiketmon.waiting_server.dto.EnterDto;

@Component
@RequiredArgsConstructor
public class QueueScheduler {

	private final RedisTemplate<String, String> redisTemplate;
	private final Map<String, WebSocketSession> sessions;
	private final ObjectMapper objectMapper;
	
	@Scheduled(fixedRate = 5000)
	public void processQueue() {
		Set<String> keys = redisTemplate.keys(QueueConstants.KEY_PREFIX.getValue() + "*");

		if (keys == null || keys.isEmpty()) {
			return;
		}

		for (String key : keys) {
			String performanceId = key.split(":")[key.length() - 1];
			int size = Integer.parseInt(QueueConstants.SIZE.getValue());
			Set<String> sessionIds = redisTemplate.opsForZSet().range(key, 0, size);

			if (sessionIds == null) {
				continue;
			}

			for (String sessionId : sessionIds) {
				WebSocketSession session = sessions.get(sessionId);

				if (session != null && session.isOpen()) {
					EnterDto enterDto = new EnterDto(performanceId);

					try {
						String jsonMessage = objectMapper.writeValueAsString(enterDto);
						session.sendMessage(new TextMessage(jsonMessage));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				redisTemplate.opsForZSet().remove(key, sessionId);
			}
		}
	}
}
