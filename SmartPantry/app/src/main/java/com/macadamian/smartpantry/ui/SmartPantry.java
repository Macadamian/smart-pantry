package com.macadamian.smartpantry.ui;

import android.app.Application;
import com.macadamian.smartpantry.utility.analytics.Analytics;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class SmartPantry extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Analytics.initAnalytics(this);
        initUIL();
    }

    private void initUIL() {
        ImageLoaderConfiguration conf = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(conf);
    }
}
