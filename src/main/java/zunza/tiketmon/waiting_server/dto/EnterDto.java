package zunza.tiketmon.waiting_server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import zunza.tiketmon.waiting_server.QueueStatus;

@Getter
@AllArgsConstructor
public class EnterDto {
	private String redirectUrl;
	private QueueStatus status = QueueStatus.ENTER_ALLOWED;

	public EnterDto(String performanceId) {
		this.redirectUrl = "/api/performances/" + performanceId + "/schedules";
	}
}
