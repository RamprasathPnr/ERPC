package com.omneagate.erbc.Dto;

import lombok.Data;

/**
 * Created by Shanthakumar on 30-08-2016.
 */
@Data
public class AppPropertiesDto {
    int statusCode;
    int billCyclePeriodDays;
    int otpLength;
    int otpExpiryTime;
    int billCycleVariationDays;

}
