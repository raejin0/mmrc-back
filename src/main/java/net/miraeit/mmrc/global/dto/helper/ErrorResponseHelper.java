package net.miraeit.mmrc.global.dto.helper;

import lombok.RequiredArgsConstructor;
import net.miraeit.mmrc.global.dto.ErrorResponse;
import net.miraeit.mmrc.global.dto.FieldErrorResponse;
import net.miraeit.mmrc.global.error.ErrorCode;
import net.miraeit.mmrc.global.error.GlobalErrorCode;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ErrorResponseHelper {

	private final MessageSource messageSource;

	public ResponseEntity<ErrorResponse> getResponseEntity(ErrorCode errorCode, String... args) {
		String message = messageSource.getMessage(errorCode.name(), args, LocaleContextHolder.getLocale());
		ErrorResponse errorResponse = new ErrorResponse(errorCode.name(), message);

		return ResponseEntity
				.status(errorCode.status())
				.body(errorResponse);
	}

	public ResponseEntity<ErrorResponse> bindErrors(Errors errors) { //  MethodArgumentNotValidException

		// field 에러 메시지 리스트
		List<FieldErrorResponse> errorList = errors
				.getFieldErrors()
				.stream()
				.map(error -> {
					String errorMessage = messageSource.getMessage(error, LocaleContextHolder.getLocale());
					return new FieldErrorResponse(error, errorMessage); })
				.collect(Collectors.toList());

		GlobalErrorCode errorCode = GlobalErrorCode.G0001;
		String message = messageSource.getMessage(errorCode.name(), null, LocaleContextHolder.getLocale());
		ErrorResponse errorResponse = new ErrorResponse(errorCode.name(), message, errorList);

		return ResponseEntity
				.status(errorCode.status())
				.body(errorResponse);
	}
}
