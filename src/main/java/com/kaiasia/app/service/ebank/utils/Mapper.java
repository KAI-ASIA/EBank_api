package com.kaiasia.app.service.ebank.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
public class Mapper {
    public final static ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T fromJson(String json ,Class <T> clazz) throws JsonProcessingException {
        if( json == null || json.isEmpty()){
            throw new IllegalArgumentException(" json cannot be empty");
        }
        return objectMapper.readValue(json,clazz);
    }
}
