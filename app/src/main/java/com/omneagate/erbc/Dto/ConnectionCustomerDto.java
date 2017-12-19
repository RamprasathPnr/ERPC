package com.omneagate.erbc.Dto;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by user1 on 2/6/16.
 */

@Data
public class ConnectionCustomerDto implements Serializable {

    GenericDto customer;

    GenericDto connection;

    String searchFilter;

}
