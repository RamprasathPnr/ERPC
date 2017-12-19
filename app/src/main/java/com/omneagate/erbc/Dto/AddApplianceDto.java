package com.omneagate.erbc.Dto;

import java.util.List;

import lombok.Data;

/**
 * Created by root on 8/12/16.
 */
@Data
public class AddApplianceDto {

 /*    int statusCode;

     String errorDescription;

    GenericDto connection;

    List<AddApplianceDto> customerAppliance;
                          customerAppliance*/


     int statusCode;

    GenericDto  connection;
   // String  connection;

     String trackId;

    List<String> appliancesCode;

    List<ApplianceDto> customerAppliance;

    String createdId;

     String errorDescription;

    String  appType;

}
