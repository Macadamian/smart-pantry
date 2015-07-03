package com.macadamian.smartpantry.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.macadamian.smartpantry.R;
import com.macadamian.smartpantry.content.MyContract;
import com.macadamian.smartpantry.database.readers.CategoryReader;
import com.macadamian.smartpantry.database.readers.InventoryItemReader;
import com.macadamian.smartpantry.database.readers.InventoryReader;
import com.macadamian.smartpantry.database.tables.CategoryTable;
import com.macadamian.smartpantry.database.tables.InventoryItemTable;
import com.macadamian.smartpantry.database.tables.InventoryTable;
import com.macadamian.smartpantry.database.tables.ItemTemplateTable;
import com.macadamian.smartpantry.ui.UIConstants;
import com.macadamian.smartpantry.ui.adapters.InventoryItemNameAdapter;
import com.macadamian.smartpantry.utility.InputUtility;
import com.macadamian.smartpantry.widgets.ImageToggleButton;
import com.macadamian.smartpantry.widgets.QuantityWidget;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public abstract class AbstractManipulationActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    protected final static String TAG = "ManipulationActivity";
    private final static int SPINNER_DEFAULT_POSITION = 0;
    private final static int SPINNER_PICK_A_DATE_POSITION = 8;
    private final static int REQUEST_CODE_EDIT_LOCATION = 1000;
    private final static int MAX_NAME_LENGTH = 50;

    //Constants for the argument bundle variable keys passed in to the fragment instance
    public final static String EXTRA_ITEM_ID = "EXTRA_ITEM_ID";

    private Button mSubmitButton;
    private ImageToggleButton mBarcodeToggle;
    protected AutoCompleteTextView mNameLabel;
    private InventoryItemNameAdapter mInventoryItemNameAdapter;
    protected TextView mExpiryLabel;
    protected Spinner mLocationSpinner;
    private Spinner mCategorySpinner;
    private Spinner mDayLeftSpinner;
    protected ShowcaseView sv;
    protected String mBarcodeValue;
    protected ArrayList<String> mAutoCompleteItems;
    protected boolean HasLocation = false;

    protected Cursor mItem;
    protected Cursor mAllItems;
    protected String mItemId;
    protected boolean mChangesOccurred = false;
    private SlidrInterface mSlidrInterface;

    private DatePickerDialog.OnDateSetListener dateListener;

    protected QuantityWidget mQuantityWidget;
    private final AdapterView.OnItemClickListener mOnAutoCompleteItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final String item = (String) mInventoryItemNameAdapter.getItem(position);
            mNameLabel.setText(item);
            mNameLabel.setSelection(mNameLabel.getText().length());
        }
    };


    private final TextView.OnEditorActionListener mOnEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                InputUtility.hideSoftKeyboard(AbstractManipulationActivity.this);
                return true;
            }
            return false;
        }
    };

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.barcode_btn:
                    if (sv !=null){
                        sv.hide();
                    }
                    scanBarcode();
                    break;
                case R.id.submit_item_btn:
                    saveEditedItemFields();
                    break;
                case R.id.discard_item_btn:
                    finish();
                    break;
            }
        }
    };

    protected abstract void saveEditedItemFields();

    protected final TextWatcher mTextChangeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            boolean empty = s.toString().isEmpty();
            boolean tooLong = s.toString().length() > MAX_NAME_LENGTH;
            boolean onlySpaces = s.toString().trim().isEmpty();
            mSubmitButton.setEnabled(!(empty || tooLong || !HasLocation || onlySpaces));
            changesOccurred();
        }

        @Override
        public void afterTextChanged(Editable s) {
            boolean empty = s.toString().isEmpty();
            boolean tooLong = s.toString().length() > MAX_NAME_LENGTH;
            boolean onlySpaces = s.toString().trim().isEmpty();
            if (empty) {
                mNameLabel.setError(getString(R.string.error_no_text));
            }else if (tooLong){
                mNameLabel.setError(getString(R.string.error_text_too_long));
            } else if (onlySpaces) {
                mNameLabel.setError(getString(R.string.error_only_spaces));
            }
        }
    };

    private final AdapterView.OnItemSelectedListener mOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {

        private boolean mCategoryFirstChanged = false;
        private boolean mInventoryFirstChanged = false;

        private String mLastSelectedInventoryName;

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (parent.getId() == R.id.spinner_location_property_values) {
                if (!mInventoryFirstChanged) {
                    mLastSelectedInventoryName = (String) parent.getAdapter().getItem(position);
                    mInventoryFirstChanged = true;
                    return;
                } else if (parent.getAdapter().getItem(position).equals(getString(R.string.spinner_item_add_inventory))) {
                    parent.setSelection(((ArrayAdapter<String>) parent.getAdapter()).getPosition(mLastSelectedInventoryName));
                    startEditLocationActivity();
                } else {
                    mLastSelectedInventoryName = (String) parent.getAdapter().getItem(position);
                }
            }
            if (parent.getId() == R.id.spinner_category_property_values && !mCategoryFirstChanged) {
                mCategoryFirstChanged = true;
                return;
            }
            changesOccurred();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        mNameLabel = (AutoCompleteTextView) findViewById(R.id.item_name);
        mNameLabel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mNameLabel.setOnTouchListener(null);
                 if (sv !=null){
                    sv.hide();
                 }
                return false;
            }
        });
        mNameLabel.setOnEditorActionListener(mOnEditorActionListener);
        mInventoryItemNameAdapter = new InventoryItemNameAdapter();
        mNameLabel.setAdapter(mInventoryItemNameAdapter);
        mNameLabel.setOnItemClickListener(mOnAutoCompleteItemClickListener);
        mExpiryLabel = (TextView) findViewById(R.id.expiration_picked_feedback);
        mSubmitButton = (Button) findViewById(R.id.submit_item_btn);
        mSubmitButton.setEnabled(false);
        mSubmitButton.setOnClickListener(mOnClickListener);
        Button discardButton = (Button) findViewById(R.id.discard_item_btn);
        discardButton.setOnClickListener(mOnClickListener);
        mBarcodeToggle = (ImageToggleButton) findViewById(R.id.barcode_btn);
        mBarcodeToggle.setOnClickListener(mOnClickListener);
        mLocationSpinner = (Spinner) findViewById(R.id.spinner_location_property_values);
        mCategorySpinner = (Spinner) findViewById(R.id.spinner_category_property_values);
        mDayLeftSpinner = (Spinner) findViewById(R.id.expiration_day_left);
        mAllItems = getContentResolver().query(MyContract.inventoryItemsUri(), null, null, null, null);
        mQuantityWidget = (QuantityWidget) findViewById(R.id.quantity_widget);
        mQuantityWidget.setButtonClickListener(new QuantityWidget.ClickCallback() {
            @Override
            public void onClick() {
                changesOccurred();
            }
        });
        mAutoCompleteItems = new ArrayList<String>();
        getLoaderManager().initLoader(UIConstants.LOADER_INVENTORY_ITEMS, null, this);
        mSlidrInterface = Slidr.attach(this);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplication());
        boolean initialShowcaseNotYetShown = prefs.getBoolean(UIConstants.PREF_EDIT_ADD_SHOWCASE, true);
        if(initialShowcaseNotYetShown) {
            displayShowcaseViewOne();
        }
    }

    private void displayShowcaseViewOne(){
        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lps.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lps.addRule(RelativeLayout.CENTER_HORIZONTAL);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
        lps.setMargins(margin, margin, 300, 200);

        ViewTarget target = new ViewTarget(R.id.barcode_btn, this);
        sv = new ShowcaseView.Builder(this)
                .setContentTitle(getString(R.string.showcase_barcode_title))
                .setContentText(getString(R.string.showcase_barcode_text))
                .setTarget(target)
                .hideOnTouchOutside()
                .setStyle(R.style.CustomShowcaseTheme)
                .setShowcaseEventListener(new OnShowcaseEventListener() {

                    @Override
                    public void onShowcaseViewShow(final ShowcaseView scv) { }

                    @Override
                    public void onShowcaseViewHide(final ShowcaseView scv) {
                        scv.setVisibility(View.GONE);
                        showOverlayTutorialTwo();
                    }

                    @Override
                    public void onShowcaseViewDidHide(final ShowcaseView scv) { }

                })
                .build();
        sv.setButtonPosition(lps);
    }

    private void showOverlayTutorialTwo(){
        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lps.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lps.addRule(RelativeLayout.CENTER_HORIZONTAL);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
        lps.setMargins(margin, margin, 300, 200);

        ViewTarget target = new ViewTarget(R.id.name_barcode_set_container, this);
        sv = new ShowcaseView.Builder(this)
                .setContentTitle(getString(R.string.showcase_editSwipe_title))
                .setContentText(getString(R.string.showcase_editSwipe_text))
                .setTarget(target)
                .hideOnTouchOutside()
                .setStyle(R.style.CustomShowcaseTheme)
                .setShowcaseEventListener(new OnShowcaseEventListener() {

                    @Override
                    public void onShowcaseViewShow(final ShowcaseView scv) {
                    }

                    @Override
                    public void onShowcaseViewHide(final ShowcaseView scv) {
                        scv.setVisibility(View.GONE);
                        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplication());
                        prefs.edit().putBoolean(UIConstants.PREF_EDIT_ADD_SHOWCASE, false).commit();
                    }

                    @Override
                    public void onShowcaseViewDidHide(final ShowcaseView scv) {
                    }

                })
                .build();
        sv.setButtonPosition(lps);
    }

    private void startEditLocationActivity() {
        final Intent intent = new Intent(this, ManageLocationsActivity.class);
        intent.putExtra(ManageLocationsActivity.EXTRA_STARTED_FOR_RESULT, true);
        startActivityForResult(intent, REQUEST_CODE_EDIT_LOCATION);
    }

    protected void initDayLeftSpinner() {
        List<String> dayChoices = Arrays.asList(getResources().getStringArray(R.array.day_left_array));
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item_expirations, R.id.list_item_expirations_label, dayChoices);
        mDayLeftSpinner.setAdapter(adapter);

        mDayLeftSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == SPINNER_DEFAULT_POSITION) {
                    return;
                }else if (position == SPINNER_PICK_A_DATE_POSITION){

                    new DatePickerDialog(AbstractManipulationActivity.this, dateListener, Calendar.getInstance()
                            .get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH),
                            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)).show();
                    return;
                }
                final Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, position);
                final int year = calendar.get(Calendar.YEAR);
                final String month = convertDateValueToString(calendar.get(Calendar.MONTH) + 1);
                final String day = convertDateValueToString(calendar.get(Calendar.DAY_OF_MONTH));
                mExpiryLabel.setVisibility(View.VISIBLE);
                mExpiryLabel.setText(String.format("%d-%s-%s", year, month, day));
                changesOccurred();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void addNewLocationButton(){
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.field_content);
        Button b = new Button(AbstractManipulationActivity.this);
        b.setText("Add new");
        b.setCompoundDrawablesWithIntrinsicBounds(R.drawable.alert_circle, 0, 0,0);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEditLocationActivity();
            }
        });
        mLocationSpinner.setVisibility(View.GONE);
        linearLayout.addView(b,0, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        HasLocation = false;
    }

    private void removeNewLocationButton(){
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.field_content);
        //Only remove it if necessary (that is if its a button)
        if (linearLayout.getChildAt(0) instanceof Button){
            linearLayout.removeViewAt(0);
            mLocationSpinner.setVisibility(View.VISIBLE);
            HasLocation = true;
        }
    }

    protected void addInventoryChoices(String inventoryName) {
        final Cursor cursor = getContentResolver().query(MyContract.inventoriesUri(), null, null, null, null);
        final ArrayList<String> inventoryListArray = InventoryTable.extractAllInventoryNames(cursor);

        if(inventoryListArray.size() == 0){
            addNewLocationButton();
        }else{
            HasLocation = true;
            removeNewLocationButton();
        }

        inventoryListArray.add(getString(R.string.spinner_item_add_inventory));
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item_locations, R.id.list_item_location_label, inventoryListArray);
        mLocationSpinner.setAdapter(adapter);
        if (inventoryName != null) {
            mLocationSpinner.setSelection(adapter.getPosition(inventoryName));
        }
        else {
            final SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            final String defaultInventoryName = mSharedPrefs.getString(UIConstants.PREF_DEFAULT_LOCATION, "");
            if (StringUtils.isNotEmpty(defaultInventoryName)) {
                mLocationSpinner.setSelection(adapter.getPosition(defaultInventoryName));
            }

        }
        cursor.close();
        mLocationSpinner.setOnItemSelectedListener(mOnItemSelectedListener);
    }

    protected void addCategoryChoices(String categoryName) {
        final Cursor cursor = getContentResolver().query(MyContract.categoriesUri(), null, null, null, null);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item_categories, R.id.list_item_category_label, CategoryTable.extractAllCategoryName(cursor));
        mCategorySpinner.setAdapter(adapter);

        if (categoryName != null) {
            mCategorySpinner.setSelection(adapter.getPosition(categoryName));
        }
        cursor.close();
        mCategorySpinner.setOnItemSelectedListener(mOnItemSelectedListener);
    }

    protected void initBarcode(final String barcode) {
        mBarcodeValue = barcode;
        mBarcodeToggle.setChecked(StringUtils.isNotEmpty(barcode));
    }

    /**
     * Registers a calendar date picker dialog for setting the expiration,
     * as well as a listener for once the date has been picked.
     */
    protected void activateExpDatePickButton() {
        dateListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker dView, int year, int month,
                                  int day) {
                TextView text = (TextView) findViewById(R.id.expiration_picked_feedback);
                String stringMonth = convertDateValueToString(month + 1);
                String stringDay = convertDateValueToString(day);
                text.setText(String.format("%s-%s-%s", year, stringMonth, stringDay));
                changesOccurred();
            }

        };
    }

    /**
     * Takes an int value and returns the string version of it, with a 0 added in front if the value < 10
     */
    private static String convertDateValueToString(int value) {

        String stringValue = String.valueOf(value);
        if (stringValue.length() == 1) {
            stringValue = "0" + stringValue;
        }
        return stringValue;
    }

    protected void alertDialog(int title, int subtitle) {
        new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                .setTitle(getString(title))
                .setMessage(getString(subtitle))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Fetches the currently set values on the various edit fragment UI pieces (spinners, TextEdit, etc)
     *
     * @return a ContentValues instance corresponding to each of the fragment UI item names, see private class constants
     */
    protected ContentValues addCurrentUIEditValues() {
        final String newInventoryName = mLocationSpinner.getSelectedItem().toString();
        final String newName = mNameLabel.getText().toString();
        final int newQuantity = mQuantityWidget.mSelectedQuantity;
        String newExpiry = mExpiryLabel.getText().toString();
        final String newCategory = mCategorySpinner.getSelectedItem().toString();

        final Cursor invCursor = getContentResolver().query(MyContract.inventoriesByNameUri(newInventoryName), null, null, null, null);
        invCursor.moveToFirst();
        String newInventoryUUID = InventoryReader.getInstance(invCursor).getUUID();
        invCursor.close();

        Long newCategoryId = null;
        final Cursor categoryCursor = getContentResolver().query(MyContract.categoriesByNameUri(newCategory), null, null, null, null);
        if(categoryCursor != null && categoryCursor.moveToFirst()) {
            newCategoryId = CategoryReader.getInstance(categoryCursor).getId();
            categoryCursor.close();
        }

        boolean updatedNotified = false;
        final Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if (newExpiry != null && !newExpiry.equals(format.format(calendar.getTime()))) {
            updatedNotified = true;
        }

        if (newExpiry != null && newExpiry.length() == 0){
            newExpiry = null;
        }

        String updatedAt = Long.toString(new Date().getTime());
        return InventoryItemTable.makeItem(newInventoryUUID, mBarcodeValue, newName, newQuantity, newExpiry, newCategoryId, updatedNotified, updatedAt, null);
    }

    protected ContentValues itemTemplateValues() {
        final String newName = mNameLabel.getText().toString();
        final String newCategory = mCategorySpinner.getSelectedItem().toString();
        final String newInventoryName = mLocationSpinner.getSelectedItem().toString();
        String newInventoryUUID;

        final Cursor invCursor = getContentResolver().query(MyContract.inventoriesByNameUri(newInventoryName), null, null, null, null);
        if (invCursor.moveToFirst()) {
            newInventoryUUID = InventoryReader.getInstance(invCursor).getUUID();
            invCursor.close();
        } else {
            newInventoryUUID = "";
        }

        final Cursor categoryCursor = getContentResolver().query(MyContract.categoriesByNameUri(newCategory), null, null, null, null);
        if (categoryCursor.moveToFirst()) {
            Long newCategoryId = CategoryReader.getInstance(categoryCursor).getId();
            categoryCursor.close();
            return ItemTemplateTable.makeTemplate(mBarcodeValue, newName, newCategoryId, newInventoryUUID);
        }
        return ItemTemplateTable.makeTemplate(mBarcodeValue, newName, 0, newInventoryUUID);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case IntentIntegrator.REQUEST_CODE:
                    onBarcodeResult(requestCode, resultCode, data);
                    break;
                case REQUEST_CODE_EDIT_LOCATION:
                    onEditLocationResult(data);
                    break;
            }
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onBarcodeResult(final int requestCode, final int resultCode, final Intent data) {
        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result.getContents() != null) {
            mBarcodeValue = result.getContents();
            Toast.makeText(this, mBarcodeValue, Toast.LENGTH_LONG).show();
            mBarcodeToggle.setChecked(true);
            changesOccurred();
        } else {
            Toast.makeText(this, getString(R.string.toast_no_barcode_captured), Toast.LENGTH_LONG).show();
        }
    }

    private void onEditLocationResult(final Intent data) {
        final String locationResult = data.getStringExtra(ManageLocationsActivity.EXTRA_ADDED_LOCATION_RESULT);
        if (StringUtils.isNotEmpty(locationResult)) {
            addInventoryChoices(locationResult);
            changesOccurred();
        }
    }

    public void onBackPressed() {

        if (sv != null && sv.isShown()){
            sv.hide();
        }else if (mNameLabel.getText().toString().isEmpty()) {
            showExitNoNameConfirmationDialog();
        } else if (mChangesOccurred) {
            showSaveConfirmationDialog();
        } else {
            finish();
        }
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    private void showExitNoNameConfirmationDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setTitle(R.string.dialog_exit_edit_no_name_title);
        builder.setMessage(R.string.dialog_exit_edit_no_name_message);
        builder.setPositiveButton(R.string.dialog_exit_edit_no_name_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton(R.string.dialog_exit_edit_no_name_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        });
        builder.show();
    }

    private void showSaveConfirmationDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setTitle(R.string.dialog_exit_edit_title);
        builder.setMessage(R.string.dialog_exit_edit_message);
        builder.setPositiveButton(R.string.dialog_exit_edit_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveEditedItemFields();
            }
        });
        builder.setNegativeButton(R.string.dialog_exit_edit_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    private void scanBarcode() {
        final IntentIntegrator intent = new IntentIntegrator(this);
        intent.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        intent.initiateScan();
    }

    private void changesOccurred() {
        mChangesOccurred = true;
        setItemButtonContainerVisible();
        mSlidrInterface.lock();
    }

    protected String doesItemAlreadyExist(String newName, String newExpiration, String newLocation) {
        if (newExpiration.equals(getString(R.string.expiry_label_filler))) {
            newExpiration = null;
        }

        String itemID = null;
        if (mItem != null && !mItem.isClosed() && mItem.moveToFirst() &&
                mItem.getColumnIndex(MyContract.InventoryItemEntry.COLUMN_ITEM_UUID) != -1) {
            itemID = mItem.getString(mItem.getColumnIndexOrThrow(MyContract.InventoryItemEntry.COLUMN_ITEM_UUID));
        }

        while (mAllItems.moveToNext()) {
            InventoryItemReader reader = InventoryItemReader.getInstance(mAllItems);

            String name = reader.getAliasedName();
            String expiration = reader.getExpiry();
            String location = reader.getAliasedInventoryName();
            if (name.equals(newName) && location.equals(newLocation) && StringUtils.equals(expiration, newExpiration)) {
                String UUID = reader.getItemUUID();

                if (itemID != null) {
                    if (itemID.equals(UUID)) {
                        mAllItems.moveToPosition(-1);
                        //we matched ourselves so don't return our UUID
                        return null;
                    } else {
                        //we are editing and we matched another item return its UUID
                        mAllItems.moveToPosition(-1);
                        return UUID;
                    }
                }

                mAllItems.moveToPosition(-1);
                return UUID;
            }
        }
        //reset cursor position
        mAllItems.moveToPosition(-1);
        return null;
    }

    protected void setItemButtonContainerVisible() {
        findViewById(R.id.item_btn_container).setVisibility(View.VISIBLE);
        boolean empty = mNameLabel.getText().toString().isEmpty();
        boolean tooLong = mNameLabel.getText().toString().length() > MAX_NAME_LENGTH;
        boolean onlySpaces = mNameLabel.getText().toString().trim().isEmpty();
        if (empty) {
            mNameLabel.setError(getString(R.string.error_no_text));
        }else if (tooLong) {
            mNameLabel.setError(getString(R.string.error_text_too_long));
        }else if (onlySpaces) {
            mNameLabel.setError(getString(R.string.error_only_spaces));
        } else if (HasLocation){
            mSubmitButton.setEnabled(true);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case UIConstants.LOADER_INVENTORY_ITEMS:
                return new CursorLoader(this, MyContract.inventoryItemsUri(), null,null,null,null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch(loader.getId()) {
            case UIConstants.LOADER_INVENTORY_ITEMS:
                ArrayList<String> names = InventoryItemReader.extractAllNames(data);
                if (names != null) {
                    mAutoCompleteItems.addAll(names);
                }
                break;
        }
        mInventoryItemNameAdapter.setData(mAutoCompleteItems);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
