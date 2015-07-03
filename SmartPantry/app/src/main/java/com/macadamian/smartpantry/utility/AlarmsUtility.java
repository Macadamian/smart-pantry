package com.macadamian.smartpantry.utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.macadamian.smartpantry.ui.receivers.AlarmReceiver;

import java.util.Calendar;

public class AlarmsUtility {

    public final static String PREF_ALARM_SET = "macadamian.smartpantry.utility.PREF_ALARM_SET";
    public final static String PREF_ALARM_DAILY = "macadamian.smartpantry.utility.PREF_ALARM_DAILY";
    private final static String UNIQUE_PENDING_INTENT_ID = "macadamian.smartpantry.utility.AlarmUtility";

    /**
     * Set an inexact repeating alarm daily or weekly (inexact to save battery)
     * @param context
     */
    public static void setupAlarm(final Context context) {
        final AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        final Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setData(Uri.parse(UNIQUE_PENDING_INTENT_ID));
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        // Uncomment for testing
        //calendar.add(Calendar.MINUTE, 1);

        // Comment for testing
        long repeatInterval = AlarmManager.INTERVAL_DAY;
        calendar.set(Calendar.HOUR_OF_DAY, 10);
        if(!prefs.getBoolean(PREF_ALARM_DAILY, true)){
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
            repeatInterval = AlarmManager.INTERVAL_DAY * 7;
        }

        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), repeatInterval, pendingIntent);

        prefs.edit().putBoolean(AlarmsUtility.PREF_ALARM_SET, true).commit();
    }

    public static void cancelAlarm(final Context context) {
        final AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setData(Uri.parse(UNIQUE_PENDING_INTENT_ID));
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        manager.cancel(pendingIntent);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean(AlarmsUtility.PREF_ALARM_SET, false).commit();
    }
}
