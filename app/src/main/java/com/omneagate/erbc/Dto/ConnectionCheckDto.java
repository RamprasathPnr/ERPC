package com.omneagate.erbc.Dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * Created by user1 on 21/5/16.
 */
@Data
public class ConnectionCheckDto implements Serializable {

    int statusCode;

    String errorDescription;

    List<RegionDto> regions;

    List<SectinoDto> sections;

    List<DistributionDto> distributions;

    List<ConnectionDto> connectionDto;

    List<DiscomDto> discom;

    List<GenericDto> complaintCategory;

    List<GenericDto> complaintSubCategory;


}
