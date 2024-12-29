package com.kaiasia.app.core.async;

public class KaiThreadException extends Exception{
    public KaiThreadException(String location, Throwable throwable) {
        super(location, throwable);
    }
}
