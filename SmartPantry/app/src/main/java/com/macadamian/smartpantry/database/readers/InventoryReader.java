package com.macadamian.smartpantry.database.readers;

import android.database.Cursor;

import com.macadamian.smartpantry.content.MyContract;

public class InventoryReader extends AbstractReader {

    private InventoryReader(Cursor cursor) {
        super(cursor);
    }

    public static InventoryReader getInstance(Cursor cursor) {
        return new InventoryReader(cursor);
    }

    public String getName() {
        return getString(MyContract.InventoryEntry.COLUMN_NAME);
    }

    public String getUUID() {
        return getString(MyContract.InventoryEntry.COLUMN_INVENTORY_UUID);
    }

    public String getLastSynched() {
        return getString(MyContract.InventoryEntry.COLUMN_LAST_SYNCHED);
    }

    public String getAliasName() { return getString(MyContract.InventoryEntry.ALIAS_NAME); }
}
