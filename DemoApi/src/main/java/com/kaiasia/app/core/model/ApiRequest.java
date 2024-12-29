package com.kaiasia.app.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ApiRequest {
    private ApiHeader header;
    private ApiBody body;
    @JsonIgnore
    private String _reqId;
    @JsonIgnore
    private Integer _rejectCounter;
    @JsonIgnore
    private Long _sendTime;
    @JsonIgnore
    private String _command;
    @JsonIgnore
    private String _authenType;
    @JsonIgnore
    private Long _timeOut;
}
