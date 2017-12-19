package com.omneagate.erbc.Dto;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by root on 21/10/16.
 */
@Data
public class BillDetailsDto {

    int statusCode;

    String errorDescription;

    private Long id;

    private String consumerNumber;

    GenericDto customer;

    GenericDto connection;

    private Long currentMeterReading;

    private Long consumption;

    private Long montlyApproxConsumption;

    private String approxCharge;

    private Boolean penalty;

    private Long customerUsage;

    private String meterImage;

    private String createdDate;

    private String modifiedDate;

    private Boolean status;

    private Double latitude;

    private Double longitude;

    private String currentReadingDate;

    private Integer consumedDays;

    private String billCycleFromDate;

    private String billCycleToDate;

    private String previousReadingDate;

    private Long previousReading;

    private Long billId;


}
