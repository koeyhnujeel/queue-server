package zunza.tiketmon.waiting_server.dto;

import lombok.Builder;
import lombok.Getter;
import zunza.tiketmon.waiting_server.QueueStatus;

@Getter
public class FetchQueueDto {
	private int queuePosition;
	private QueueStatus Status = QueueStatus.QUEUE_UPDATE;

	@Builder
	private FetchQueueDto(int queuePosition) {
		this.queuePosition = queuePosition;
	}

	public static FetchQueueDto from(int queuePosition) {
		return FetchQueueDto.builder()
			.queuePosition(queuePosition)
			.build();
	}
}
