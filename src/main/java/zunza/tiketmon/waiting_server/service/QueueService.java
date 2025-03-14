package zunza.tiketmon.waiting_server.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import zunza.tiketmon.waiting_server.QueueConstants;

@Service
@RequiredArgsConstructor
public class QueueService {

	private final RedisService redisService;

	public void addToQueue(String performanceId, String sessionId) {
		long timestamp = System.currentTimeMillis();
		String key = QueueConstants.KEY_PREFIX.getValue() + performanceId;
		redisService.zAdd(key, sessionId, timestamp);
	}

	public int getQueuePosition(String performanceId, String sessionId) {
		Long rank = redisService.zRank(QueueConstants.KEY_PREFIX.getValue() + performanceId, sessionId);
		return rank == null ? -1 : rank.intValue() + 1;
	}

	public void removeFromQueue(String performanceId, String sessionId) {
		String key = QueueConstants.KEY_PREFIX.getValue() + performanceId;
		redisService.zRemove(key, sessionId);
	}
}
