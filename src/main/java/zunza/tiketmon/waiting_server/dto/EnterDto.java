package zunza.tiketmon.waiting_server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EnterDto {
	private boolean success = true;
	private String redirectUrl;
	private int statusCode = 200;

	public EnterDto(String performanceId) {
		this.redirectUrl = "/api/performances/" + performanceId + "/schedules";
	}
}
