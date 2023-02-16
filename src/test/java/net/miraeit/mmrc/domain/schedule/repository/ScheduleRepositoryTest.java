package net.miraeit.mmrc.domain.schedule.repository;

import net.miraeit.mmrc.domain.schedule.entity.Schedule;
import net.miraeit.mmrc.global.dto.helper.DateTimeFormatHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class ScheduleRepositoryTest {

	@Autowired ScheduleRepository scheduleRepository;
	@Autowired DateTimeFormatHelper formatHelper;
	StopWatch stopWatch = new StopWatch();

	@Test
	@DisplayName("월별 일정 조회")
	void findSchedule() {
		// given
		String yearMonth = "2022-05";

		// when
		List<Schedule> scheduleOfMonth = scheduleRepository.findScheduleOfMonth(
				formatHelper.getLastDateOfPreviousMonth(yearMonth),
				formatHelper.getFirstDateOfNextMonth(yearMonth)
		);

		// then
		assertThat(scheduleOfMonth.size()).isGreaterThan(0);
	}

	@Test
	@DisplayName("시간으로 일정 조회_결과 있음")
	void findScheduleByTime_1() {
		// given
		LocalDateTime startTime = formatHelper.toDateTime("2022-05-01 09:30:00");
		LocalDateTime endTime = formatHelper.toDateTime("2022-05-01 11:30:00");

		// when
		List<Schedule> scheduleList = scheduleRepository.findScheduleByTime(startTime, endTime);

		// then
		assertThat(scheduleList.size()).isGreaterThan(0);
	}

	@Test
	@DisplayName("시간으로 일정 조회_결과 없음")
	void findScheduleByTime_2() {
		// given
		LocalDateTime startTime = formatHelper.toDateTime("2022-05-01 13:00:00");
		LocalDateTime endTime = formatHelper.toDateTime("2022-05-01 15:00:00");

		// when
		List<Schedule> scheduleList = scheduleRepository.findScheduleByTime(startTime, endTime);

		// then
		assertEquals(0, scheduleList.size());
	}

	@Test
	@DisplayName("일정 등록")
	void saveSchedule() {
		// given
		Schedule schedule = Schedule.builder()
				.email("user1@miraeit.net")
				.username("사용자1")
				.title("일정 등록")
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now())
				.isAllDay(false)
				.content("일정 등록 내용")
				.build();

		// when
		Schedule save = scheduleRepository.save(schedule);

		// then
		assertEquals(schedule.getEmail(), save.getEmail());
		assertEquals(schedule.getTitle(), save.getTitle());
		assertEquals(schedule.getContent(), save.getContent());
	}
}
