package com.kaiasia.app.service.ebank.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EBankRes {
    private String customerID;
    private String responseCode;
    private String customerType;
    private String company;
    private String nationality;
    private String phone;
    private String email;
    private String mainAccount;
    private String name;
    private String trustedType;
    private String lang;
    private String startDate;
    private String endDate;
    private String pwDate;
    private String userLock;
    private String packAge;
    private String userStatus;
}
