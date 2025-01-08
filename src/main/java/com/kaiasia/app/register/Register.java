package com.kaiasia.app.register;


import ms.apiclient.model.ApiRequest;

import java.util.LinkedHashMap;

public class Register {
	   public static final String PROCESSING = "PROCESSING";
	    public static final String VALIDATE = "VALIDATE";
	    public static final String VALIDATE_PREFIX = "VALIDATE_";

	    public static String getAuthenType(ApiRequest request) {
	        LinkedHashMap enquiry = (LinkedHashMap) request.getBody().get("enquiry");
	        if (enquiry == null) {
	            enquiry = (LinkedHashMap) request.getBody().get("transaction");
	        }
	        return (String) enquiry.get("authenType");
	    }
}
