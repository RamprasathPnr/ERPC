package com.omneagate.erbc.Dto;

import lombok.Data;

/**
 * Created by root on 26/10/16.
 */
@Data
public class GenerateBillResponse {

    int statusCode;

    String errorDescription;

    BillDetailsDto currentMeterDetailsDto;


}
