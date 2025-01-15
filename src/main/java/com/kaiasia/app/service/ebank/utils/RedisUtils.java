package com.kaiasia.app.service.ebank.utils;

import lombok.extern.slf4j.Slf4j;
import ms.apiclient.model.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


@Slf4j
@Component
public class RedisUtils {

    @Autowired
    private RedisTemplate<String , Object> redisTemplate;

    public ApiResponse getCache(String key){
        if(key == null || key.trim().isEmpty()){
            log.info("invalid key :{}",key);
            return null;
        }

        try{
            Object cache = redisTemplate.opsForValue().get(key);
            if(cache != null){
                log.info("cache get User info :{}", key);
                return (ApiResponse) cache;
            }else {
                log.info("Cache miss for key: {}", key);
            }
        }catch (Exception e){
            log.info("Redis error :{}",e.getMessage());
        }

        return  null;
    }

    public void setTimeToLive(String key ,ApiResponse apiResponse, long time){
        redisTemplate.opsForValue().set(key,apiResponse,time, TimeUnit.SECONDS);
        log.info("Save {} in cache }",key);
    }
}
