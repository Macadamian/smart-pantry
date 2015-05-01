package com.macadamian.smartpantry.utility;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.common.collect.Maps;
import com.macadamian.smartpantry.R;
import com.macadamian.smartpantry.content.MyContract;
import com.macadamian.smartpantry.database.readers.InventoryItemReader;
import com.macadamian.smartpantry.database.tables.InventoryItemTable;
import com.macadamian.smartpantry.ui.activities.MainActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;

public class NotificationUtility {

    private final static int NOTIFICATION_ID = 24668;
    private final static String TAG = "NotificationUtility";
    private final static double MS_IN_ONE_DAY = 1000 * 60 * 60 * 24;


    public static void postNotification(final Context context) {
        final HashMap<String, String> expiredItemsIdName = Maps.newHashMap();

        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(MyContract.inventoryItemsActiveNotNotified(), null, null, null, null);
        }catch(Exception e){
            Log.e(TAG, "Couldn't get data for notification");
        }
            final Calendar today = Calendar.getInstance();
        today.setTimeInMillis(System.currentTimeMillis());
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int notifyAheadWindow = prefs.getBoolean(AlarmsUtility.PREF_ALARM_DAILY, true)? 1 : 7;

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                final InventoryItemReader reader = InventoryItemReader.getInstance(cursor);
                final DateFormat formatter = new SimpleDateFormat(context.getString(R.string.expiration_date_format));
                final String expireString = reader.getExpiry();
                if (expireString != null && !expireString.isEmpty()) {
                    try {
                        final Calendar itemCalendar = Calendar.getInstance();
                        itemCalendar.setTime(formatter.parse(expireString));
                        double itemExpiryDay = Math.floor(itemCalendar.getTimeInMillis() / MS_IN_ONE_DAY);
                        double currentDay = Math.floor(today.getTimeInMillis() / MS_IN_ONE_DAY);
                        if (itemExpiryDay <= currentDay + notifyAheadWindow) {
                            expiredItemsIdName.put(Long.toString(reader.getId()), reader.getAliasedName());
                        }
                    } catch (final ParseException e) {
                        Log.e(TAG, "Error when parsing date - " + e);
                    }
                }
            }
            cursor.close();
            if (expiredItemsIdName.size() > 1) {
                postNotification(context, expiredItemsIdName);
            }
            else if (expiredItemsIdName.size() > 0) {
                postNotification(context, expiredItemsIdName.keySet().iterator().next(), expiredItemsIdName.values().iterator().next());
            }
        }
    }

    public static void postNotification(final Context context, final HashMap<String, String> itemsIdName) {
        final NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        final NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(context.getString(R.string.notification_content_text_format));

        final Set<String> keys = itemsIdName.keySet();
        for (final String id : keys) {
            inboxStyle.addLine(context.getString(R.string.notification_inbox_text_format, itemsIdName.get(id)));
            try{
                context.getContentResolver().update(MyContract.inventoryItemNotifiedUri(id), InventoryItemTable.makeItemNotified(true), null, null);
            }catch (Exception e){
                Log.e(TAG, e.getMessage());
            }
        }
        manager.notify(NOTIFICATION_ID, buildBoxedNotification(context, inboxStyle));
    }

    public static void postNotification(final Context context, final String itemId, final String itemName) {
        final NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        manager.notify(NOTIFICATION_ID, buildNotification(context, itemName));

        try {
            context.getContentResolver().update(MyContract.inventoryItemNotifiedUri(itemId), InventoryItemTable.makeItemNotified(true), null, null);
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

    //Only for testing
    public static void dummyNotification (final Context context){
        final NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, buildNotification(context, "DUMMMMY"));
    }

    private static Notification buildNotification(final Context context, final String itemName) {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        final Intent intent = new Intent(context, MainActivity.class);
        builder.setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(context.getString(R.string.notification_content_title_format))
                .setContentText(context.getString(R.string.notification_inbox_text_format, itemName))
                .setAutoCancel(true)
                .setTicker(context.getString(R.string.notification_content_title_format))
                .setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_LIGHTS|Notification.DEFAULT_VIBRATE)
                .setContentIntent(PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT));
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
        builder.setLargeIcon(largeIcon);
        return builder.build();
    }

    private static Notification buildBoxedNotification(final Context context, final NotificationCompat.InboxStyle style) {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        final Intent intent = new Intent(context, MainActivity.class);
        builder.setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(context.getString(R.string.notification_content_title_format))
                .setContentText(context.getString(R.string.notification_content_text_format))
                .setTicker(context.getString(R.string.notification_content_title_format))
                .setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_LIGHTS|Notification.DEFAULT_VIBRATE)
                .setStyle(style)
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT));
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
        builder.setLargeIcon(largeIcon);
        return builder.build();
    }

}
