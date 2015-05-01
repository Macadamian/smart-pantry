package com.macadamian.smartpantry.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.zxing.client.android.PreferencesActivity;
import com.macadamian.smartpantry.R;
import com.macadamian.smartpantry.content.MyContract;
import com.macadamian.smartpantry.content.action.ActionExecuter;
import com.macadamian.smartpantry.database.readers.InventoryItemReader;
import com.macadamian.smartpantry.database.readers.InventoryReader;
import com.macadamian.smartpantry.database.tables.InventoryItemTable;
import com.macadamian.smartpantry.ui.UIConstants;
import com.macadamian.smartpantry.ui.adapters.InventoryRecyclerAdapter;
import com.macadamian.smartpantry.ui.fragments.NavigationDrawerFragment;
import com.macadamian.smartpantry.ui.fragments.SettingsFragment;
import com.macadamian.smartpantry.utility.AlarmsUtility;
import com.macadamian.smartpantry.utility.AnimationUtility;
import com.macadamian.smartpantry.utility.Toaster;
import com.macadamian.smartpantry.widgets.EmptyViewWidget;
import com.macadamian.smartpantry.widgets.SmartRecyclerView;
import com.macadamian.smartpantry.widgets.SubAppBarWidget;
import com.melnykov.fab.FloatingActionButton;

import org.apache.commons.lang3.StringUtils;

import java.util.List;


