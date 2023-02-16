package net.miraeit.mmrc.domain.schedule.controller;

import lombok.RequiredArgsConstructor;

import net.miraeit.mmrc.domain.schedule.dto.ScheduleDeleteRequest;
import net.miraeit.mmrc.domain.schedule.dto.ScheduleModifyRequest;
import net.miraeit.mmrc.domain.schedule.dto.ScheduleRegisterRequest;
import net.miraeit.mmrc.domain.schedule.dto.ScheduleResponse;
import net.miraeit.mmrc.domain.schedule.service.ScheduleService;
import net.miraeit.mmrc.global.authentication.TokenProvider;
import net.miraeit.mmrc.global.dto.SuccessResponse;
import net.miraeit.mmrc.global.property.Regex;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Pattern;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Validated
public class ScheduleController {

	private final ScheduleService scheduleService;
	private final TokenProvider tokenProvider;

	// field 에러가 아니기 때문에 로그인 validation 과 같은  MethodArgumentNotValidException 아니라 ConstraintViolationException 발생 -> 다른 구조로 처리
	@GetMapping("/schedule/{yearMonth}")
	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	public ResponseEntity<SuccessResponse> scheduleGet(@PathVariable @Pattern(regexp = Regex.YEAR_MONTH, message = "{REGEX_YEAR_MONTH}") String yearMonth) {
		List<ScheduleResponse> monthlySchedule = scheduleService.getMonthlySchedule(yearMonth);
		return ResponseEntity.ok(new SuccessResponse(monthlySchedule));
	}

	@PostMapping("/schedule")
	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	public ResponseEntity<SuccessResponse> scheduleRegister(@RequestBody ScheduleRegisterRequest registerRequest){
		scheduleService.registerSchedule(registerRequest, tokenProvider.getUserEmailFromToken());
		return ResponseEntity.status(HttpStatus.CREATED).body(new SuccessResponse());
	}

	@PatchMapping("/schedule")
	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	public ResponseEntity scheduleMod(@RequestBody ScheduleModifyRequest modifyRequest){
		scheduleService.modifySchedule(modifyRequest, tokenProvider.getUserEmailFromToken());
		return ResponseEntity.ok(new SuccessResponse());
	}

	@DeleteMapping("/schedule")
	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	public ResponseEntity scheduleDelete(@RequestBody ScheduleDeleteRequest request) {
		// 로직
		return ResponseEntity.ok(new SuccessResponse());
	}
}
