package com.omneagate.erbc.Dto;

import lombok.Data;

/**
 * Created by user1 on 25/11/15.
 */
@Data
public class VersionUpgradeDto {


    int statusCode;

    long id;

    int upgradeVersion;

    String releaseDate;

    String location;



}