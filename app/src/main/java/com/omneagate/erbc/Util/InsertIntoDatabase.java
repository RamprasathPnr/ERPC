package com.omneagate.erbc.Util;

import android.content.Context;

import com.omneagate.erbc.Dto.ApplianceDto;

/**
 * Created by root on 2/12/16.
 */
public class InsertIntoDatabase {
    Context context;

    public InsertIntoDatabase(Context context) {
        this.context = context;
    }
    public void insertIntoDatabase() {
            insertDbValues("1","Television",40,true,0.04);
            insertDbValues("2","Audio System",50,true,0.05);
            insertDbValues("3","Refrigerator",500,true,0.5);
            insertDbValues("4","Lamps (Bulb)",60,true,0.06);
            insertDbValues("5","Lamps (CFL)",20,true,0.02);
            insertDbValues("6","Tube Lights",40,true,0.04);
            insertDbValues("7","Electric Dry Iron",500,true,0.5);
            insertDbValues("8","Microwave Oven",100,true,0.1);
            insertDbValues("9","Electric Toaster",800,true,0.8);
            insertDbValues("10","Storage Water Heater",500,true,0.5);
            insertDbValues("11","Instant Water Heater",18,true,0.018);
            insertDbValues("12","Washing Machine",700,true,0.7);
            insertDbValues("13","Mixer",100,true,0.1);
            insertDbValues("14","Grinder",500,true,0.5);
            insertDbValues("15","Charger",5,true,0.005);
            insertDbValues("16","Emergency Light",100,true,0.1);
            insertDbValues("17","Air Conditioner",1500,true,1.5);
            insertDbValues("18","Pump Set",1119,false,1.119);


        insertStarRatedValues();

    }

    private void insertDbValues(String appliacnce_code, String appliance_name, int capacity, boolean star_rated, double units_hour) {
        ApplianceDto applianceDto = new ApplianceDto();
        applianceDto.setApplianceCode(appliacnce_code);
        applianceDto.setApplianceName(appliance_name);
        applianceDto.setCapacityInWatts(capacity);
        applianceDto.setStarRated(star_rated);
        DBHelper.getInstance(context).insertApplianceValue(applianceDto);
    }

    private void insertStarRatedValues() {
        DBHelper.getInstance(context).insertStarRated(1, 4);
        DBHelper.getInstance(context).insertStarRated(2, 7);
        DBHelper.getInstance(context).insertStarRated(3, 15);
        DBHelper.getInstance(context).insertStarRated(4, 22);
        DBHelper.getInstance(context).insertStarRated(5, 30);


    }

}
