package net.miraeit.mmrc.domain.schedule.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.miraeit.mmrc.domain.schedule.entity.Schedule;
import net.miraeit.mmrc.global.property.Format;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResponse {

	private Long id;            // 회의 id
	private String username;    // 사용자명
	private String title;       // 회의명

	@JsonFormat(pattern = Format.DATETIME)
	private LocalDateTime startTime;   // 시작시간

	@JsonFormat(pattern = Format.DATETIME)
	private LocalDateTime endTime;     // 종료시간

	private Boolean isAllDay;   // 종일 사용 여부
	private String content;     // 내용

	public ScheduleResponse(Schedule schedule) {
		this.id = schedule.getId();
		this.username = schedule.getUsername();
		this.title = schedule.getTitle();
		this.startTime = schedule.getStartTime();
		this.endTime = schedule.getEndTime();
		this.isAllDay = schedule.getIsAllDay();
		this.content = schedule.getContent();
	}
}