public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>{

    private final String EXTRA_SEARCH_QUERY = "EXTRA_SEARCH_QUERY";
    private final String KEY_STATE_SEARCH_QUERY = "KEY_STATE_SEARCH_QUERY";

    private NavigationDrawerFragment mDrawerFragment;

    // Recycler related variables
    private SmartRecyclerView mInventoryList;
    private InventoryRecyclerAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private ActionMode mActionMode;
    private EmptyViewWidget mEmptyViewWidget;
    private TextView mActionBarSplat;
    private ImageView mImageViewAppIcon;
    private FloatingActionButton fab;
    private SearchView mSearchView;
    private String mSavedSearchQuery;
    private SubAppBarWidget mSubAppBar;
    private ShowcaseView sv;
    private ContentObserver mInactiveItemContentObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            mInventoryList.smoothScrollToPosition(0);
        }
    };

    private BroadcastReceiver mResetSubAppBarReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
             AnimationUtility.resetMoveToZero(mSubAppBar);
        }
    };

    private String mFilterSelectedUUID;
    private String mFilterSelectionLocation;

    private Boolean mIsActiveListShowing = true;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.pantry_empty_view_button:
                    startEditItem();
                    break;
                case R.id.add_item_button:
                    if (sv != null){
                        sv.hide();
                    }
                    startEditItem();
                    break;
                case R.id.actionbar_btn_container:
                    if (sv != null){
                        sv.hide();
                    }
                    AnimationUtility.resetMoveToZero(mSubAppBar);
                    mIsActiveListShowing = !mIsActiveListShowing;
                    mSubAppBar.clearFilter();
                    switchToActiveInactiveLoader(mIsActiveListShowing);
                    mActionBarSplat.setVisibility(mIsActiveListShowing ? View.VISIBLE : View.INVISIBLE);
                    mImageViewAppIcon.setImageDrawable(getDrawable(mIsActiveListShowing ? R.drawable.shopping_list_icon : R.drawable.ic_launcher));
                    mDrawerFragment.closeDrawers();
                    break;
            }
        }
    };

    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UIConstants.ACTION_LOCATION_FILTER:
                    final String selectedItem = intent.getStringExtra(UIConstants.EXTRA_LOCATION_FILTER);
                    filterByLocation(selectedItem);
            }
        }
    };

    private final BroadcastReceiver mSplatAnimation = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UIConstants.ACTION_ANIMATE_SPLAT:
                    if (mIsActiveListShowing) {
                        AnimationUtility.updateAnimation(mActionBarSplat);
                    }
            }
        }
    };

    private final SearchView.OnQueryTextListener mOnQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            if (mAdapter.getItemCount() == 1) {
                final Cursor cursor = mAdapter.getCursor();
                cursor.moveToFirst();
                startEditItemFromID(InventoryItemReader.getInstance(cursor).getItemUUID());
                cursor.close();
                destroyAnyLoaderButSelf(mIsActiveListShowing ? UIConstants.LOADER_ACTIVE_INVENTORY_ITEMS : UIConstants.LOADER_INACTIVE_INVENTORY_ITEMS);
                switchToCurrentActiveLoader();

                mSearchView.setQuery("", false);
                mSearchView.setIconified(true);
            }
            mSearchView.clearFocus();
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if (newText != null && !newText.isEmpty()) {
                switchToSearchLoader(newText);
                return true;
            }
            destroyAnyLoaderButSelf(mIsActiveListShowing ? UIConstants.LOADER_ACTIVE_INVENTORY_ITEMS : UIConstants.LOADER_INACTIVE_INVENTORY_ITEMS);
            switchToCurrentActiveLoader();
            return false;
        }
    };

    private final InventoryRecyclerAdapter.MultiSelectionInterface mMultiSelection = new InventoryRecyclerAdapter.MultiSelectionInterface() {

        @Override
        public void onMultiSelectionChanged(int position, boolean selected) {
            if (mActionMode != null) {
                if (mAdapter.getSelectedPositions().size() == 0) {
                    mActionMode.finish();
                }
                return;
            }
            mActionMode = startActionMode(mActionModeCallback);
        }

        @Override
        public boolean isMultiSelectionModeEnabled() {
            return mActionMode != null;
        }
    };

    private final ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        private void onBatchMoveToShoppingList() {
            final List<Integer> selectedPosition = mAdapter.getSelectedPositions();
            final Cursor cursor = mAdapter.getCursor();
            for (final int position : selectedPosition) {
                cursor.moveToPosition(position);
                final InventoryItemReader reader = InventoryItemReader.getInstance(cursor);
                getContentResolver().update(MyContract.inventoryItemActiveUri(reader.getItemUUID()), InventoryItemTable.makeItemActive(false), null, null);
            }
            final String toastMessage = getResources().getQuantityString(R.plurals.toast_item_moved_to_shopping_list, selectedPosition.size(), selectedPosition.size());
            Toaster.makeText(MainActivity.this, toastMessage, Toast.LENGTH_LONG);
        }

        private void onBatchRemove(final ActionMode mode) {
            final List<Integer> selectedPosition = mAdapter.getSelectedPositions();
            final Cursor cursor = mAdapter.getCursor();
            new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                    .setTitle(getResources().getQuantityString(R.plurals.alert_delete_items_title, selectedPosition.size()))
                    .setMessage(getResources().getQuantityString(R.plurals.alert_delete_items_sub_title, selectedPosition.size()))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            for (final int position : selectedPosition) {
                                cursor.moveToPosition(position);
                                final InventoryItemReader reader = InventoryItemReader.getInstance(cursor);
                                getContentResolver().delete(MyContract.inventoryItemUri(reader.getItemUUID()), null, null);
                            }
                            final String toastMessage = getResources().getQuantityString(R.plurals.toast_item_removed, selectedPosition.size(), selectedPosition.size());
                            Toaster.makeText(MainActivity.this, toastMessage, Toast.LENGTH_LONG);
                            mAdapter.clearSelectedPositions();
                            mode.finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            mode.finish();
                        }
                    })
                    .setIcon(R.drawable.ic_delete_grey600_48dp)
                    .show();
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            getMenuInflater().inflate(R.menu.menu_main_cab, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.item_delete:
                    onBatchRemove(mode);
                    return true;
                case R.id.item_toshop:
                    onBatchMoveToShoppingList();
                    mAdapter.clearSelectedPositions();
                    mode.finish();
                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAdapter.clearSelectedPositions();
            mActionMode = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_STATE_SEARCH_QUERY)) {
                mSavedSearchQuery = savedInstanceState.getString(KEY_STATE_SEARCH_QUERY);
            }
        }

        //TODO change in PreferencesActivity if we bring the code into the project and remove below
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstRun = prefs.getBoolean(UIConstants.PREF_APP_FIRST_LAUNCH, true);
        if (isFirstRun) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(PreferencesActivity.KEY_PLAY_BEEP, false);
            final Cursor cursor = getContentResolver().query(MyContract.inventoriesUri(), null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                final InventoryReader reader = InventoryReader.getInstance(cursor);
                editor.putString(UIConstants.PREF_DEFAULT_LOCATION, reader.getName()).apply();
            }
            editor.putBoolean(UIConstants.PREF_APP_FIRST_LAUNCH, false);
            editor.apply();
        }

        mSubAppBar = (SubAppBarWidget) findViewById(R.id.sub_app_bar_widget);
        LocalBroadcastManager.getInstance(this).registerReceiver(mResetSubAppBarReciever, new IntentFilter(UIConstants.ACTION_RESET_SUBAPPBARR));
        setupActionBar();
        maybeSetupAlarm();
        initializeNavigationDrawer();
        initializeRecyclerView();

        getLoaderManager().initLoader(UIConstants.LOADER_ACTIVE_INVENTORY_ITEMS, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return new CursorLoader(MainActivity.this, MyContract.inventoryItemsActiveUri(), null, null, null, null);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                getLoaderManager().initLoader(UIConstants.LOADER_ACTIVE_INVENTORY_ITEMS, null, MainActivity.this);
                getLoaderManager().initLoader(UIConstants.LOADER_LOCATION_LIST, null, MainActivity.this);
                getLoaderManager().initLoader(UIConstants.LOADER_INACTIVE_COUNT_INVENTORY_ITEMS, null, MainActivity.this);

                LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(mMessageReceiver,
                        new IntentFilter(UIConstants.ACTION_LOCATION_FILTER));
                LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(mSplatAnimation,
                        new IntentFilter(UIConstants.ACTION_ANIMATE_SPLAT));

                boolean initialShowcaseNotYetShown = prefs.getBoolean(UIConstants.PREF_MAIN_INITIAL_SHOWCASE, true);
                if (initialShowcaseNotYetShown) {
                    showOverlayTutorialOne();
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

            }
        });
        
        setupUIComponents();
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mSplatAnimation);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mResetSubAppBarReciever);
        getContentResolver().unregisterContentObserver(mInactiveItemContentObserver);
        super.onDestroy();
    }

    private void setupActionBar() {
        if (getActionBar() != null) {
            final View v = LayoutInflater.from(this).inflate(R.layout.actionbar_layout, null);
            mActionBarSplat = (TextView) v.findViewById(R.id.actionbar_splat);
            mImageViewAppIcon = (ImageView)v.findViewById(R.id.icon_appbar_right);
            ((TextView) v.findViewById(R.id.actionbar_title)).setText(getTitle());
            v.findViewById(R.id.actionbar_btn_container).setOnClickListener(mOnClickListener);
            getActionBar().setCustomView(v);
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setDisplayShowCustomEnabled(true);
        }
    }

    private void setupUIComponents() {
        setupSearch();

        findViewById(R.id.add_item_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sv !=null){
                    sv.hide();
                }
                startEditItem();
            }
        });

        mSubAppBar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mSubAppBar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mInventoryList.setPadding(0, mSubAppBar.getHeight(), 0, 0);
            }
        });
    }

    private void setupSearch() {
        final SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) mSubAppBar.findViewById(R.id.search_inventory_view);
        mSearchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        mSearchView.setOnQueryTextListener(mOnQueryTextListener);
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                final LinearLayout filterContainer = (LinearLayout) mSubAppBar.findViewById(R.id.filter_container);
                filterContainer.setVisibility(View.VISIBLE);
                mSubAppBar.clearFilter();
                return false;
            }
        });
        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LinearLayout filterContainer = (LinearLayout) mSubAppBar.findViewById(R.id.filter_container);
                filterContainer.setVisibility(View.GONE);
                mSubAppBar.clearFilter();
                mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            if (mSearchView.getQuery().toString().isEmpty()) {
                                filterContainer.setVisibility(View.VISIBLE);
                                mSearchView.setIconified(true);
                            }
                            mSearchView.setOnQueryTextFocusChangeListener(null);
                        }
                    }
                });
            }
        });


        if (mSavedSearchQuery != null && !mSavedSearchQuery.isEmpty()) {
            mSearchView.setIconified(false);
            mSearchView.setQuery(mSavedSearchQuery, false);
        }
    }


    private void initializeNavigationDrawer() {
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setHomeButtonEnabled(true);
        }
        mDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mDrawerFragment.init((DrawerLayout) findViewById(R.id.drawer_layout));
    }

    private void initializeRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this);
        mEmptyViewWidget = (EmptyViewWidget) findViewById(R.id.pantry_list_empty_view);
        mEmptyViewWidget.setLabel(R.string.empty_view_widget_active_label);
        mEmptyViewWidget.setAction(R.string.empty_view_widget_active_button);
        mEmptyViewWidget.setActionListener(mOnClickListener);
        mInventoryList = (SmartRecyclerView) findViewById(R.id.pantry_list_view);
        mInventoryList.setLayoutManager(mLayoutManager);
        fab = (FloatingActionButton) findViewById(R.id.add_item_button);
        fab.attachToRecyclerView(mInventoryList);

        mAdapter = new InventoryRecyclerAdapter(this, null);
        mAdapter.setMultiSelection(mMultiSelection);
        mInventoryList.setAdapter(mAdapter);
        mInventoryList.setEmptyView(mEmptyViewWidget);
        mInventoryList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            private int lastKnownFirst = 0;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                AnimationUtility.moveByAmount(mSubAppBar, dy * -1);

                final int firstVisiblePosition = mLayoutManager.findFirstVisibleItemPosition() - 1;
                final int lastVisiblePosition = mLayoutManager.findLastVisibleItemPosition() + 1;
                final int totalItemCount = mLayoutManager.getItemCount();
                for (int i = 0; i < totalItemCount; i++) {
                    if (i < firstVisiblePosition || i > lastVisiblePosition) {
                        final Intent collapseIntent = new Intent(UIConstants.ACTION_VIEW_HOLDER_COLLAPSE);
                        collapseIntent.putExtra(UIConstants.EXTRA_VIEW_HOLDER_COLLAPSE, i);
                        LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(collapseIntent);
                    }
                }
                if (mLayoutManager.findFirstVisibleItemPosition() > lastKnownFirst) {
                    fab.hide();
                } else if (mLayoutManager.findFirstVisibleItemPosition() < lastKnownFirst) {
                    fab.show();
                }
                lastKnownFirst = mLayoutManager.findFirstVisibleItemPosition();
            }
        });
    }

    private void collapseQuickQuantities() {
        final Intent collapseIntent = new Intent(UIConstants.ACTION_VIEW_HOLDER_COLLAPSE_ALL);
        LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(collapseIntent);
    }

    private void maybeSetupAlarm() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // Setup alarm the very first launch, then boot receiver will take care of it
        if (!prefs.getBoolean(AlarmsUtility.PREF_ALARM_SET, false) && prefs.getBoolean(SettingsFragment.PREF_KEY_NOTIFICATION_TOGGLE, true)) {
            AlarmsUtility.setupAlarm(this);
        }
    }

    private void switchToSearchLoader(final String search) {
        final Bundle bundle = new Bundle();
        bundle.putString(EXTRA_SEARCH_QUERY, search);
        // Reset the adapter to prevent a known crash happening in the recycler view
        initializeRecyclerView();
        if (destroyAnyLoaderButSelf(UIConstants.LOADER_SEARCH)) {
            getLoaderManager().initLoader(UIConstants.LOADER_SEARCH, bundle, this);
            return;
        }
        getLoaderManager().restartLoader(UIConstants.LOADER_SEARCH, bundle, this);
    }

    private void resetSearchBar(){
        //Search is no longer valid. Reset the search bar.
        final LinearLayout filterContainer = (LinearLayout) mSubAppBar.findViewById(R.id.filter_container);
        filterContainer.setVisibility(View.VISIBLE);
        mSubAppBar.clearFilter();
        mSearchView.setQuery("", false);
        mSearchView.clearFocus();
        mSearchView.setIconified(true);
    }

    private void switchToActiveInactiveLoader(Boolean isActive) {

        resetSearchBar();
        ActionExecuter.getInstance().clearLastAction();
        initializeRecyclerView();
        destroyAnyLoaderButSelf(isActive ? UIConstants.LOADER_ACTIVE_INVENTORY_ITEMS : UIConstants.LOADER_INACTIVE_INVENTORY_ITEMS);
        mEmptyViewWidget.setActionVisibility(isActive ? View.VISIBLE : View.GONE);
        toggleLoader(isActive);
    }

    private void switchToCurrentActiveLoader() {
        initializeRecyclerView();
        toggleLoader(mIsActiveListShowing);
    }

    private boolean destroyAnyLoaderButSelf(int selfID) {
        if (selfID != UIConstants.LOADER_INVENTORY_ITEMS && getLoaderManager().getLoader(UIConstants.LOADER_INVENTORY_ITEMS) != null) {
            getLoaderManager().destroyLoader(UIConstants.LOADER_INVENTORY_ITEMS);
            return true;
        } else if (selfID != UIConstants.LOADER_SEARCH && getLoaderManager().getLoader(UIConstants.LOADER_SEARCH) != null) {
            getLoaderManager().destroyLoader(UIConstants.LOADER_SEARCH);
            return true;
        } else if (selfID != UIConstants.LOADER_ACTIVE_INVENTORY_ITEMS && getLoaderManager().getLoader(UIConstants.LOADER_ACTIVE_INVENTORY_ITEMS) != null) {
            getLoaderManager().destroyLoader(UIConstants.LOADER_ACTIVE_INVENTORY_ITEMS);
            return true;
        } else if (selfID != UIConstants.LOADER_INACTIVE_INVENTORY_ITEMS && getLoaderManager().getLoader(UIConstants.LOADER_INACTIVE_INVENTORY_ITEMS) != null) {
            getLoaderManager().destroyLoader(UIConstants.LOADER_INACTIVE_INVENTORY_ITEMS);
            return true;
        } else if (selfID != UIConstants.LOADER_ACTIVE_FILTER_INVENTORY_ITEMS && getLoaderManager().getLoader(UIConstants.LOADER_ACTIVE_FILTER_INVENTORY_ITEMS) != null) {
            getLoaderManager().destroyLoader(UIConstants.LOADER_ACTIVE_FILTER_INVENTORY_ITEMS);
            return true;
        } else if (selfID != UIConstants.LOADER_INACTIVE_FILTER_INVENTORY_ITEMS && getLoaderManager().getLoader(UIConstants.LOADER_INACTIVE_FILTER_INVENTORY_ITEMS) != null) {
            getLoaderManager().destroyLoader(UIConstants.LOADER_INACTIVE_FILTER_INVENTORY_ITEMS);
            return true;
        }
        return false;
    }

    private void toggleLoader(boolean isActive) {
        if (isActive) {
            fab.show();
            getLoaderManager().initLoader(UIConstants.LOADER_ACTIVE_INVENTORY_ITEMS, null, this);
        } else {
            fab.hide();
            getLoaderManager().initLoader(UIConstants.LOADER_INACTIVE_INVENTORY_ITEMS, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle args) {
        switch (loaderID) {
            case UIConstants.LOADER_INVENTORY_ITEMS:
                return new CursorLoader(this, MyContract.inventoryItemsUri(), null, null, null, null);
            case UIConstants.LOADER_SEARCH:
                return new CursorLoader(this, MyContract.inventoryItemsSearchUri(args.getString(EXTRA_SEARCH_QUERY)), null, null, null, null);
            case UIConstants.LOADER_ACTIVE_INVENTORY_ITEMS:
                return new CursorLoader(this, MyContract.inventoryItemsActiveUri(), null, null, null, null);
            case UIConstants.LOADER_INACTIVE_INVENTORY_ITEMS:
                return new CursorLoader(this, MyContract.inventoryItemsInactiveUri(), null, null, null, null);
            case UIConstants.LOADER_ACTIVE_FILTER_INVENTORY_ITEMS:
                return new CursorLoader(this, MyContract.inventoryItemsFilterActiveByInventoryUuidUri(mFilterSelectedUUID), null, null, null, null);
            case UIConstants.LOADER_INACTIVE_FILTER_INVENTORY_ITEMS:
                return new CursorLoader(this, MyContract.inventoryItemsFilterInactiveByInventoryUuidUri(mFilterSelectedUUID), null, null, null, null);
            case UIConstants.LOADER_LOCATION_LIST:
                return new CursorLoader(this, MyContract.inventoriesUri(), null, null, null, null);
            case UIConstants.LOADER_INACTIVE_COUNT_INVENTORY_ITEMS:
                return new CursorLoader(this, MyContract.inventoryItemsInactiveCountUri(), null, null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case UIConstants.LOADER_LOCATION_LIST:
                mSubAppBar.setupFilterSpinner(data);
                AnimationUtility.resetMoveToZero(mSubAppBar);
                break;
            case UIConstants.LOADER_INACTIVE_COUNT_INVENTORY_ITEMS:
                data.moveToFirst();
                InventoryItemReader reader = InventoryItemReader.getInstance(data);
                mActionBarSplat.setText("" + reader.getCount());
                getContentResolver().registerContentObserver(MyContract.inventoryItemsInactiveCountUri(), false, mInactiveItemContentObserver);
                break;
            default:
                mAdapter.swapCursor(data);
                mInventoryList.showEmptyView(data.getCount() == 0);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mInventoryList.showEmptyView(false);
    }

    private void startEditItem() {
        collapseQuickQuantities();
        final Intent intent = new Intent(this, InsertItemActivity.class);
        intent.putExtra(InsertItemActivity.EXTRA_INVENTORY_NAME, mFilterSelectionLocation);
        startActivity(intent);
    }

    private void startEditItemFromID(final String itemID) {
        collapseQuickQuantities();
        final Intent intent = new Intent(this, EditItemActivity.class);
        intent.putExtra(AbstractManipulationActivity.EXTRA_ITEM_ID, itemID);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (mSearchView != null && StringUtils.isNotEmpty(mSearchView.getQuery().toString())) {
            outState.putString(KEY_STATE_SEARCH_QUERY, mSearchView.getQuery().toString());
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerFragment.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerFragment.syncState();
    }

    private void filterByLocation(final String selectedLocation) {
        mFilterSelectionLocation = selectedLocation;
        final Cursor invCursor = getContentResolver().query(MyContract.inventoriesByNameUri(selectedLocation), null, null, null, null);
        if (invCursor.moveToFirst()) {
            mFilterSelectedUUID = InventoryReader.getInstance(invCursor).getUUID();
        } else {
            mFilterSelectedUUID = "";
            mFilterSelectionLocation = null;
        }
        invCursor.close();
        initializeRecyclerView();
        if (!mFilterSelectedUUID.isEmpty()) {
            if (destroyAnyLoaderButSelf(mIsActiveListShowing ? UIConstants.LOADER_ACTIVE_FILTER_INVENTORY_ITEMS : UIConstants.LOADER_INACTIVE_FILTER_INVENTORY_ITEMS)) {
                getLoaderManager().initLoader(mIsActiveListShowing ? UIConstants.LOADER_ACTIVE_FILTER_INVENTORY_ITEMS : UIConstants.LOADER_INACTIVE_FILTER_INVENTORY_ITEMS, null, MainActivity.this);
            } else {
                getLoaderManager().restartLoader(mIsActiveListShowing ? UIConstants.LOADER_ACTIVE_FILTER_INVENTORY_ITEMS : UIConstants.LOADER_INACTIVE_FILTER_INVENTORY_ITEMS, null, MainActivity.this);
            }
        } else {
            destroyAnyLoaderButSelf(mIsActiveListShowing ? UIConstants.LOADER_ACTIVE_INVENTORY_ITEMS : UIConstants.LOADER_INACTIVE_INVENTORY_ITEMS);
            getLoaderManager().initLoader(mIsActiveListShowing ? UIConstants.LOADER_ACTIVE_INVENTORY_ITEMS : UIConstants.LOADER_INACTIVE_INVENTORY_ITEMS, null, MainActivity.this);
        }
    }

    @Override
    public void onBackPressed() {
        if (!mSearchView.getQuery().toString().isEmpty()) {
            resetSearchBar();
        }else if (sv != null && sv.isShown()){
            sv.hide();
        }else if (!mIsActiveListShowing) {
            AnimationUtility.resetMoveToZero(mSubAppBar);
            mIsActiveListShowing = !mIsActiveListShowing;
            mSubAppBar.sendSelectionChangedMessage(mSubAppBar.getSelectedPosition());
            switchToActiveInactiveLoader(mIsActiveListShowing);
        } else {
            super.onBackPressed();
        }
    }

    private void displayShowcaseViewTwo(){
        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lps.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lps.addRule(RelativeLayout.CENTER_HORIZONTAL);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
        lps.setMargins(margin, margin, 300, 200);

        ViewTarget target = new ViewTarget(R.id.icon_appbar_right, this);
        sv = new ShowcaseView.Builder(this)
                .setContentText(getString(R.string.showcase_list_text))
                .setStyle(R.style.CustomShowcaseTheme)
                .hideOnTouchOutside()
                .setTarget(target)
                .setShowcaseEventListener(new OnShowcaseEventListener() {

                    @Override
                    public void onShowcaseViewShow(final ShowcaseView scv) {
                    }

                    @Override
                    public void onShowcaseViewHide(final ShowcaseView scv) {


                        scv.setVisibility(View.GONE);
                        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplication());
                        prefs.edit().putBoolean(UIConstants.PREF_MAIN_INITIAL_SHOWCASE, false).commit();

                    }

                    @Override
                    public void onShowcaseViewDidHide(final ShowcaseView scv) {
                    }

                })
                .build();
        sv.setButtonPosition(lps);
    }

    private void showOverlayTutorialOne(){
        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        lps.addRule(RelativeLayout.CENTER_HORIZONTAL);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
        lps.setMargins(margin, margin, 300, 200);
        ViewTarget target = new ViewTarget(R.id.add_item_button, this);
        sv = new ShowcaseView.Builder(this)
                .setContentTitle(getString(R.string.showcase_list_title))
                .setContentText(getString(R.string.showcase_add_text))
                .setTarget(target)
                .hideOnTouchOutside()
                .setStyle(R.style.CustomShowcaseTheme)
                .setShowcaseEventListener(new OnShowcaseEventListener() {

                    @Override
                    public void onShowcaseViewShow(final ShowcaseView scv) { }

                    @Override
                    public void onShowcaseViewHide(final ShowcaseView scv) {
                        scv.setVisibility(View.GONE);
                        displayShowcaseViewTwo();
                    }

                    @Override
                    public void onShowcaseViewDidHide(final ShowcaseView scv) { }

                })
                .build();
        sv.setButtonPosition(lps);
    }
}
