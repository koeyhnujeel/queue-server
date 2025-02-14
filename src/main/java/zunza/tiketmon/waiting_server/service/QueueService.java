package zunza.tiketmon.waiting_server.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QueueService {

	private final RedisTemplate<String, String> redisTemplate;
	private final static String KEY = "waiting_queue";

	public void addToQueue(String sessionId) {
		long timestamp = System.currentTimeMillis();
		redisTemplate.opsForZSet().add(KEY, sessionId, timestamp);
	}

	public int getQueuePosition(String sessionId) {
		Long rank = redisTemplate.opsForZSet().rank(KEY, sessionId);
		return rank == null ? -1 : rank.intValue() + 1;
	}

	public void removeFromQueue(String sessionId) {
		redisTemplate.opsForZSet().remove(KEY, sessionId);
	}
}
