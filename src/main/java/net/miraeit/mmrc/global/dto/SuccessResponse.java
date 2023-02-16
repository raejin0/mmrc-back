package net.miraeit.mmrc.global.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuccessResponse {
    @NonNull
    private final String success = "true";
    private Object data;

    public SuccessResponse(Object data){
        this.data = data;
    }
}
