package com.omneagate.erbc.Dto;

import java.util.List;

import lombok.Data;

/**
 * Created by user1 on 15/6/16.
 */
@Data
public class ResponseDto {

    int statusCode;

    String errorDescription;

    List<MyUsageHistoryDto> contents;

    CurrentMeterDetailsDto currentMeterDetailsDto;

}
