package com.macadamian.smartpantry.ui.activities;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.macadamian.smartpantry.R;
import com.macadamian.smartpantry.content.MyContract;
import com.macadamian.smartpantry.database.readers.CategoryReader;
import com.macadamian.smartpantry.database.readers.InventoryItemReader;
import com.macadamian.smartpantry.ui.UIConstants;
import com.macadamian.smartpantry.utility.InputUtility;

import java.util.Arrays;

public class EditItemActivity extends AbstractManipulationActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().hasExtra(EXTRA_ITEM_ID)) {
            mItemId = getIntent().getStringExtra(EXTRA_ITEM_ID);
            getLoaderManager().initLoader(UIConstants.LOADER_EDIT_INVENTORY_ITEM, null, this);
        }
        else {
            Log.e(TAG, "Error : EditItemActivity called without an id");
        }

        Integer [] exclude = {R.id.item_name, R.id.submit_item_btn, R.id.discard_item_btn};
        InputUtility.setupUI(findViewById(R.id.edit_item_root_layout), this, Arrays.asList(exclude));
        InputUtility.hideSoftKeyboard(this);
    }

    private void initFromDatabase() {
        if (mItem != null && mItem.moveToFirst()) {
            InventoryItemReader reader = InventoryItemReader.getInstance(mItem);
            mNameLabel.setText(reader.getAliasedName());
            mNameLabel.setSelection(mNameLabel.getText().length());
            mNameLabel.addTextChangedListener(mTextChangeListener);
            String expiry = reader.getExpiry();
            if (expiry != null){
                mExpiryLabel.setVisibility(View.VISIBLE);
                mExpiryLabel.setText(expiry);
            }
            addInventoryChoices(reader.getAliasedInventoryName());

            final String categoryId = Long.toString(reader.getCategory());
            final Cursor catCursor = getContentResolver().query(MyContract.categoryUri(categoryId), null, null, null, null);
            if (catCursor != null && catCursor.moveToFirst()) {
                addCategoryChoices(CategoryReader.getInstance(catCursor).getName());
                catCursor.close();
                mQuantityWidget.setSelectedQuantity(reader.getQuantity(), reader.getActive());
            } else {
                addCategoryChoices(null);
            }
            activateExpDatePickButton();
            initBarcode(reader.getBarcode());
            initDayLeftSpinner();

            if(!reader.getActive()) {
                setItemButtonContainerVisible();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == UIConstants.LOADER_EDIT_INVENTORY_ITEM) {
            return new CursorLoader(this, MyContract.inventoryItemUri(mItemId), null, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == UIConstants.LOADER_EDIT_INVENTORY_ITEM) {
            mItem = data;
            initFromDatabase();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

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

            if (itemUUID != null) {
                new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                        .setTitle(getString(R.string.item_dialog_title))
                        .setMessage(getString(R.string.item_dialog_sub_title_no_insert))
                        .setPositiveButton(getString(R.string.item_dialog_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            } else {
                getContentResolver().update(MyContract.inventoryItemUri(mItemId), newValues, null, null);

                Cursor c = getContentResolver().query(MyContract.templateByBarcodeUri(itemBarcode), null, null, null, null);
                if (c.getCount() > 0) {
                    Log.v(TAG, "Item already exists in template table so update it");
                    getContentResolver().update(MyContract.templateByBarcodeUri(itemBarcode), templateValues, null, null);
                } else if (itemBarcode != null) {
                    getContentResolver().insert(MyContract.templatesUri(), templateValues);
                }
                c.close();
                mAllItems.close();
                finish();
            }
        }
    }
}
