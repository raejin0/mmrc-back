package net.miraeit.mmrc.global.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.miraeit.mmrc.global.dto.ErrorResponse;
import net.miraeit.mmrc.global.error.GlobalErrorCode;
import net.miraeit.mmrc.global.dto.helper.ErrorResponseHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

	private final ErrorResponseHelper errorResponseHelper;

	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleException(Exception e) {
		log.error("handleException :", e);
		return errorResponseHelper.getResponseEntity(GlobalErrorCode.G0000);
	}

    @ExceptionHandler
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		log.error("MethodArgumentNotValidException :", e);
		return errorResponseHelper.bindErrors(e);
	}

    @ExceptionHandler
	public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
		log.error("handleHttpRequestMethodNotSupportedException : ", e);
        return errorResponseHelper.getResponseEntity(GlobalErrorCode.G0004);
	}

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
		log.error("handleHttpRequestMethodNotSupportedException : ", e);
        return errorResponseHelper.getResponseEntity(GlobalErrorCode.G0005);
    }

	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleForbiddenException(AccessDeniedException e) {
		log.error("AccessDeniedException : ", e);
		return errorResponseHelper.getResponseEntity(GlobalErrorCode.G0007);
	}
}

