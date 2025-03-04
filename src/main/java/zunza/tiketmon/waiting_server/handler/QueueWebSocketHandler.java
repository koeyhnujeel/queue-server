package zunza.tiketmon.waiting_server.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import zunza.tiketmon.waiting_server.service.QueueService;
import zunza.tiketmon.waiting_server.util.WebSocketSessionManager;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueueWebSocketHandler extends TextWebSocketHandler {

	private final QueueService queueService;
	private final WebSocketSessionManager webSocketSessionManager;

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		String uri = session.getUri().toString();
		String performanceId = extractPerformanceId(uri);
		session.getAttributes().put("performanceId", performanceId);

		String sessionId = session.getId();
		queueService.addToQueue(performanceId, sessionId);
		webSocketSessionManager.addSession(sessionId, session);
		log.info("=================WebSocket connected=================");
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		String performanceId = session.getAttributes().get("performanceId").toString();
		String sessionId = session.getId();
		queueService.removeFromQueue(performanceId, sessionId);
		webSocketSessionManager.removeSession(sessionId);
		log.info("=================WebSocket disconnected=================");
	}

	public String extractPerformanceId(String uri) {
		String[] parts = uri.split("/");
		return parts[parts.length - 1];
	}
}
