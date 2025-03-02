package zunza.tiketmon.waiting_server.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class WebSocketSessionManager {

	private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

	public void addSession(String sessionId, WebSocketSession session) {
		sessions.put(sessionId, session);
	}

	public WebSocketSession getSession(String sessionId) {
		return sessions.get(sessionId);
	}

	public void removeSession(String sessionId) {
		sessions.remove(sessionId);
	}
}
