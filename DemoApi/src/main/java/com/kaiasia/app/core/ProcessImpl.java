package com.kaiasia.app.core;

import com.kaiasia.app.core.async.IProcess;
import com.kaiasia.app.register.Register;
import com.kaiasia.app.register.ServiceRegister;
import com.kaiasia.app.core.model.ApiRequest;
import com.kaiasia.app.core.model.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.kaiasia.app.core.utils.GetErrorUtils;

@Component
public class ProcessImpl implements IProcess {
    private final Logger logger = LoggerFactory.getLogger(ProcessImpl.class);

    @Autowired
    private ServiceRegister serviceRegister;
    @Autowired
    private GetErrorUtils apiErrUtils;
    
    @Value("${kai.name}")
    private String api;
    
    @Value("${kai.key}")
    private String apiKey;
    
    @Override
    public ApiResponse process(ApiRequest req) {
    	logger.debug("received msg: "+req);
        try {

	        ApiResponse dataResponse = serviceRegister.processAuthenType(Register.getAuthenType(req), req);
	        req.getHeader().setApi(api);
	        req.getHeader().setApiKey(apiKey);
	        dataResponse.setHeader(req.getHeader());
	        return dataResponse;
	        
        } catch (Exception e) {
			ApiResponse dataResponse = new ApiResponse();
			logger.error("{}", e);
			dataResponse.setHeader(req.getHeader());
			dataResponse.setError(apiErrUtils.getError("999", new String[] {e.toString()}));
			return dataResponse;
		}
    }

}
