package net.miraeit.mmrc.global.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

	private final String success = "false";
	private String code;
	private String message;
	private List<FieldErrorResponse> errorList;

	public ErrorResponse(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public ErrorResponse(String code, String message, List<FieldErrorResponse> errorList) {
		this.code = code;
		this.message = message;
		this.errorList = errorList;
	}
}
