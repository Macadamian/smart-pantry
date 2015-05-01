package com.macadamian.smartpantry.ui.activities;

import android.app.Activity;
import android.os.Bundle;

import com.macadamian.smartpantry.R;
import com.macadamian.smartpantry.ui.fragments.SettingsFragment;

public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getFragmentManager().beginTransaction().add(R.id.root_container, SettingsFragment.newInstance()).commit();
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
