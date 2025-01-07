package com.kaiasia.app.service.ebank.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Mapper {
    public final static ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T fromJson(String json ,Class <T> clazz) throws JsonProcessingException {
        if( json == null || json.isEmpty()){
            throw new IllegalArgumentException(" json cannot be empty");
        }
        return objectMapper.readValue(json,clazz);
    }

    public static String toJson(Object object) throws JsonProcessingException {
        if (object == null) {
            throw new IllegalArgumentException ("object cannot be empty");
        }
        return objectMapper.writeValueAsString(object);
    }
    public static <T> T fromObject(Object from, Class<T> to)  {
        return objectMapper.convertValue(from, to);
    }



}
