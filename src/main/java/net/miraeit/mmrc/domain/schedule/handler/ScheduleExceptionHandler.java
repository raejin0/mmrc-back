package net.miraeit.mmrc.domain.schedule.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.miraeit.mmrc.domain.schedule.exception.DifferentUserException;
import net.miraeit.mmrc.domain.schedule.exception.ScheduleConflictException;
import net.miraeit.mmrc.domain.schedule.exception.ScheduleNotFoundException;
import net.miraeit.mmrc.global.dto.ErrorResponse;
import net.miraeit.mmrc.global.dto.helper.ErrorResponseHelper;
import net.miraeit.mmrc.global.error.GlobalErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ScheduleExceptionHandler {

	private final ErrorResponseHelper errorResponseHelper;

	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
		log.error("handleConstraintViolationException : ", e);
		return errorResponseHelper.getResponseEntity(GlobalErrorCode.S0000);
	}

	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handScheduleConflictException(ScheduleConflictException e) {
		log.error("handScheduleConflictException : ", e);
		return errorResponseHelper.getResponseEntity(GlobalErrorCode.S0001);
	}

	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handScheduleNotFoundException(ScheduleNotFoundException e) {
		log.error("handScheduleNotFoundException : ", e);
		return errorResponseHelper.getResponseEntity(GlobalErrorCode.S0002);
	}

	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handDifferentUserException(DifferentUserException e) {
		log.error("handDifferentUserException : ", e);
		return errorResponseHelper.getResponseEntity(GlobalErrorCode.S0003);
	}
}
