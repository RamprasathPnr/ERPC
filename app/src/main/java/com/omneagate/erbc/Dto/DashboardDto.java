package com.omneagate.erbc.Dto;

import java.util.List;

import lombok.Data;

/**
 * Created by Shanthakumar on 20-07-2016.
 */
@Data
public class DashboardDto {

    public int statusCode;
    public String trackId;
    public String errorDescription;
    public String customerId;
    public String billAmount;
    public int connectionCount;

    private Integer connectionDueCount;
    private String lastViewedConnection;
    private Integer unpaidBillCount;

    private String lastViewedDate;

    private String lastViewedConsumerName;

    private List<String> connectionDueNumbers;

    public List<UsageMonitoringDto> usageMonitoring;
    public List<ElectricityUsage> currentelectricityUsage;
    public List<UsageMonitoringDto> contents;
    public List<BillDetailsDto> currentBillDetailsDto;


}
