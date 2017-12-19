package com.omneagate.erbc.Dto;

import lombok.Data;

@Data
public class PaymentHistoryDto {

    Long id;
    String billInvoiceNumber;
    String paymentDetail;
    String amountPaid;
    Long transactionId;
    String billAmount;
    String  billDate;;
    String consumption;
    Long createdBy;
    Long modifiedBy;
    String createdDate;
    String modifiedDate;
    Boolean status;
    String consumerNumber;
    String consumerDisplayNo;
    String consumerName;
    String transactionDate;
    String customerName;
    String customerAddress;
    String customerMobile;
    String consumerAddress1;
    String consumerAddress2;
    String consumerVillage;
    String consumerTaluk;
    String consumerCountry;
    String consumerPinCode;
    String customerAddress1;
    String customerAddress2;
    String customerVillage;
    String customerTaluk;
    String customerCountry;
    String customerPinCode;
    boolean paymentStatus;
    String reconnectionCharges;
    String bpscCharges;



    public PaymentHistoryDto() {
    }

}
