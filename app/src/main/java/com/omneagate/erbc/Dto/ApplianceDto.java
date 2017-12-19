package com.omneagate.erbc.Dto;

import java.util.List;

import lombok.Data;

/**
 * Created by root on 2/12/16.
 */
@Data
public class ApplianceDto  {

    int statusCode;

    GenericDto connection;

    int id;

    String errorDescription;

    String applianceCode;

    String applianceName;

    float capacityInWatts;

    boolean starRated;

    long hoursUsed;

    int imageDrawableId;

    double rating;



    boolean isselected=false;

    String Hours;
    String Minutes;
    float ConsumedUnits;
    int Position;

    /*   String ApplianceCode;
    String ApplianceName;
    int imageDrawableId;

    String Ratting;
    String Watts;
    String Quantity;
    boolean sStarRated;

    int Position;*/





}
