package com.omneagate.erbc.Dto;

import lombok.Data;

/**
 * Created by user1 on 23/5/16.
 */
@Data
public class ConnenctionChkResponseDto  {

   int  statusCode;

    String errorDescription;

    ConnectionDto connection;

    MasterDto master;



}
