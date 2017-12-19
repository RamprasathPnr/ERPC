package com.omneagate.erbc.Dto;

import java.util.List;

import lombok.Data;

/**
 * Created by user1 on 6/5/16.
 */
@Data
public class personalDto  {

    int statusCode;

    String errorDescription;

    List<MaritalStatusDto> maritalStatus;

    List<OccupationDto> occupation;

    List<countryDto> country;

    List<StateDto> states;

    List<DistrictDto> districts;

    List<TalukDto> taluks;

    List<VillageDto> villages;

    List<DiscomDto> discom;






}
