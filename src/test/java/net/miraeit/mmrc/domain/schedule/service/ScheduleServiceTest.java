package net.miraeit.mmrc.domain.schedule.service;

import net.miraeit.mmrc.domain.schedule.dto.ScheduleModifyRequest;
import net.miraeit.mmrc.domain.schedule.dto.ScheduleRegisterRequest;
import net.miraeit.mmrc.domain.schedule.dto.ScheduleResponse;
import net.miraeit.mmrc.domain.schedule.entity.Schedule;
import net.miraeit.mmrc.domain.schedule.exception.DifferentUserException;
import net.miraeit.mmrc.domain.schedule.exception.ScheduleConflictException;
import net.miraeit.mmrc.domain.schedule.exception.ScheduleNotFoundException;
import net.miraeit.mmrc.domain.schedule.repository.ScheduleRepository;
import net.miraeit.mmrc.global.dto.helper.DateTimeFormatHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class ScheduleServiceTest {

	@Mock DateTimeFormatHelper formatHelper;
	@Mock ScheduleRepository scheduleRepository;
	@InjectMocks ScheduleService scheduleService;


	@Test
	@DisplayName("월별 일정 조회")
	void getMonthlySchedule() {
		// given
		String yearMonth = "2022-05";

		List<Schedule> responses = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			responses.add(
				Schedule.builder()
					.id(((long)i))
					.username("사용자" + i)
					.title("title" )
					.startTime(LocalDateTime.now())
					.endTime(LocalDateTime.now())
					.isAllDay(false)
					.content("123")
					.build()
			);
		}

		given(formatHelper.getLastDateOfPreviousMonth(any())).willReturn(LocalDateTime.now());
		given(formatHelper.getFirstDateOfNextMonth(any())).willReturn(LocalDateTime.now());
		given(scheduleRepository.findScheduleOfMonth(any(), any())).willReturn(responses);

		// when
		List<ScheduleResponse> monthlySchedule = scheduleService.getMonthlySchedule(yearMonth);

		// then
		assertEquals(monthlySchedule.size(), 3);
	}

	@Test
	@DisplayName("일정 등록")
	void registerSchedule() {
		// given
		List<Schedule> findSchedule = new ArrayList<>();
        String email = "user1@miraeit.net";
        ScheduleRegisterRequest registerRequest = ScheduleRegisterRequest.builder()
				.username("사용자1")
				.title("일정 등록")
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now())
				.isAllDay(false)
				.content("일정 등록 내용")
				.build();

		given(scheduleRepository.findScheduleByTime(any(),any())).willReturn(findSchedule);

		// when
		scheduleService.registerSchedule(registerRequest, email);

		// then
		// 반환값 없음
	}

	@Test
	@DisplayName("일정 등록_실패(일정 겹침)")
	void registerSchedule_fail() {
		// given
		List<Schedule> findSchedule = new ArrayList<>();
		findSchedule.add(new Schedule());

        String email = "user1@miraeit.net";
        ScheduleRegisterRequest registerRequest = ScheduleRegisterRequest.builder()
				.username("사용자1")
				.title("일정 등록")
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now())
				.isAllDay(false)
				.content("일정 등록 내용")
				.build();

		given(scheduleRepository.findScheduleByTime(any(),any())).willReturn(findSchedule);

		// when & then
		assertThrows(ScheduleConflictException.class, () ->{
			scheduleService.registerSchedule(registerRequest, email);
		});
	}

	@Test
	@DisplayName("일정 수정")
	void modifySchedule() {
		// given
        String email = "user1@miraeit.net";
        ScheduleModifyRequest modifyRequest = ScheduleModifyRequest.builder()
				.id(1L)
		        .username("사용자1")
				.title("일정 등록")
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now())
				.isAllDay(false)
				.content("일정 등록 내용")
				.build();

		Schedule findSchedule = Schedule.builder()
				.id(modifyRequest.getId())
				.email(email)
				.username(modifyRequest.getUsername())
				.title(modifyRequest.getTitle())
				.startTime(modifyRequest.getStartTime())
				.endTime(modifyRequest.getEndTime())
				.isAllDay(modifyRequest.getIsAllDay())
				.content(modifyRequest.getContent())
				.build();
		given(scheduleRepository.findById(modifyRequest.getId())).willReturn(Optional.of(findSchedule));

		// when
		scheduleService.modifySchedule(modifyRequest, email);

		// then 반환값 없음
	}

	@Test
	@DisplayName("일정 수정_실패(조회 값 없음)")
	void modifySchedule_fail_null() {
		// given
        String email = "user1@miraeit.net";
        ScheduleModifyRequest modifyRequest = ScheduleModifyRequest.builder()
				.id(1L)
		        .username("사용자1")
				.title("일정 등록")
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now())
				.isAllDay(false)
				.content("일정 등록 내용")
				.build();

        List<Schedule> findScheduleList = new ArrayList<>();

        given(scheduleRepository.findById(modifyRequest.getId())).willReturn(Optional.empty());
        given(scheduleRepository.findScheduleByTime(any(),any())).willReturn(findScheduleList);

		// when & then
		assertThrows(ScheduleNotFoundException.class, () -> scheduleService.modifySchedule(modifyRequest, email));
	}

	@Test
	@DisplayName("일정 수정_실패(일정 겹침)")
	void modifySchedule_fail_timeConflict() {
		// given
        String email = "user1@miraeit.net";
        ScheduleModifyRequest modifyRequest = ScheduleModifyRequest.builder()
				.id(1L)
		        .username("사용자1")
				.title("일정 등록")
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now())
				.isAllDay(false)
				.content("일정 등록 내용")
				.build();

		Schedule findSchedule = Schedule.builder()
				.id(modifyRequest.getId())
				.email(email)
				.username(modifyRequest.getUsername())
				.title(modifyRequest.getTitle())
				.startTime(modifyRequest.getStartTime())
				.endTime(modifyRequest.getEndTime())
				.isAllDay(modifyRequest.getIsAllDay())
				.content(modifyRequest.getContent())
				.build();

		List<Schedule> findScheduleList = new ArrayList<>();
		findScheduleList.add(new Schedule());

		given(scheduleRepository.findById(modifyRequest.getId())).willReturn(Optional.of(findSchedule));
		given(scheduleRepository.findScheduleByTime(any(),any())).willReturn(findScheduleList);

		// when & then
		assertThrows(ScheduleConflictException.class, () -> scheduleService.modifySchedule(modifyRequest, email));
	}

	@Test
	@DisplayName("일정 수정_실패(다른 사용자)")
	void modifySchedule_fail_differentUser() {
		// given
        String email = "user1@miraeit.net";
        ScheduleModifyRequest modifyRequest = ScheduleModifyRequest.builder()
				.id(1L)
		        .username("사용자1")
				.title("일정 등록")
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now())
				.isAllDay(false)
				.content("일정 등록 내용")
				.build();

		Schedule findSchedule = Schedule.builder()
				.id(modifyRequest.getId())
				.email("admin@miraeit.net")
				.username(modifyRequest.getUsername())
				.title(modifyRequest.getTitle())
				.startTime(modifyRequest.getStartTime())
				.endTime(modifyRequest.getEndTime())
				.isAllDay(modifyRequest.getIsAllDay())
				.content(modifyRequest.getContent())
				.build();


        List<Schedule> findScheduleList = new ArrayList<>();

        given(scheduleRepository.findById(modifyRequest.getId())).willReturn(Optional.of(findSchedule));
        given(scheduleRepository.findScheduleByTime(any(),any())).willReturn(findScheduleList);

		// when & then
		assertThrows(DifferentUserException.class, () -> scheduleService.modifySchedule(modifyRequest, email));
	}
}