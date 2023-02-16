package net.miraeit.mmrc.domain.schedule.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.miraeit.mmrc.domain.schedule.dto.ScheduleModifyRequest;
import net.miraeit.mmrc.domain.schedule.dto.ScheduleRegisterRequest;
import net.miraeit.mmrc.global.entity.BaseTimeEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(
		name = "SCHEDULE_SEQ_GENERATOR",
		sequenceName = "SCHEDULE_SEQ",
		initialValue = 4,
		allocationSize = 1
)
public class Schedule extends BaseTimeEntity{

	@Id @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator = "SCHEDULE_SEQ_GENERATOR")
	private Long id;
	private String email;
	private String username;    // 등록자명
	private String title;       // 회의명
	private LocalDateTime startTime;   // 시작시간
	private LocalDateTime endTime;     // 종료시간
	private Boolean isAllDay;   // 종일 사용 여부
	private String content;     // 내용

	public Schedule(ScheduleRegisterRequest registerRequest, String email) {
		this.email = email;
		this.username = registerRequest.getUsername();
		this.title = registerRequest.getTitle();
		this.startTime = registerRequest.getStartTime();
		this.endTime = registerRequest.getEndTime();
		this.isAllDay = registerRequest.getIsAllDay();
		this.content = registerRequest.getContent();
	}

	public void changeSchedule(ScheduleModifyRequest modifyRequest) {
		this.title = modifyRequest.getTitle();
		this.startTime = modifyRequest.getStartTime();
		this.endTime = modifyRequest.getEndTime();
		this.isAllDay = modifyRequest.getIsAllDay();
		this.content = modifyRequest.getContent();
	}
}
