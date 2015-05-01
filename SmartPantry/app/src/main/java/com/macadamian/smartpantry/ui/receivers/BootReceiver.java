package com.macadamian.smartpantry.ui.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.macadamian.smartpantry.utility.AlarmsUtility;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            AlarmsUtility.setupAlarm(context);
        }
    }
}
