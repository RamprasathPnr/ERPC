package com.omneagate.erbc.Dto;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by user1 on 2/6/16.
 */

@Data
public class BillHistoryDto implements Serializable {

     int id;//
     int connectionId;//
     int customerId;//
     String invoiceNo;//
     String amount;//Total Amount
     String billDate;//
     String totalMeterReading;//
     String previousMeterReading;//
     String previousMeterReadingDate;//
     String currentMeterReading;//
     String currentMeterReadingDate;//
     String consumption;//
     String status;//
     String consumerNumber;//
     String consumerName;//
     String consumerDisplayNo;//
     String consumerAdress;
     String consumerAddressLine1;//
     String consumerAddressLine2;//
     String customerName;
     String customerAdress;
     String customerMobile;
     String customeremail;
     String phaseType;
     String consumerType;
     String connectionType;
     String lastDueDate;// want to do Payment due date
     String penaltyAmount;//want to do Total Penalty Charges
     String consumerVillage;
     String consumerTaluk;
     String consumerCountry;
     String consumerPinCode;
     String billStatus;
     String billCycleFromDate;
     String billCycleToDate;
     String statusCode;
     String trackId;
     String appType;
     String errorDescription;
     String createdId;
     String billId;
     String createdBy;
     String modifiedBy;
     String createdDate;
     String modifiedDate;
     String consumerState;
     String consumerDistrict;
     String billCycleDate;
     String reconnectionCharges;// want to do Re-connection Charges(E)
     String bpscCharges;// want to do  Penalty Charges-BPSC (INR)
     String nextCycleDate;
     String unitsConsumed;
     String slabRate;
     String fixedCharge;
     String subsidyAmount;
     String billType;
     String unitsConsumption;
     String perUnit;
     String agentId;
     String generatedType;
     String previousPayment;
     String previousPaymentDate;
     GenericDto region;

}






















