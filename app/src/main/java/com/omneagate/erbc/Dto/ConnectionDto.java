package com.omneagate.erbc.Dto;

import com.omneagate.erbc.Dto.EnumDto.ConnectionUserType;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by user1 on 19/5/16.
 */
@Data
public class ConnectionDto implements Serializable {

    int statusCode;
    int id;
    String errorDescription;
    CustomerDto customer;
    countryDto country;
    StateDto state;
    VillageDto village;
    String pinCode;
    TalukDto taluk;
    DistrictDto district;
    String countryCode;
    String serialNumber;
    MeterTypeDto meterType;
    ConnectionTypeDto connectionType;
    ConsumerTypeDto consumerType;
    ConnectionUserType connectionUserType;
//    String connectionUserType;
    MeterBrandDto meterBrand;
    String lastMeterReading;
    String lastMeterReadingDate;
    String nextMeterReadingDate;
    String billCycleFromDate;
    String billCycleToDate;
    PhaseDto phase;
    DiscomDto discom;
    String meterImage;
    String ebCardImage;
    String ebBillImage;
    String consumerName;
    String addressLine1;
    String addressLine2;
    String consumerNumber;
    String consumerDisplayNo;
    Double latitude;
    Double longitude;
    String imageTakenDate;
    String createdDate;
    String regionCode;
    String lastViewDate;
    String lastViewAmount;
    String lastViewConsumption;
    boolean checkCycleDate;
    String meterImageUrl;
    String ebCardImageUrl;
    String ebBillImageUrl;
    boolean reminderAdded;
    boolean billStatus;
}
