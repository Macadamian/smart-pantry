package com.macadamian.smartpantry.utility;

import java.util.Calendar;


public class DateUtility {

    public static final long ONE_MILLISECOND = 1;
    public static final long ONE_SECOND = ONE_MILLISECOND * 1000;
    public static final long ONE_MINUTE = ONE_SECOND * 60;
    public static final long ONE_HOUR = ONE_MINUTE * 60;
    public static final long ONE_DAY = ONE_HOUR * 24;
    public static final long ONE_WEEK = ONE_DAY * 7;

    public static long compareCalendars(final Calendar calendar1, final Calendar calendar2) {
        final long time1 = calendar1.getTimeInMillis();
        final long time2 = calendar2.getTimeInMillis();
        return time2 - time1;
    }
}
