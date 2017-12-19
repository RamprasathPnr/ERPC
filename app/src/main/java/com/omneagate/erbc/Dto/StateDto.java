package com.omneagate.erbc.Dto;

import android.database.Cursor;

import lombok.Data;

/**
 * Created by user1 on 5/5/16.
 */
@Data
public class StateDto {

    int id;

    String name;
    String regionalName;

    public StateDto(Cursor cursor) {
        id = cursor.getInt(cursor.getColumnIndex("id"));
        name = cursor.getString(cursor.getColumnIndex("name"));
    }

    public StateDto() {

    }
}
