package com.kaiasia.app.core.async;

import ms.apiclient.model.ApiError;
import ms.apiclient.model.ApiRequest;

public interface IValidate {
    ApiError validate(ApiRequest request);
}
