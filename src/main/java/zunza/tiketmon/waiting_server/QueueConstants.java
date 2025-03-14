package zunza.tiketmon.waiting_server;

import lombok.Getter;

public enum QueueConstants {
	KEY_PREFIX("queue:performance:"),
	SIZE("100"),
	ENTER("enter_allowed"),
	UPDATE("queue_update");

	@Getter
	private String value;

	QueueConstants(String value) {
		this.value = value;
	}
}
