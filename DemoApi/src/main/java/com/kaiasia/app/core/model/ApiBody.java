package com.kaiasia.app.core.model;

import java.util.TreeMap;

public class ApiBody extends TreeMap<String, Object> {
    public ApiBody() {
        this.put("status", "OK");
    }
}
