package com.macadamian.smartpantry.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import com.macadamian.smartpantry.R;
import com.macadamian.smartpantry.utility.AlarmsUtility;

public class SettingsFragment extends PreferenceFragment {

    public final static String PREF_KEY_NOTIFICATION_TOGGLE = "pref_key_notification_toggle";
    public final static String PREF_KEY_NOTIFICATION_FREQUENCY_TOGGLE = "listPref";
    private ListPreference listPreference;
    private SharedPreferences prefs;

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        listPreference = (ListPreference) findPreference(PREF_KEY_NOTIFICATION_FREQUENCY_TOGGLE);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        boolean isDaily = prefs.getBoolean(AlarmsUtility.PREF_ALARM_DAILY, false);

        //Set the default or previously selected value
        listPreference.setSummary(getString(R.string.text_configured) +  (isDaily ? getString(R.string.text_daily) : getString(R.string.text_weekly)));
        listPreference.setValueIndex(isDaily ? 1 : 0);


        listPreference.setEnabled(((CheckBoxPreference)findPreference(PREF_KEY_NOTIFICATION_TOGGLE)).isChecked());


        getPreferenceManager().setSharedPreferencesMode(Context.MODE_MULTI_PROCESS);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(mSharePrefListener);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(mSharePrefListener);
    }

    //This is triggered anytime a change is made in preferences screen
    public SharedPreferences.OnSharedPreferenceChangeListener mSharePrefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            //Meaning a change in our radio buttons was made
            if (key.equals(PREF_KEY_NOTIFICATION_FREQUENCY_TOGGLE)) {
                listPreference.setSummary(getString(R.string.text_configured) +  listPreference.getEntry().toString());
                if (listPreference.getEntry().equals(getString(R.string.text_daily))) {
                    prefs.edit().putBoolean(AlarmsUtility.PREF_ALARM_DAILY, true).commit();
                } else {
                    prefs.edit().putBoolean(AlarmsUtility.PREF_ALARM_DAILY, false).commit();
                }

                //Remove the current alarm
                AlarmsUtility.cancelAlarm(getActivity().getApplicationContext());
                //Reset one with the new frequency preference that has been set
                AlarmsUtility.setupAlarm(getActivity().getApplicationContext());
            }
        }
    };

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        //Initialize the default interval if there is none:
        if (preference.getKey().equals(PREF_KEY_NOTIFICATION_TOGGLE)) {
            final CheckBoxPreference pref = (CheckBoxPreference) preference;
            if (pref.isChecked()) {
                AlarmsUtility.setupAlarm(getActivity());
                findPreference(PREF_KEY_NOTIFICATION_FREQUENCY_TOGGLE).setEnabled(true);
            }
            else {
                AlarmsUtility.cancelAlarm(getActivity());
                findPreference(PREF_KEY_NOTIFICATION_FREQUENCY_TOGGLE).setEnabled(false);
            }
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
