package com.omneagate.erbc.Dto;

import java.util.List;

import lombok.Data;

/**
 * Created by user1 on 2/7/16.
 */

@Data
public class ReminderDto {

    int id;
    int statusCode;
    String errorDescription;
    String customerId;
    ConnectionDto connection;
    List<ReminderDto> customerReminders;
    String billCycleToDate;
    String reminderType;
    String reminderTime;
    String reminderDueDays;
    String noOfTimes;
    String customerEmail;
    String customerMobile;
    String billCycleFromDate;

}
