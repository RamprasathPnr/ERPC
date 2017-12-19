package com.omneagate.erbc.Dto;

import com.omneagate.erbc.Dto.EnumDto.FeedBackType;
import com.omneagate.erbc.Dto.EnumDto.GrievanceStatus;

import lombok.Data;

/**
 * Created by user1 on 19/7/16.
 */
@Data
public class GrievanceDto {

    int statusCode;
    int id;
    String errorDescription;
    GenericDto customer;
    GenericDto grievanceCategory;
    String connectionId;
    GenericDto grievanceSubCategory;
    String description;
    String grievanceNumber;
    GrievanceStatus grievanceStatus;
    String createdDate;
    FeedBackType feedbackType;
    GrievanceType grievanceType;
    String feedbackComments;
    String regionalGrievanceStatus;
}
