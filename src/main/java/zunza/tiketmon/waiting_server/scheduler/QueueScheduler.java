package zunza.tiketmon.waiting_server.scheduler;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import zunza.tiketmon.waiting_server.QueueConstants;
import zunza.tiketmon.waiting_server.dto.EnterDto;
import zunza.tiketmon.waiting_server.dto.FetchQueueDto;
import zunza.tiketmon.waiting_server.service.QueueService;
import zunza.tiketmon.waiting_server.service.RedisService;
import zunza.tiketmon.waiting_server.util.WebSocketSessionManager;

@Component
@RequiredArgsConstructor
public class QueueScheduler {

	private final ObjectMapper objectMapper;
	private final RedisService redisService;
	private final QueueService queueService;
	private final WebSocketSessionManager webSocketSessionManager;

	@Scheduled(fixedRate = 5000)
	public void processQueue() {
		Set<String> keys = redisService.scanKeys(QueueConstants.KEY_PREFIX.getValue() + "*");

		if (keys == null || keys.isEmpty()) {
			return;
		}

		keys.forEach(this::processQueueForKey);
	}

	public void processQueueForKey(String key) {
		String performanceId = getPerformanceId(key);
		int size = Integer.parseInt(QueueConstants.SIZE.getValue());
		Set<String> sessionIds = redisService.zRange(key, 0, size);

		if (sessionIds == null || sessionIds.isEmpty()) {
			return;
		}

		sessionIds.stream()
			.map(webSocketSessionManager::getSession)
			.filter(Objects::nonNull)
			.filter(WebSocketSession::isOpen)
			.forEach(session -> {
				try {
					String jsonMessage = objectMapper.writeValueAsString(new EnterDto(performanceId));
					session.sendMessage(new TextMessage(jsonMessage));
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					redisService.zRemove(key, session.getId());
				}
			});
	}

	@Scheduled(fixedRate = 1000)
	public void fetchQueueInfo() {
		Set<String> keys = redisService.scanKeys(QueueConstants.KEY_PREFIX.getValue() + "*");

		if (keys == null || keys.isEmpty()) {
			return;
		}

		for (String key : keys) {
			Set<String> sessionIds = redisService.zRange(key, 0, -1);
			if (sessionIds != null) {
				sessionIds.stream()
					.map(webSocketSessionManager::getSession)
					.filter(Objects::nonNull)
					.filter(WebSocketSession::isOpen)
					.forEach(session -> {
						int position = queueService.getQueuePosition(getPerformanceId(key), session.getId());
						try {
							String fetchQueueJson = objectMapper.writeValueAsString(FetchQueueDto.from(position));
							session.sendMessage(new TextMessage(fetchQueueJson));
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
