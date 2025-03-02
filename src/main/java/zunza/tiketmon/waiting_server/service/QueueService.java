package zunza.tiketmon.waiting_server.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import zunza.tiketmon.waiting_server.QueueConstants;

@Service
@RequiredArgsConstructor
public class QueueService {

	private final RedisTemplate<String, String> redisTemplate;

	public void addToQueue(String performanceId, String sessionId) {
		long timestamp = System.currentTimeMillis();
		String key = QueueConstants.KEY_PREFIX.getValue() + performanceId;
		redisTemplate.opsForZSet().add(key, sessionId, timestamp);
	}

	// public int getQueuePosition(String sessionId) {
	// 	Long rank = redisTemplate.opsForZSet().rank(QueueConstants.KEY.getValue(), sessionId);
	// 	return rank == null ? -1 : rank.intValue() + 1;
	// }

	public void removeFromQueue(String performanceId, String sessionId) {
		String key = QueueConstants.KEY_PREFIX.getValue() + performanceId;
		redisTemplate.opsForZSet().remove(key, sessionId);
	}
}
