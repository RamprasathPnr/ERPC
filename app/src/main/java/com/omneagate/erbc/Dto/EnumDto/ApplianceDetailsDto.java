package com.omneagate.erbc.Dto.EnumDto;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by user1 on 7/12/16.
 */
@Data
public class ApplianceDetailsDto  implements Serializable {


    String ApplianceCode;
    String ApplianceName;
    int imageDrawableId;
    String Hours;
    String Minutes;
    String Ratting;
    String Watts;
    String Quantity;
    boolean sStarRated;
    float ConsumedUnits;
    int Position;
}
