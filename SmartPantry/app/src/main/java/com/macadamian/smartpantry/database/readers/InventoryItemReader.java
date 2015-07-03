package com.macadamian.smartpantry.database.readers;

import android.database.Cursor;

import com.google.common.collect.Lists;
import com.macadamian.smartpantry.content.MyContract;
import com.macadamian.smartpantry.utility.comparators.AlphabeticalComparator;

import java.util.ArrayList;
import java.util.Collections;

public class InventoryItemReader extends AbstractReader {
    private InventoryItemReader(Cursor cursor) {
        super(cursor);
    }

    public static InventoryItemReader getInstance(Cursor cursor) {
        return new InventoryItemReader(cursor);
    }

    public String getItemUUID() {
        return getString(MyContract.InventoryItemEntry.COLUMN_ITEM_UUID);
    }

    public String getInventoryUUID() {
        return getString(MyContract.InventoryItemEntry.COLUMN_INVENTORY_UUID);
    }

    public String getBarcode() {
        return getString(MyContract.InventoryItemEntry.COLUMN_BARCODE);
    }

    public String getName() {
        return getString(MyContract.InventoryItemEntry.COLUMN_NAME);
    }

    public String getAliasedName() {
        return getString(MyContract.InventoryItemEntry.ALIAS_NAME);
    }

    public Integer getQuantity() {
        return getInt(MyContract.InventoryItemEntry.COLUMN_RELATIVE_QUANTITY);
    }

    public long getCategory() {
        return getLong(MyContract.InventoryItemEntry.COLUMN_CATEGORY);
    }

    public String getExpiry() {
        return getString(MyContract.InventoryItemEntry.COLUMN_EXPIRY);
    }

    public String getLastSynched() {
        return getString(MyContract.InventoryItemEntry.COLUMN_LAST_SYNCHED);
    }

    public String getUpdatedAt() {
        return getString(MyContract.InventoryItemEntry.COLUMN_UPDATED_AT);
    }

    public String getAliasedInventoryName() { return getString(MyContract.InventoryEntry.ALIAS_NAME); }

    public boolean getActive() {
        int active = getInt(MyContract.InventoryItemEntry.COLUMN_ACTIVE);
        return active == 1;
    }

    public boolean getNotified() {
        int notified = getInt(MyContract.InventoryItemEntry.COLUMN_NOTIFIED);
        return notified == 1;
    }

    public static ArrayList<String> extractAllNames(Cursor cursor) {
        final ArrayList<String> rv = Lists.newArrayList();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                rv.add(InventoryItemReader.getInstance(cursor).getAliasedName());
            }
        }
        Collections.sort(rv, new AlphabeticalComparator());
        return rv;
    }
}
