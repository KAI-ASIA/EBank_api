package com.kaiasia.app.core.async;

import ms.apiclient.model.ApiRequest;
import ms.apiclient.model.ApiResponse;

public interface IProcess {
    ApiResponse process(ApiRequest req);
}
