package com.macadamian.smartpantry.ui.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.macadamian.smartpantry.utility.NotificationUtility;

public class AlarmReceiver extends BroadcastReceiver {

    public final static String EXTRA_INVENTORY_ITEM_ID = "macadamian.smartpantry.ui.receivers.EXTRA_INVENTORY_ITEM_ID";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationUtility.postNotification(context);
    }
}
