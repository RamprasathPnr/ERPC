package com.omneagate.erbc.Dto;

import lombok.Data;

/**
 * Created by Shanthakumar on 20-07-2016.
 */
@Data
public class ElectricityUsage {

    public String consumerNumber;
    public String consumerName;
    public String unitsConsumed;
    public float consumedPercentage;
}
