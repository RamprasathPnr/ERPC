package com.omneagate.erbc.Dto;

import com.omneagate.erbc.Dto.EnumDto.Gender;

import lombok.Data;

/**
 * Created by user1 on 3/5/16.
 */
@Data
public class CustomerDto {

     int id;

     String firstName;

     String adhaarNumber;

     String middleName;

     String lastName;

     String email;

     String addressLine1;

     String addressLine2;

     String dob;

     String countryCode;

     String mobileNumber;

     String pinCode;

     boolean status;

     Gender gender;

     MaritalStatusDto maritalStatus;

     OccupationDto occupation;

     countryDto country;

     StateDto state;

     VillageDto village;

     TalukDto taluk;

     DistrictDto district;


}
