package zunza.tiketmon.waiting_server.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import zunza.tiketmon.waiting_server.QueueConstants;

@Service
@RequiredArgsConstructor
public class RedisService {

	private final RedisTemplate<String, String> redisTemplate;

	public Boolean zAdd(String key, String value, double score) {
		return redisTemplate.opsForZSet().add(key, value, score);
	}

	public Set<String> zRange(String key, long start, long end) {
		return redisTemplate.opsForZSet().range(key, start, end);
	}

	public Long zRemove(String key, Object... values) {
		return redisTemplate.opsForZSet().remove(key, values);
	}

	public Long zRank(String key, Object o) {
		return redisTemplate.opsForZSet().rank(key, o);
	}

	public Set<String> scanKeys(String pattern) {
		Set<String> keys = new HashSet<>();

		Object execute = redisTemplate.execute((RedisConnection connection) -> {
			ScanOptions options = ScanOptions.scanOptions()
				.match(QueueConstants.KEY_PREFIX.getValue() + "*")
				.count(100)
				.build();
			try (Cursor<byte[]> cursor = connection.keyCommands().scan(options)) {
				cursor.forEachRemaining(key -> keys.add(new String(key)));
			}
			return null;
		});
		return keys;
	}
}
