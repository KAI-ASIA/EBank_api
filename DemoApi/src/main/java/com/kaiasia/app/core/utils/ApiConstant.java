package com.kaiasia.app.core.utils;

public class ApiConstant {

    public static abstract class STATUS{
        public static final String WAITING = "WAITING";
        public static final String RECEIVE = "RECEIVE";
        public static final String PROCESSING = "PROCESSING";
        public static final String DONE = "DONE";
        public static final String ERROR = "ERROR";
    }

    public static abstract class ErrorCode{
        public static final String INTERNAL_SERVER_ERROR = "999";
        public static final String TIMEOUT = "998";
    }
}
