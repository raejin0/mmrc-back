package net.miraeit.mmrc.domain.schedule.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.miraeit.mmrc.domain.schedule.dto.ScheduleModifyRequest;
import net.miraeit.mmrc.domain.schedule.dto.ScheduleRegisterRequest;
import net.miraeit.mmrc.domain.schedule.dto.ScheduleResponse;
import net.miraeit.mmrc.domain.schedule.exception.DifferentUserException;
import net.miraeit.mmrc.domain.schedule.exception.ScheduleConflictException;
import net.miraeit.mmrc.domain.schedule.exception.ScheduleNotFoundException;
import net.miraeit.mmrc.domain.schedule.service.ScheduleService;
import net.miraeit.mmrc.global.authentication.TokenProvider;
import net.miraeit.mmrc.global.dto.helper.DateTimeFormatHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ScheduleControllerTest {

	@Autowired ObjectMapper objectMapper;
	@Autowired DateTimeFormatHelper formatHelper;

	@MockBean ScheduleService scheduleService;
	@MockBean TokenProvider tokenProvider;
	@Autowired MockMvc mockMvc;

	protected MediaType contentType = new MediaType(
			MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(),
			Charset.forName("utf8")
	);


	@Test
	@DisplayName("월별 일정 조회")
	@WithMockUser(roles = {"USER"})
	void scheduleGet() throws Exception {
		// given
		String yearMonth = "2022-05";

		List<ScheduleResponse> responses = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			responses.add(
				ScheduleResponse.builder()
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

		given(scheduleService.getMonthlySchedule(any())).willReturn(responses);

		// when
		ResultActions resultActions = mockMvc.perform(get("/api/schedule/" + yearMonth)
				.contentType(this.contentType))
				.andDo(print());
		// then
		resultActions
				.andExpectAll(
						status().isOk(),
						content().contentType(MediaType.APPLICATION_JSON),
						jsonPath("$.*", hasSize(2)),
						jsonPath("$.data.*", hasSize(3))
				);
	}

	@Test
	@DisplayName("월별 일정 조회 (해당 월에 일정 없음)")
	@WithMockUser(roles = {"USER"})
	void scheduleGet_fail() throws Exception {
		// given
		String yearMonth = "2022-04";

		List<ScheduleResponse> responses = new ArrayList<>();

		given(scheduleService.getMonthlySchedule(any())).willReturn(responses);

		// when
		ResultActions resultActions = mockMvc.perform(get("/api/schedule/" + yearMonth)
				.contentType(this.contentType))
				.andDo(print());
		// then
		resultActions
				.andExpectAll(
						status().isOk(),
						content().contentType(MediaType.APPLICATION_JSON),
						jsonPath("$.*", hasSize(2)),
						jsonPath("$.data.*", hasSize(0))
				);
	}

	@Test
	@DisplayName("일정 등록")
	@WithMockUser(roles = {"USER"})
	void scheduleRegister() throws Exception {
		// given
        ScheduleRegisterRequest registerRequest = ScheduleRegisterRequest.builder()
				.username("사용자1")
				.title("일정 등록")
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now())
				.isAllDay(false)
				.content("일정 등록 내용")
				.build();

        doNothing().when(scheduleService).registerSchedule(any(),any());
        given(tokenProvider.getUserEmailFromToken()).willReturn("user@miraeit.net");

		// when
		ResultActions resultActions = mockMvc.perform(post("/api/schedule")
				.contentType(contentType)
				.content(objectMapper.writeValueAsString(registerRequest)))
				.andDo(print());

		// then
		verify(scheduleService).registerSchedule(any(), any());

		resultActions
				.andExpectAll(
						status().isCreated(),
						content().contentType(MediaType.APPLICATION_JSON),
						jsonPath("$.success").value("true")
		);
	}

	@Test
	@DisplayName("일정 등록_실패(일정 겹침)")
	@WithMockUser(roles = {"USER"})
	void scheduleRegister_fail() throws Exception {
		// given
        String email = "user1@miraeit.net";
        ScheduleRegisterRequest registerRequest = ScheduleRegisterRequest.builder()
				.username("사용자1")
				.title("일정 등록")
				.startTime(formatHelper.toDateTime("2022-05-01 09:30:00"))
				.endTime(formatHelper.toDateTime("2022-05-01 11:30:00"))
				.isAllDay(false)
				.content("일정 등록 내용")
				.build();

        doThrow(ScheduleConflictException.class).when(scheduleService).registerSchedule(any(), any());

		// when
		ResultActions resultActions = mockMvc.perform(post("/api/schedule")
				.contentType(contentType)
				.content(objectMapper.writeValueAsString(registerRequest)))
				.andDo(print());

		// then
		resultActions
				.andExpectAll(
						status().isConflict(),
						content().contentType(MediaType.APPLICATION_JSON),
						jsonPath("$.success").value("false"),
						jsonPath("$.code").value("S0001")
		);
	}

	@Test
	@DisplayName("일정 수정")
	@WithMockUser(roles = {"USER"})
	void modifySchedule() throws Exception{
		// given
        ScheduleModifyRequest modifyRequest = ScheduleModifyRequest.builder()
				.id(1L)
		        .username("사용자1")
				.title("일정 등록")
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now())
				.isAllDay(false)
				.content("일정 등록 내용")
				.build();

        doNothing().when(scheduleService).modifySchedule(any(), any());

		// when
		ResultActions resultActions = mockMvc.perform(patch("/api/schedule")
				.contentType(contentType)
				.content(objectMapper.writeValueAsString(modifyRequest)))
				.andDo(print());

		// then
		resultActions.andExpectAll(
				status().isOk(),
				content().contentType(MediaType.APPLICATION_JSON),
				jsonPath("$.success").value("true")
		);
	}

	@Test
	@DisplayName("일정 수정_실패(조회 값 없음)")
	@WithMockUser(roles = {"USER"})
	void modifySchedule_fail_null() throws Exception{
		// given
        ScheduleModifyRequest modifyRequest = ScheduleModifyRequest.builder()
				.id(1L)
		        .username("사용자1")
				.title("일정 등록")
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now())
				.isAllDay(false)
				.content("일정 등록 내용")
				.build();

        doThrow(ScheduleNotFoundException.class).when(scheduleService).modifySchedule(any(), any());

		// when
		ResultActions resultActions = mockMvc.perform(patch("/api/schedule")
				.contentType(contentType)
				.content(objectMapper.writeValueAsString(modifyRequest)))
				.andDo(print());

		// then
		resultActions.andExpectAll(
				status().isNotFound(),
				content().contentType(MediaType.APPLICATION_JSON),
				jsonPath("$.success").value("false"),
				jsonPath("$.code").value("S0002")
		);
	}

	@Test
	@DisplayName("일정 수정_실패(일정 겹침)")
	@WithMockUser(roles = {"USER"})
	void modifySchedule_fail_timeConflict() throws Exception {
		// given
		ScheduleModifyRequest modifyRequest = ScheduleModifyRequest.builder()
				.id(1L)
				.username("사용자1")
				.title("일정 등록")
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now())
				.isAllDay(false)
				.content("일정 등록 내용")
				.build();

		doThrow(ScheduleConflictException.class).when(scheduleService).modifySchedule(any(), any());

		// when
		ResultActions resultActions = mockMvc.perform(patch("/api/schedule")
				.contentType(contentType)
				.content(objectMapper.writeValueAsString(modifyRequest)))
				.andDo(print());

		// then
		resultActions.andExpectAll(
				status().isConflict(),
				content().contentType(MediaType.APPLICATION_JSON),
				jsonPath("$.success").value("false"),
				jsonPath("$.code").value("S0001")
		);
	}

	@Test
	@DisplayName("일정 수정_실패(다른 사용자)")
	@WithMockUser(roles = {"USER"})
	void modifySchedule_fail_differentUser() throws Exception {
		// given
		ScheduleModifyRequest modifyRequest = ScheduleModifyRequest.builder()
				.id(1L)
				.username("사용자1")
				.title("일정 등록")
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now())
				.isAllDay(false)
				.content("일정 등록 내용")
				.build();

		doThrow(DifferentUserException.class).when(scheduleService).modifySchedule(any(), any());

		// when
		ResultActions resultActions = mockMvc.perform(patch("/api/schedule")
				.contentType(contentType)
				.content(objectMapper.writeValueAsString(modifyRequest)))
				.andDo(print());

		// then
		resultActions.andExpectAll(
				status().isBadRequest(),
				content().contentType(MediaType.APPLICATION_JSON),
				jsonPath("$.success").value("false"),
				jsonPath("$.code").value("S0003")
		);
	}
}
