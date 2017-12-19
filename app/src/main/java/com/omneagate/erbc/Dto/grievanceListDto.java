package com.omneagate.erbc.Dto;

import java.util.List;

import lombok.Data;

/**
 * Created by user1 on 20/7/16.
 */
@Data
public class grievanceListDto {

    int statusCode;

    String errorDescription;

    List<GrievanceDto> grievanceListDto;


}
