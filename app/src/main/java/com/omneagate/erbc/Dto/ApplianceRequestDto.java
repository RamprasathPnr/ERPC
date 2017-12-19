package com.omneagate.erbc.Dto;

import java.util.List;

import lombok.Data;

/**
 * Created by root on 15/12/16.
 */
@Data
public class ApplianceRequestDto {

    GenericDto connection;

    List<ApplianceDto>customerAppliance;
}
