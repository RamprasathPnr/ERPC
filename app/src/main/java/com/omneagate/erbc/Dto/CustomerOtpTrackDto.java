package com.omneagate.erbc.Dto;

import lombok.Data;

/**
 * Created by user1 on 11/5/16.
 */
@Data
public class CustomerOtpTrackDto {

    int statusCode;

    String otp;
    String mobile;
    String countryCode;
    String mode;

}
