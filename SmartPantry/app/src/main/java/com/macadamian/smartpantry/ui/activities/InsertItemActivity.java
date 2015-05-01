package com.macadamian.smartpantry.ui.activities;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.macadamian.smartpantry.R;
import com.macadamian.smartpantry.content.MyContract;
import com.macadamian.smartpantry.database.readers.CategoryReader;
import com.macadamian.smartpantry.database.readers.ItemTemplateReader;
import com.macadamian.smartpantry.ui.UIConstants;
import com.macadamian.smartpantry.utility.InputUtility;

import java.util.Arrays;

public class InsertItemActivity extends AbstractManipulationActivity {

    private String mInventoryName;
    public final static String EXTRA_INVENTORY_NAME = "EXTRA_INVENTORY_NAME";

    @Override
    protected void saveEditedItemFields() {
        ContentValues newValues = addCurrentUIEditValues();
        ContentValues templateValues = itemTemplateValues();
        String itemName = templateValues.getAsString(MyContract.TemplateEntry.COLUMN_NAME);
        String itemBarcode = templateValues.getAsString(MyContract.TemplateEntry.COLUMN_BARCODE);
        //If we don't have an item name, do not make any modifications
        if (itemName.isEmpty()) {
            alertDialog(R.string.alert_title_insert, R.string.alert_sub_title_insert);
        } else {

            final String itemUUID = doesItemAlreadyExist(mNameLabel.getText().toString(), mExpiryLabel.getText().toString(), mLocationSpinner.getSelectedItem().toString());

            if (itemUUID !=null) {
                new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                        .setTitle(getString(R.string.item_dialog_title))
                        .setMessage(getString(R.string.item_dialog_sub_title_update_option))
                        .setPositiveButton(getString(R.string.item_dialog_update), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                closeActivity();
                                final Intent intent = new Intent(getApplicationContext(), EditItemActivity.class);
                                intent.putExtra(EditItemActivity.EXTRA_ITEM_ID, itemUUID);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(getString(R.string.item_dialog_edit), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
            } else {

                getContentResolver().insert(MyContract.inventoryItemsUri(), newValues);
                Cursor c = getContentResolver().query(MyContract.templateByBarcodeUri(itemBarcode), null, null, null, null);

                    if (c!= null && c.getCount() > 0) {
                        Log.v(TAG, "Item already exists in template table so update it");
                        getContentResolver().update(MyContract.templateByBarcodeUri(itemBarcode), templateValues, null, null);
                        c.close();
                    } else if (itemBarcode != null) {
                        getContentResolver().insert(MyContract.templatesUri(), templateValues);
                    }


                closeActivity();
            }
        }
    }

    private void closeActivity(){
        mAllItems.close();
        finish();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().hasExtra(EXTRA_ITEM_ID)) {
            mItemId = getIntent().getStringExtra(EXTRA_ITEM_ID);
        }

        if (getIntent().hasExtra(EXTRA_INVENTORY_NAME)){
            mInventoryName = getIntent().getStringExtra(EXTRA_INVENTORY_NAME);
        }

        mNameLabel.setText(null);
        mNameLabel.addTextChangedListener(mTextChangeListener);
        addInventoryChoices(mInventoryName);
        addCategoryChoices(null);
        activateExpDatePickButton();
        initBarcode(null);
        initDayLeftSpinner();

        Integer [] exclude = {R.id.item_name, R.id.submit_item_btn, R.id.discard_item_btn};
        InputUtility.setupUI(findViewById(R.id.edit_item_root_layout), this, Arrays.asList(exclude));

        mQuantityWidget.setSelectedQuantity(UIConstants.QUANTITY_LOTS);
    }

    private void initLocally(String name, String inventoryName, String categoryName, String barcode) {
        mNameLabel.setText(name);
        addInventoryChoices(inventoryName);
        addCategoryChoices(categoryName);
        initBarcode(barcode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean changesHadOccurred = mChangesOccurred;
        super.onActivityResult(requestCode, resultCode, data);
        if(!changesHadOccurred) {
            populateByBarcode();
        }
    }

    private void populateByBarcode() {
        //Pre-populate spinners..
        Cursor itemToCopyFrom = getContentResolver().query(MyContract.templateByBarcodeUri(mBarcodeValue), null, null, null, null);
        if (itemToCopyFrom != null && itemToCopyFrom.moveToFirst()) {
            String categoryName = "";
            ItemTemplateReader readerFrom = ItemTemplateReader.getInstance(itemToCopyFrom);
            String name = readerFrom.getName();
            final Cursor catCursor = getContentResolver().query(MyContract.categoryUri(Long.toString(readerFrom.getCategory())), null, null, null, null);
            if (catCursor != null && catCursor.moveToFirst()) {
                categoryName = CategoryReader.getInstance(catCursor).getName();
                catCursor.close();
            }

            String inventoryName = readerFrom.getAliasedInventoryName();

            initLocally(name, inventoryName, categoryName, mBarcodeValue);
        }
        if (itemToCopyFrom != null) {
            itemToCopyFrom.close();
        }
    }
}
