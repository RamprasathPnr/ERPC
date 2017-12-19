package com.omneagate.erbc.Dto;

import lombok.Data;

/**
 * Created by user1 on 31/5/16.
 */

@Data
public class CurrentMeterDetailsDto  {


    int statusCode;

    String errorDescription;

    Long id;

    ConnectionDto connection;

    CustomerDto customer;

    String currentMeterReading;

    String consumption;

    String approxCharge;

    String customerUsage;

    String meterImage;

    String latitude;

    String longitude;

    String currentReadingDate;

    String confirm;

    String billCycleToDate;

    String billCycleFromDate;

    String billId;


}
