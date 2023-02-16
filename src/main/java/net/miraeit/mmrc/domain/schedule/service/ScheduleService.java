package net.miraeit.mmrc.domain.schedule.service;

import lombok.RequiredArgsConstructor;
import net.miraeit.mmrc.domain.schedule.dto.ScheduleModifyRequest;
import net.miraeit.mmrc.domain.schedule.dto.ScheduleRegisterRequest;
import net.miraeit.mmrc.domain.schedule.dto.ScheduleResponse;
import net.miraeit.mmrc.domain.schedule.entity.Schedule;
import net.miraeit.mmrc.domain.schedule.exception.DifferentUserException;
import net.miraeit.mmrc.domain.schedule.exception.ScheduleConflictException;
import net.miraeit.mmrc.domain.schedule.exception.ScheduleNotFoundException;
import net.miraeit.mmrc.domain.schedule.repository.ScheduleRepository;
import net.miraeit.mmrc.global.dto.helper.DateTimeFormatHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true) //메모리를 적게 할당하여 부하를 최소화 할 수 있음
@RequiredArgsConstructor
public class ScheduleService {

	private final ScheduleRepository scheduleRepository;
	private final DateTimeFormatHelper formatHelper;

	/**
	 * ScheduleList 조회 후 ScheduleResponse(DTO)로 변환하여 return
	 * @param yearMonth
	 * @return
	 */
	public List<ScheduleResponse> getMonthlySchedule(String yearMonth) {
		LocalDateTime lastDateOfPreviousMonth = formatHelper.getLastDateOfPreviousMonth(yearMonth);
		LocalDateTime firstDateOfNextMonth = formatHelper.getFirstDateOfNextMonth(yearMonth);

		return scheduleRepository.findScheduleOfMonth(lastDateOfPreviousMonth, firstDateOfNextMonth)
				.stream()
				.map(ScheduleResponse::new)
				.collect(Collectors.toList());
	}

	/**
	 * Schedule(Entity)로 변환하여 save
	 * @param registerRequest
	 * @param email
	 */
	@Transactional
	public void registerSchedule(ScheduleRegisterRequest registerRequest, String email) {
		throwExceptionIfScheduleConfilct(registerRequest.getStartTime(), registerRequest.getEndTime()); // 일정 겹치는지 확인

		scheduleRepository.save(new Schedule(registerRequest, email));
	}

	/**
	 * 일정 수정
	 * @param modifyRequest
	 * @param email
	 */
	@Transactional
	public void modifySchedule(ScheduleModifyRequest modifyRequest, String email) {
		// 일정 존재 유무 확인
		Schedule schedule = scheduleRepository.findById(modifyRequest.getId()).orElseThrow(ScheduleNotFoundException::new);

		// 일정 겹치는지 확인
		throwExceptionIfScheduleConfilct(modifyRequest.getStartTime(), modifyRequest.getEndTime());

		// 같은 사용자인지 확인
		if(!schedule.getEmail().equals(email)) throw new DifferentUserException();

		schedule.changeSchedule(modifyRequest);
	}

	/**
	 * 일정 겹칠 경우 exception
	 * @param startTime
	 * @param endTime
	 */
	private void throwExceptionIfScheduleConfilct(LocalDateTime startTime, LocalDateTime endTime) {
		List<Schedule> findScheduls = scheduleRepository.findScheduleByTime(startTime, endTime);
		if (findScheduls.size() > 0) throw new ScheduleConflictException();
	}
}
