package net.miraeit.mmrc.domain.user.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.miraeit.mmrc.domain.user.exception.DuplicateUserException;
import net.miraeit.mmrc.global.dto.ErrorResponse;
import net.miraeit.mmrc.global.dto.helper.ErrorResponseHelper;
import net.miraeit.mmrc.global.error.GlobalErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class UserExceptionHandler {

	private final ErrorResponseHelper errorResponseHelper;

	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleDuplicateUserException(DuplicateUserException e) {
		log.info("handleDuplicateUserException : ", e);
		return errorResponseHelper.getResponseEntity(GlobalErrorCode.U0000);
	}

	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException e) {
		log.info("handleUsernameNotFoundException : ", e);
		return errorResponseHelper.getResponseEntity(GlobalErrorCode.U0001);
	}

	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException e) {
		log.info("handleBadCredentialsException : ", e);
		return errorResponseHelper.getResponseEntity(GlobalErrorCode.U0002);
	}
}
