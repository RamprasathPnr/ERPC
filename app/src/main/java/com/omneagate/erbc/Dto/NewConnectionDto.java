package com.omneagate.erbc.Dto;

import java.util.List;
import lombok.Data;
/**
 * Created by user1 on 20/5/16.
 */
@Data
public class NewConnectionDto {

    int statusCode;

    String errorDescription;

    List<DiscomDto> discom;

    List<PhaseDto> phases;

    List<ConsumerTypeDto> consumerType;

    List<ConnectionTypeDto> connectionType;

    List<MeterBrandDto> meterBrand;

    List<MeterTypeDto> meterType;


}
