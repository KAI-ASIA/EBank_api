package com.kaiasia.app.service.ebank.config;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class DepApiPropeties {
    private String url;
    private long timeout;
    private String apiKey;
    private String apiName;
}
