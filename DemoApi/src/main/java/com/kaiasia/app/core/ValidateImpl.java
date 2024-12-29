package com.kaiasia.app.core;

import com.kaiasia.app.core.async.IValidate;
import com.kaiasia.app.core.model.ApiError;
import com.kaiasia.app.core.model.ApiRequest;
import com.kaiasia.app.core.utils.GetErrorUtils;
import com.kaiasia.app.register.Register;
import com.kaiasia.app.register.ServiceRegister;
import lombok.Getter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ValidateImpl implements IValidate {
	private final Logger logger = LoggerFactory.getLogger(ValidateImpl.class);

	@Autowired
	private GetErrorUtils apiErrUtils;
	@Autowired
	private ServiceRegister serviceRegister;

	@Override
	public ApiError validate(ApiRequest request) {

		ApiError err = new ApiError();
		err.setCode(ApiError.OK_CODE);
		if ("REQUEST".equals(request.getHeader().getReqType())) {
			String authenType = Register.getAuthenType(request);
			try {
				err = serviceRegister.processValidate(authenType, request);
				logger.info("{} {}", err.getCode(), err.getDesc());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("{}", e);
				err = apiErrUtils.getError("999", new String[] { ExceptionUtils.getRootCauseMessage(e) });
			}
		}
		return err;
	}

}
