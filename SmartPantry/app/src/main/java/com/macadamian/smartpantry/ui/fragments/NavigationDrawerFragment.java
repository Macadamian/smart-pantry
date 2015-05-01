package com.macadamian.smartpantry.ui.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.macadamian.smartpantry.R;
import com.macadamian.smartpantry.ui.NavigationDrawerToggle;
import com.macadamian.smartpantry.ui.activities.AboutActivity;
import com.macadamian.smartpantry.ui.activities.ManageLocationsActivity;
import com.macadamian.smartpantry.ui.activities.SettingsActivity;
import com.macadamian.smartpantry.ui.adapters.NavDrawerAdapter;

public class NavigationDrawerFragment extends Fragment {

    private DrawerLayout mDrawer;
    private NavigationDrawerToggle mDrawerToggle;
    private RecyclerView mRecyclerView;
    private TextView mSettingsOptions;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.drawer_settings_option:
                    startSettings();
                    mDrawer.closeDrawers();
                    break;
            }
            mDrawer.closeDrawers();
        }
    };

    private final NavDrawerAdapter.NavDrawerAdapterListener mNavDrawerListener = new NavDrawerAdapter.NavDrawerAdapterListener() {
        @Override
        public void onItemClicked(int position) {
            switch (position) {
                case NavDrawerAdapter.NAVIGATION_ITEM_INSERT_LOCATION:
                    startManageLocations();
                    break;
                case NavDrawerAdapter.NAVIGATION_ITEM_ABOUT:
                    startAboutActivity();
                    break;
            }
            mDrawer.closeDrawers();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.drawer_list);
        mSettingsOptions = (TextView) view.findViewById(R.id.drawer_settings_option);
        return view;
    }

    public void init(final DrawerLayout drawerLayout) {
        mDrawer = drawerLayout;
        mDrawerToggle = new NavigationDrawerToggle(getActivity(), mDrawer, R.string.open_drawer, R.string.close_drawer);
        mDrawer.setDrawerListener(mDrawerToggle);
        mRecyclerView.setAdapter(new NavDrawerAdapter(getActivity(), mNavDrawerListener));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSettingsOptions.setOnClickListener(mOnClickListener);
    }

    private void startSettings() {
        final Intent intent = new Intent(getActivity(), SettingsActivity.class);
        startActivity(intent);
    }

    private void startManageLocations() {
        final Intent intent = new Intent(getActivity(), ManageLocationsActivity.class);
        startActivity(intent);
    }

    private void startAboutActivity() {
        final Intent intent = new Intent(getActivity(), AboutActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            if (mDrawer.isDrawerOpen(Gravity.START))
                mDrawer.closeDrawers();
            else
                mDrawer.openDrawer(Gravity.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void syncState() {
        mDrawerToggle.syncState();
    }

    public void closeDrawers() {
        mDrawer.closeDrawers();
    }
}
