package com.kaiasia.app.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {
    public static String OK_CODE = "000";
    public static String OK_DESC = "OK";
    private String code;
    private String desc;
}
