package com.macadamian.smartpantry.utility.analytics;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;
import com.macadamian.smartpantry.R;
import com.macadamian.smartpantry.ui.SmartPantry;

import java.util.HashMap;

public class Analytics {

    private static final String TAG = "Analytics";
    private static final String appId = "com.macadamian.smartpantry";
    private static final HashMap<String, Tracker> mTrackers = new HashMap<String, Tracker>();

    public static void initAnalytics(Context context) {
        if (!mTrackers.containsKey(appId)) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
            analytics.enableAutoActivityReports((SmartPantry)(context));
            analytics.setLocalDispatchPeriod(3);
            analytics.setLogger(new Logger() {
                @Override
                public void verbose(String s) {
                    message(s);
                }

                @Override
                public void info(String s) {
                    message(s);
                }

                @Override
                public void warn(String s) {
                    message(s);
                }

                @Override
                public void error(String s) {
                    message(s);
                }

                @Override
                public void error(Exception e) {
                    message(e.getMessage());
                }

                @Override
                public void setLogLevel(int i) {

                }

                @Override
                public int getLogLevel() {
                    return 0;
                }

                public void message(String m){
                    Log.d("Google Analytics", m);
                }
            });
            Tracker t = analytics.newTracker(R.xml.tracker);
            t.enableExceptionReporting(true);
            mTrackers.put(appId, t);
            Log.v(TAG, "initialized analytics tracker");
        }
    }

    synchronized public static void sendScreenHit(final String screen) {
        Tracker t = mTrackers.get(appId);
        t.setScreenName(screen);
        t.send(new HitBuilders.ScreenViewBuilder().build());
    }

}
