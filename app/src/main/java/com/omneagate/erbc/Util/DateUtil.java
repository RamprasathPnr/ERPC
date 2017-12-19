package com.omneagate.erbc.Util;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by user1 on 15/6/16.
 */
public class DateUtil {
    public static Date addDays(Date date) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, AppProperties.BILL_CYCLE_PERIOD_DAYS); //minus number would decrement the days
        return cal.getTime();
    }
}