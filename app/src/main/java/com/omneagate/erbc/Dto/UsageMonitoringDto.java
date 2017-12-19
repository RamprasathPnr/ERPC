package com.omneagate.erbc.Dto;

import lombok.Data;

/**
 * Created by Shanthakumar on 20-07-2016.
 */
@Data
public class UsageMonitoringDto {

    public String unitsConsumed;
    public String billAmount;
    private String month;
    private String consumerNumber;


}
