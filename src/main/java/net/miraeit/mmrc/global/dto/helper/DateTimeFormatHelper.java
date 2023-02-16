package net.miraeit.mmrc.global.dto.helper;

import net.miraeit.mmrc.global.property.Format;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * LocalDateTime 관련 처리
 */
@Component
public class DateTimeFormatHelper {

	public DateTimeFormatter formatter() {
		return DateTimeFormatter.ofPattern(Format.DATETIME);
	}

	public LocalDateTime getLastDateOfPreviousMonth(String condition) {
		return LocalDateTime
				.parse(condition + "-01 23:59:59", formatter())
				.minusDays(1);
	}

	public LocalDateTime getFirstDateOfNextMonth(String condition) {
		return LocalDateTime
				.parse(condition + "-01 00:00:00", formatter())
				.plusMonths(1);
	}

	public String toString(LocalDateTime dateTime) {
		return formatter().format(dateTime);
	}

	public LocalDateTime toDateTime(String dateTime) {
		return LocalDateTime.parse(dateTime, formatter());
	}
}
