package com.omneagate.erbc.Dto;

import java.util.List;

import lombok.Data;

/**
 * Created by Shanthakumar on 09-08-2016.
 */
@Data
public class ConnectionListDto {

    public int statusCode;
    public String trackId;
    public String errorDescription;

    public List<CityDto> contents;
}
