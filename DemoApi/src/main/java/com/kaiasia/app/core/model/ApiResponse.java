package com.kaiasia.app.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ApiResponse {
    private ApiHeader header;
    private ApiBody body;
    private ApiError error;

    public void setError(ApiError error) {
        this.error = error;
        if (error != null && !ApiError.OK_CODE.equals(error.getCode())) {
            this.body = new ApiBody();
            this.body.put("status", "FAILE");
        }
    }

}
