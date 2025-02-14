package zunza.tiketmon.waiting_server.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import lombok.RequiredArgsConstructor;
import zunza.tiketmon.waiting_server.service.QueueService;

@Component
@RequiredArgsConstructor
public class QueueWebSocketHandler extends TextWebSocketHandler {

	private final QueueService queueService;
	private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		String sessionId = session.getId();
		queueService.addToQueue(sessionId);
		sessions.put(sessionId, session);
	}

	// @Override
	// public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
	// 	int position = queueService.getQueuePosition(session.getId());
	// 	session.sendMessage(new TextMessage(String.valueOf(position)));
	// }

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		String sessionId = session.getId();
		queueService.removeFromQueue(sessionId);
		sessions.remove(sessionId);
	}
}
