package net.miraeit.mmrc.global.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.miraeit.mmrc.global.dto.ErrorResponse;
import net.miraeit.mmrc.global.dto.helper.ErrorResponseHelper;
import net.miraeit.mmrc.global.error.GlobalErrorCode;
import net.miraeit.mmrc.global.exception.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final ErrorResponseHelper errorResponseHelper;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
		// exception 던지면 로직중 캐치돼서 ExceptionHandler를 안탐
		// throw new UnauthorizedException();

		// 최선인가..
		log.info("UnauthorizedException : ", new UnauthorizedException());

		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(HttpStatus.UNAUTHORIZED.value());

		ResponseEntity<ErrorResponse> responseEntity = errorResponseHelper.getResponseEntity(GlobalErrorCode.G0006);

		OutputStream os = response.getOutputStream();
		new ObjectMapper().writeValue(os, responseEntity.getBody());
		os.flush();
	}
}