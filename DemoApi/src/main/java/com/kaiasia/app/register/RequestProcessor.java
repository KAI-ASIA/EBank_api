package com.kaiasia.app.register;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;

public class RequestProcessor {

    private final ApplicationContext ctx;
    private final Class clazz;
    private final Method method;

    RequestProcessor(ApplicationContext ctx, Class clazz, Method method) {
        this.ctx = ctx;
        this.clazz = clazz;
        this.method = method;
    }

    public Object execute(Object dataInput) throws Exception {
        Object instance = ctx.getBean(clazz);
        return method.invoke(instance, dataInput);
    }
}
