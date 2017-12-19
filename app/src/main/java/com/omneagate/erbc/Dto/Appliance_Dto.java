package com.omneagate.erbc.Dto;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by user1 on 30/11/16.
 */

@Data
public class Appliance_Dto implements Serializable {

    int icon;
    String ApplianceName;
    String Units;
}
