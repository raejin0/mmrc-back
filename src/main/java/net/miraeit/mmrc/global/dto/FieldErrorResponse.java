package net.miraeit.mmrc.global.dto;

import lombok.Getter;
import org.springframework.validation.FieldError;

@Getter
public class FieldErrorResponse {

	private String field;
	private String code;
	private String message;

	public FieldErrorResponse(FieldError error, String message) {
		this.field = error.getField();
		this.code = error.getCode();
		this.message = message;
	}
}
