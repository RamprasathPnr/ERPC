package com.omneagate.erbc.Dto;

import lombok.Data;

/**
 * Created by user1 on 5/5/16.
 */
@Data
public class LoginResponseDto  {

    int statusCode;

    String errorDescription;

    String otp;

    String mobilenumber;

    String countrycode;

    CustomerDto customerDto2;





}
