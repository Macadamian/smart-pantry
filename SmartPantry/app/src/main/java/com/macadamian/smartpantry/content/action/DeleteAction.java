package com.macadamian.smartpantry.content.action;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.util.Log;

import com.macadamian.smartpantry.R;
import com.macadamian.smartpantry.content.MyContract;
import com.macadamian.smartpantry.database.tables.InventoryItemTable;

import java.util.Date;

public class DeleteAction implements ActionInterface {

    private final Context mContext;
    private String mItemID;
    private ContentValues mReinsertValues = null;
    private final String TAG = "DeleteAction";
    private static final int INACTIVE = 0;

    public DeleteAction(Context context, String itemID) {
        mContext = context;
        mItemID = itemID;
    }

    @Override
    public void execute() {
        Cursor c = mContext.getContentResolver().query(MyContract.inventoryItemUri(mItemID), null, null, null, null);
        if (c != null && c.moveToFirst()) {
            mReinsertValues = convertCursorToContentValues(c);
            mReinsertValues.put(MyContract.InventoryItemEntry.COLUMN_ACTIVE, INACTIVE);
        } else {
            Log.e(TAG, "Unable to find item to re-insert");
        }

        mContext.getContentResolver().delete(MyContract.inventoryItemUri(mItemID), null, null);
    }

    @Override
    public void revert() {
        if (mReinsertValues != null) {
            mContext.getContentResolver().insert(MyContract.inventoryItemsUri(), mReinsertValues);
            mReinsertValues = null;
        }
    }

    private ContentValues convertCursorToContentValues(Cursor c) {
        String updatedAt = Long.toString(new Date().getTime());
        String inventoryID = c.getString(c.getColumnIndexOrThrow(MyContract.InventoryItemEntry.COLUMN_INVENTORY_UUID));
        String barcode = c.getString(c.getColumnIndexOrThrow(MyContract.InventoryItemEntry.COLUMN_BARCODE));
        String name = c.getString(c.getColumnIndexOrThrow(MyContract.InventoryItemEntry.ALIAS_NAME));
        Integer quantity = c.getInt(c.getColumnIndexOrThrow(MyContract.InventoryItemEntry.COLUMN_RELATIVE_QUANTITY));
        Long category = c.getLong(c.getColumnIndexOrThrow(MyContract.InventoryItemEntry.COLUMN_CATEGORY));
        String expiry = c.getString(c.getColumnIndexOrThrow(MyContract.InventoryItemEntry.COLUMN_EXPIRY));

        return InventoryItemTable.makeItem(inventoryID, barcode, name, quantity, expiry, category, false, updatedAt, mItemID);
    }
}
