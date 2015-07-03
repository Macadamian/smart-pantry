package com.macadamian.smartpantry.database.tables;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.common.collect.Lists;
import com.macadamian.smartpantry.content.MyContract.InventoryEntry;
import com.macadamian.smartpantry.database.readers.InventoryReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class InventoryTable{
    public static final List<String> AllColumns
                    = new ArrayList<String>(Arrays.asList(InventoryEntry._ID,
                                                     InventoryEntry.COLUMN_INVENTORY_UUID,
                                                     InventoryEntry.COLUMN_NAME,
                                                     InventoryEntry.COLUMN_LAST_SYNCHED));

    public static String tableQualifiedColumn (String column)
    {
        assert (AllColumns.contains(column));
        return InventoryEntry.TABLE_NAME + "." + column;
    }

    public static ArrayList<String> extractAllInventoryNames(Cursor cursor) {
        final ArrayList<String> rv = Lists.newArrayList();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                rv.add(InventoryReader.getInstance(cursor).getName());
            }
        }
        return rv;
    }

    public static ContentValues makeInventory(final String inventoryName) {
        final ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_INVENTORY_UUID, UUID.randomUUID().toString());
        values.put(InventoryEntry.COLUMN_NAME, inventoryName);
        return values;
    }

    public static ContentValues makeInventoryUpdate(final String inventoryUUID, final String inventoryName) {
        final ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_INVENTORY_UUID, inventoryUUID);
        values.put(InventoryEntry.COLUMN_NAME, inventoryName);
        return values;
    }
}
