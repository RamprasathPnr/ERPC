package com.omneagate.erbc.Dto;

import java.util.List;

import lombok.Data;

/**
 * Created by user1 on 20/6/16.
 */
@Data
public class PaymentHistoryList {

    int statusCode;

    String errorDescription;

    List<PaymentHistoryDto> paymentHistoryList;
}
