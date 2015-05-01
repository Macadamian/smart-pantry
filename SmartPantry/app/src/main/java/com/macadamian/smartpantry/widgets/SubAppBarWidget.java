package com.macadamian.smartpantry.widgets;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.macadamian.smartpantry.R;
import com.macadamian.smartpantry.database.tables.InventoryTable;
import com.macadamian.smartpantry.ui.UIConstants;

import java.util.ArrayList;

public class SubAppBarWidget extends RelativeLayout {

    private Spinner mLocationSpinner;
    private final Context mContext;

    private ArrayList<String> mInventoryList;
    private int mSelectedPosition = 0;
    private ArrayAdapter<String> mAdapter;

    public SubAppBarWidget(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.widget_sub_app_bar, this, true);

        mContext = context;
    }

    public void setupFilterSpinner(Cursor cursor) {
        mLocationSpinner = (Spinner) findViewById(R.id.filter_spinner);
        mInventoryList = InventoryTable.extractAllInventoryNames(cursor);
        mInventoryList.add(0, mContext.getString(R.string.spinner_location_all));
        mAdapter = new ArrayAdapter<String>(mContext, R.layout.list_item_locations, R.id.list_item_location_label, mInventoryList);
        mLocationSpinner.setAdapter(mAdapter);
        mLocationSpinner.setOnItemSelectedListener(mOnItemSelectedListener);
    }

    private final AdapterView.OnItemSelectedListener mOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            sendSelectionChangedMessage(position);
            mSelectedPosition = position;
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    public void clearFilter() {
        if (mLocationSpinner!=null && mLocationSpinner.getCount() > 0){
            mLocationSpinner.setSelection(0);
        }
    }

    public void sendSelectionChangedMessage(int position){
        Intent intent = new Intent(UIConstants.ACTION_LOCATION_FILTER);
        // You can also include some extra data.
        intent.putExtra(UIConstants.EXTRA_LOCATION_FILTER, mAdapter.getItem(position));
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    public int getSelectedPosition(){
        return mSelectedPosition;
    }
}
