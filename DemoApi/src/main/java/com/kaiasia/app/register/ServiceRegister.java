package com.kaiasia.app.register;

import com.kaiasia.app.core.model.ApiError;
import com.kaiasia.app.core.model.ApiRequest;
import com.kaiasia.app.core.model.ApiResponse;
import com.kaiasia.app.core.utils.GetErrorUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class ServiceRegister {
    private final Logger logger = LoggerFactory.getLogger(ServiceRegister.class);
    private static final Map<String, RequestProcessor> processors = new HashMap<>();

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private GetErrorUtils apiErrorUtils;

    @PostConstruct
    private void init() {
        Set<Class<?>> classesService = new org.reflections.Reflections("com.kaiasia.app").getTypesAnnotatedWith(KaiService.class);
        logger.debug("Found {} classes with annotation SeabService", classesService.size());

        for (Class<?> service : classesService) {
            doRegisterMethod(service);
        }
    }

    private void doRegisterMethod(Class service) {
        for (Method m : service.getMethods()) {
            if (m.isAnnotationPresent(KaiMethod.class)) {
                KaiMethod method = m.getAnnotation(KaiMethod.class);
                if (method != null && method.name() != null && method.name().trim().length() > 0) {
                    RequestProcessor processor = new RequestProcessor(ctx, service, m);

                    String key = Register.VALIDATE.equalsIgnoreCase(method.type()) ? Register.VALIDATE_PREFIX + method.name() : method.name();
                    processors.put(key, processor);
                }
            }
        }
    }

    public ApiError processValidate(String authenType, ApiRequest request) {
        String validateAuthenType = Register.VALIDATE_PREFIX + authenType;
        ApiError err = new ApiError();
        err.setCode(ApiError.OK_CODE);

        try {
            if (processors.containsKey(validateAuthenType)) {
                logger.debug("Execute method validate for AuthenType: {}", validateAuthenType);
                return (ApiError) processors.get(validateAuthenType).execute(request);
            } else {
                logger.debug("Dont have method validate for AuthenType: {}", validateAuthenType);
                err.setCode("333");
                err.setDesc("Invalid authenType");
            }
        } catch (Exception ex) {
            logger.error("{}{}", request, ex);
            err = apiErrorUtils.getError("999", new String[]{ExceptionUtils.getRootCauseMessage(ex)});
        }

        return err;
    }

    public ApiResponse processAuthenType(String authenType, ApiRequest req) {
        try {
            if (processors.containsKey(authenType)) {
                logger.debug("Execute method AuthenType: {}", authenType);
                return (ApiResponse) processors.get(authenType).execute(req);
            } else {
                logger.error("{}{}-Khong tim thay service voi authenType", req, authenType);
                ApiResponse rs = new ApiResponse();
                ApiError err = new ApiError();
                err.setCode("333");
                err.setDesc("Invalid authenType");
				rs.setError(err);
				return rs;
            }
        } catch (Exception ex) {
            logger.error("{}", ex);
        }
        return null;
    }
}
