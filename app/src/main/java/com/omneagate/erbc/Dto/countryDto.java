package com.omneagate.erbc.Dto;

import android.database.Cursor;

import lombok.Data;

/**
 * Created by user1 on 5/5/16.
 */
@Data
public class countryDto {

    int id;

    String name;
    String regionalName;


    public countryDto(Cursor cursor) {

        id = cursor.getInt(cursor.getColumnIndex("id"));
        name = cursor.getString(cursor.getColumnIndex("name"));

    }

    public countryDto() {

    }
}
