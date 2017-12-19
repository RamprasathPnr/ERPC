package com.omneagate.erbc.Dto;

import lombok.Data;

/**
 * Created by user1 on 17/6/16.
 */
@Data
public class BillpayDto {

    int statusCode;

    String errorDescription;

    String connectionId;

    String customerId;

    String billId;

    String amount;

    String amountPaid;

    String consumption;

    Long transactionId;

    String transactionDate;

    String consumerNumber;

    String consumerName;


}
