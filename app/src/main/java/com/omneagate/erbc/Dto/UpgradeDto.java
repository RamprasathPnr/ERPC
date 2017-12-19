package com.omneagate.erbc.Dto;


import com.omneagate.erbc.Dto.EnumDto.CommonStatus;

import lombok.Data;

@Data
public class UpgradeDto  {

    String createdDate;
    String modifiedDate;
    int previousVersion;
    int currentVersion;
    String applicationType;
    CommonStatus versionStatus;
}