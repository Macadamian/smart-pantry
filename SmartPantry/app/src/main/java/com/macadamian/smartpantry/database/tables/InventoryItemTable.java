package com.macadamian.smartpantry.database.tables;

import android.content.ContentValues;

import com.macadamian.smartpantry.content.MyContract.InventoryItemEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class InventoryItemTable {

    public static final List<String> AllColumns =
                        new ArrayList<String>(Arrays.asList( InventoryItemEntry._ID,
                                InventoryItemEntry.COLUMN_ITEM_UUID,
                                InventoryItemEntry.COLUMN_INVENTORY_UUID,
                                InventoryItemEntry.COLUMN_BARCODE,
                                InventoryItemEntry.COLUMN_NAME,
                                InventoryItemEntry.COLUMN_RELATIVE_QUANTITY,
                                InventoryItemEntry.COLUMN_CATEGORY,
                                InventoryItemEntry.COLUMN_EXPIRY,
                                InventoryItemEntry.COLUMN_LAST_SYNCHED,
                                InventoryItemEntry.COLUMN_UPDATED_AT,
                                InventoryItemEntry.COLUMN_ACTIVE));

    public static String tableQualifiedColumn (String column)
    {
        assert (AllColumns.contains(column));
        return InventoryItemEntry.TABLE_NAME + "." + column;
    }

    public static ContentValues makeItem(String inventoryID, String barcode, String name, int relQuantity, String expiry, long category, boolean updateNotified, String updatedAt, String itemID) {
        ContentValues values = new ContentValues();
        values.put(InventoryItemEntry.COLUMN_ITEM_UUID,         itemID != null ? itemID : getRandomUUID());
        values.put(InventoryItemEntry.COLUMN_INVENTORY_UUID,    inventoryID);
        values.put(InventoryItemEntry.COLUMN_BARCODE,           barcode);
        values.put(InventoryItemEntry.COLUMN_NAME,              name);
        values.put(InventoryItemEntry.COLUMN_RELATIVE_QUANTITY, relQuantity);
        values.put(InventoryItemEntry.COLUMN_EXPIRY,            expiry);
        values.put(InventoryItemEntry.COLUMN_CATEGORY,          category);
        values.put(InventoryItemEntry.COLUMN_ACTIVE,            1);
        values.put(InventoryItemEntry.COLUMN_UPDATED_AT,        updatedAt);

        if (updateNotified) {
            values.put(InventoryItemEntry.COLUMN_NOTIFIED, 0);
        }
        if(relQuantity == 0){
            values.put(InventoryItemEntry.COLUMN_ACTIVE, false);
        }
        return values;
    }

    public static String getRandomUUID(){
        return UUID.randomUUID().toString();
    }

    public static ContentValues makeItemActive(boolean active) {
        ContentValues values = new ContentValues();
        values.put(InventoryItemEntry.COLUMN_ACTIVE, active);
        return values;
    }

    public static ContentValues makeItemNotified(boolean notified) {
        ContentValues values = new ContentValues();
        values.put(InventoryItemEntry.COLUMN_NOTIFIED, notified);
        return values;
    }

    public static ContentValues setNewQuantity(int quantity) {
        ContentValues values = new ContentValues();
        values.put(InventoryItemEntry.COLUMN_RELATIVE_QUANTITY, quantity);
        return values;
    }
}
