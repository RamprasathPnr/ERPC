package com.omneagate.erbc.Dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class UnitConsumptionDto implements Serializable {

    int statusCode;
    String errorDescription;
    Long id;
    Long unitsConsumed;
    ConsumerTypeDto consumerType;
    String slabCategory;
    String amount;
    String fixedCharges;
    List<SlabCategoryRangeDto> slabCategoryRange;
    Long approxUnitconsumed;
    Integer consumedDays;
    Boolean isInternBill;
    String subsidyCharge;
    String totalAmount;
    String penaltyAmount;
}
