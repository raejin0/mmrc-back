package net.miraeit.mmrc.global.error;

import org.springframework.http.HttpStatus;

public enum GlobalErrorCode implements ErrorCode {
    G0000(HttpStatus.INTERNAL_SERVER_ERROR)
    ,G0001(HttpStatus.BAD_REQUEST)
    /*,G0002(HttpStatus.INTERNAL_SERVER_ERROR)
    ,G0003(HttpStatus.BAD_REQUEST)*/
    ,G0004(HttpStatus.NOT_FOUND)
    ,G0005(HttpStatus.BAD_REQUEST)
    ,G0006(HttpStatus.UNAUTHORIZED)
    ,G0007(HttpStatus.FORBIDDEN)

	,U0000(HttpStatus.CONFLICT)
	,U0001(HttpStatus.NOT_FOUND)
	,U0002(HttpStatus.BAD_REQUEST)

	,S0000(HttpStatus.BAD_REQUEST)
	,S0001(HttpStatus.CONFLICT)
	,S0002(HttpStatus.NOT_FOUND)
	,S0003(HttpStatus.BAD_REQUEST);

	private final HttpStatus status;

	GlobalErrorCode(HttpStatus status) {
		this.status = status;
	}

	@Override
	public HttpStatus status() {
		return status;
	}
}
