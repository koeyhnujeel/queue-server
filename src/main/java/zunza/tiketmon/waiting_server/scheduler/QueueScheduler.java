package zunza.tiketmon.waiting_server.scheduler;

import java.io.IOException;
import java.util.Objects;
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
import zunza.tiketmon.waiting_server.service.QueueService;
import zunza.tiketmon.waiting_server.service.RedisService;
import zunza.tiketmon.waiting_server.util.WebSocketSessionManager;

@Component
@RequiredArgsConstructor
public class QueueScheduler {

	private final ObjectMapper objectMapper;
	private final RedisService redisService;
	private final QueueService queueService;
	private final RedisTemplate<String, String> redisTemplate;
	private final WebSocketSessionManager webSocketSessionManager;

	@Scheduled(fixedRate = 500000)
	public void processQueue() {
		Set<String> keys = redisTemplate.keys(QueueConstants.KEY_PREFIX.getValue() + "*");

		if (keys == null || keys.isEmpty()) {
			return;
		}

		for (String key : keys) {
			String performanceId = getPerformanceId(key);
			int size = Integer.parseInt(QueueConstants.SIZE.getValue());
			Set<String> sessionIds = redisTemplate.opsForZSet().range(key, 0, size);

			if (sessionIds == null) {
				continue;
			}

			for (String sessionId : sessionIds) {
				WebSocketSession session = webSocketSessionManager.getSession(sessionId);

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

	@Scheduled(fixedRate = 1000)
	public void fetchQueueInfo() {
		Set<String> keys = redisService.scanKeys(QueueConstants.KEY_PREFIX.getValue() + "*");

		if (keys == null || keys.isEmpty()) {
			return;
		}

		for (String key : keys) {
			Set<String> sessionIds = redisTemplate.opsForZSet().range(key, 0, -1);
			if (sessionIds != null) {
				sessionIds.stream()
					.map(webSocketSessionManager::getSession)
					.filter(Objects::nonNull)
					.filter(WebSocketSession::isOpen)
					.forEach(session -> {
						int position = queueService.getQueuePosition(getPerformanceId(key), session.getId());
						try {
							session.sendMessage(new TextMessage(String.valueOf(position)));
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					});
			}
		}
	}

	private String getPerformanceId(String key) {
		return key.split(":")[2];
	}
}
