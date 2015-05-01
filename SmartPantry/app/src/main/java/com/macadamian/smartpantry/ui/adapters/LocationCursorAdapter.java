package com.macadamian.smartpantry.ui.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.macadamian.smartpantry.R;
import com.macadamian.smartpantry.database.readers.InventoryReader;
import com.macadamian.smartpantry.ui.UIConstants;

public class LocationCursorAdapter extends CursorAdapter {

    private final SharedPreferences mSharedPrefs;

    public LocationCursorAdapter(final Context context) {
        super(context, null, false);
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.adapter_location, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        final InventoryReader reader = InventoryReader.getInstance(cursor);
        final String inventoryName = reader.getName();
        final String defaultInventoryName = mSharedPrefs.getString(UIConstants.PREF_DEFAULT_LOCATION, "");

        view.findViewById(R.id.location_default).setVisibility(defaultInventoryName.equals(inventoryName) ? View.VISIBLE : View.INVISIBLE);
        ((TextView) view.findViewById(R.id.location_txt)).setText(inventoryName);


    }
}
