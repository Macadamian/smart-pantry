package com.macadamian.smartpantry.ui.activities;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.Lists;
import com.macadamian.smartpantry.R;
import com.macadamian.smartpantry.content.MyContract;
import com.macadamian.smartpantry.database.readers.InventoryReader;
import com.macadamian.smartpantry.database.tables.InventoryTable;
import com.macadamian.smartpantry.ui.UIConstants;
import com.macadamian.smartpantry.ui.adapters.LocationCursorAdapter;
import com.macadamian.smartpantry.ui.fragments.dialogs.DialogEditLocation;
import com.macadamian.smartpantry.utility.InputUtility;
import com.macadamian.smartpantry.utility.Toaster;

import java.util.ArrayList;
import java.util.Arrays;

public class ManageLocationsActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public final static String EXTRA_STARTED_FOR_RESULT = "EXTRA_STARTED_FOR_RESULT";
    public final static String EXTRA_ADDED_LOCATION_RESULT = "EXTRA_ADDED_LOCATION_RESULT";
    public final static int MAX_NAME_LENGTH = 50;

    private final String FRAGMENT_EDIT_LOCATION = "FRAGMENT_EDIT_LOCATION";
    private ListView mList;
    private EditText mEdit;
    private Button mButton;
    private LocationCursorAdapter mAdapter;
    // Indicates whether the user has made any changes to the EditText in this 'session'
    private boolean freshNameEntrySession = true;


    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().trim().length() == 0) {
                if(!freshNameEntrySession) {
                    mEdit.setError(getString(R.string.error_no_text));
                }
                mButton.setEnabled(false);
                return;
            }else if (s.toString().trim().length() > MAX_NAME_LENGTH){
                mEdit.setError(getString(R.string.error_text_too_long));
                mButton.setEnabled(false);
                return;
            }
            freshNameEntrySession = false;
            final Cursor cursor = getContentResolver().query(MyContract.inventoriesByNameUri(s.toString()), null, null, null, null);
            mButton.setEnabled(cursor == null || cursor.getCount() <= 0);
            if (!mButton.isEnabled()) {
                mEdit.setError(getString(R.string.edit_location_inventory_already_exists));
            }
            else {
                mEdit.setError(null);
            }
            if (cursor != null) {
                cursor.close();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            // If content is empty ADD should be disabled, no empty check required
        }
    };

    private void addLocation() {
        final String addedLocation = mEdit.getText().toString().trim();
        getContentResolver().insert(MyContract.inventoriesUri(), InventoryTable.makeInventory(addedLocation));
        freshNameEntrySession = true;
        mEdit.setText("");
        if (getIntent().hasExtra(EXTRA_STARTED_FOR_RESULT) && getIntent().getBooleanExtra(EXTRA_STARTED_FOR_RESULT, false)) {
            final Intent intent = new Intent();
            intent.putExtra(EXTRA_ADDED_LOCATION_RESULT, addedLocation);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.add_location_button)  {
                addLocation();
            }
        }
    };

    private final AbsListView.MultiChoiceModeListener mMultiChoiceModeListener = new AbsListView.MultiChoiceModeListener() {
        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            updateItemVisibility(mode);
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_edit_locations, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            updateItemVisibility(mode);
            return true;
        }

        private void updateItemVisibility(final ActionMode mode) {
            mode.setTitle(getResources().getQuantityString(R.plurals.multi_selection_cab_title, mList.getCheckedItemCount(), mList.getCheckedItemCount()));
            mode.getMenu().findItem(R.id.item_edit).setVisible(mList.getCheckedItemCount() == 1);
            mode.getMenu().findItem(R.id.item_default).setVisible(mList.getCheckedItemCount() == 1);
            mode.getMenu().findItem(R.id.item_delete).setVisible(mList.getCheckedItemCount() > 0);

            // Don't show favorite option if already the default location
            if (mList.getCheckedItemCount() > 0) {
                final Pair<String, String> pair = getCheckedItemUUIDName();
                final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                if (pair.second.equals(sharedPrefs.getString(UIConstants.PREF_DEFAULT_LOCATION, ""))) {
                    mode.getMenu().findItem(R.id.item_default).setVisible(false);
                }
            }
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.item_edit:
                    editLocation();
                    mode.finish();
                    return true;
                case R.id.item_delete:
                    deleteLocation();
                    mode.finish();
                    return true;
                case R.id.item_default:
                    defaultLocation();
                    mode.finish();
                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
        }

        private void defaultLocation() {
            final Pair<String, String> pair = getCheckedItemUUIDName();
            final SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            mSharedPrefs.edit().putString(UIConstants.PREF_DEFAULT_LOCATION, pair.second).apply();
            mAdapter.notifyDataSetChanged();
        }

        private void editLocation() {
            final Pair<String, String> pair = getCheckedItemUUIDName();
            final DialogEditLocation dialog = DialogEditLocation.getInstance(pair.first, pair.second);
            dialog.show(getFragmentManager(), FRAGMENT_EDIT_LOCATION);
        }

        private void deleteLocation() {
            final ArrayList<String> checkedItemPositions = getCheckedItemUUID();
            new AlertDialog.Builder(ManageLocationsActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                    .setTitle(getResources().getQuantityString(R.plurals.alert_delete_inventory_title, checkedItemPositions.size()))
                    .setMessage(getResources().getQuantityString(R.plurals.alert_delete_inventories_sub_title, checkedItemPositions.size()))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            boolean inventoryInUse = false;
                            for (String uuid : checkedItemPositions) {
                                final Cursor checkCursor = getContentResolver().query(MyContract.inventoryItemsByInventoryUuidUri(uuid), null, null, null, null);
                                if (checkCursor.getCount() > 0) {
                                    inventoryInUse = true;
                                } else {
                                    getContentResolver().delete(MyContract.inventoriesByUuidUri(uuid), null, null);
                                }
                                checkCursor.close();
                            }
                            if (inventoryInUse) {
                                Toaster.makeText(ManageLocationsActivity.this, getResources().getQuantityString(R.plurals.inventory_currently_in_use, checkedItemPositions.size()), Toast.LENGTH_LONG);
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(R.drawable.ic_delete_grey600_48dp)
                    .show();
        }
    };

    private ArrayList<String> getCheckedItemUUID() {
        final SparseBooleanArray checkedItem = mList.getCheckedItemPositions();
        final ArrayList<String> checkedItemList = Lists.newArrayList();
        for (int i=0; i < mAdapter.getCount(); i++) {
            if (checkedItem.get(i)) {
                final Cursor cursor = (Cursor) mAdapter.getItem(i);
                checkedItemList.add(InventoryReader.getInstance(cursor).getUUID());
            }
        }
        return checkedItemList;
    }

    private Pair<String, String> getCheckedItemUUIDName() {
        final SparseBooleanArray checkedItem = mList.getCheckedItemPositions();
        for (int i=0; i < mAdapter.getCount(); i++) {
            if (checkedItem.get(i)) {
                final Cursor cursor = (Cursor) mAdapter.getItem(i);
                final InventoryReader reader = InventoryReader.getInstance(cursor);
                final Pair pair = new Pair<String, String>(reader.getUUID(), reader.getName());
                return pair;
            }
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_locations);

        mAdapter = new LocationCursorAdapter(this);
        mList = (ListView) findViewById(R.id.edit_location_list);
        mList.setAdapter(mAdapter);
        mList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        mList.setMultiChoiceModeListener(mMultiChoiceModeListener);
        mEdit = (EditText) findViewById(R.id.edit_location_input);
        mEdit.addTextChangedListener(mTextWatcher);
        mEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == 10) {
                    addLocation();
                }
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return true;
            }
        });
        mButton = (Button) findViewById(R.id.add_location_button);
        mButton.setOnClickListener(mOnClickListener);

        Integer [] exclude = {R.id.edit_location_input};
        InputUtility.setupUI(getWindow().getDecorView().findViewById(android.R.id.content), this, Arrays.asList(exclude));

        getLoaderManager().initLoader(UIConstants.LOADER_LOCATION_LIST, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == UIConstants.LOADER_LOCATION_LIST) {
            return new CursorLoader(this, MyContract.inventoriesUri(), null, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == UIConstants.LOADER_LOCATION_LIST) {
            mAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == UIConstants.LOADER_LOCATION_LIST) {
            mAdapter.swapCursor(null);
        }
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

}
