package com.omneagate.erbc.Dto;

import java.util.List;

import lombok.Data;

/**
 * Created by user1 on 2/6/16.
 */
@Data

public class BillhistoryList {

    int statusCode;

    String errorDescription;

    List<BillHistoryDto> billHistoryList;
}
