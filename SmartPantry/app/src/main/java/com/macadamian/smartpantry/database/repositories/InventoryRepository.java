package com.macadamian.smartpantry.database.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.macadamian.smartpantry.content.MyContract;
import com.macadamian.smartpantry.database.tables.InventoryTable;


public class InventoryRepository extends AbstractRepository {
    private static final String TAG = "InventoryRepository";


    public InventoryRepository(final Context context) {
        super(context);
    }

    public Cursor getAllInventories() {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        queryBuilder.setTables(MyContract.InventoryEntry.TABLE_NAME);
        String orderBy =  "name ASC";

        Cursor inventoryCursor = queryBuilder.query(getReadableDatabase(), null, null, null, null, null, orderBy, null);
        return inventoryCursor;
    }

    public int removeItem(String inventoryUUID){
        int rv = -1;
        String whereClause = MyContract.InventoryEntry.COLUMN_INVENTORY_UUID + "= ?";
        String[] whereArugments = {inventoryUUID};

        getWritableDatabase().beginTransaction();
        try {
            rv = getWritableDatabase().delete(MyContract.InventoryEntry.TABLE_NAME, whereClause, whereArugments);
            getWritableDatabase().setTransactionSuccessful();
            notify(MyContract.inventoriesUri());
        } catch (Exception e) {
            Log.e(TAG, "Error during transaction - " + e);
        }
        getWritableDatabase().endTransaction();
        return rv;
    }

    public int editInventory(final String inventoryUUID, final ContentValues values) {
        int rv = -1;

        getWritableDatabase().beginTransaction();
        try {
            final String whereClause = MyContract.InventoryEntry.COLUMN_INVENTORY_UUID + "= ?";
            final String[] whereArgs = { inventoryUUID };
            rv = getWritableDatabase().update(MyContract.InventoryEntry.TABLE_NAME, values, whereClause, whereArgs);
            getWritableDatabase().setTransactionSuccessful();
            notify(MyContract.inventoriesUri());
            notify(MyContract.inventoryItemsUri());
        } catch (Exception e) {
            Log.e(TAG, "Error during transaction - " + e);
        }
        getWritableDatabase().endTransaction();
        return rv;
    }

    public Cursor getInventoryUUIDByName(String inventoryName){
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        queryBuilder.setTables(MyContract.InventoryEntry.TABLE_NAME);
        String[] columns = {MyContract.InventoryEntry.COLUMN_INVENTORY_UUID};
        String selection = InventoryTable.tableQualifiedColumn(MyContract.InventoryEntry.COLUMN_NAME) + "= ?";
        DatabaseUtils.appendEscapedSQLString(new StringBuilder(), inventoryName);
        String[] selectionArgs = {inventoryName};
        String limit = "1";

        return queryBuilder.query(getReadableDatabase(), columns, selection, selectionArgs, null, null, null, limit);
    }

    public long addInventory(final ContentValues values){
        long rv = -1L;

        getWritableDatabase().beginTransaction();
        try {
            rv = getWritableDatabase().insert(MyContract.InventoryEntry.TABLE_NAME, null, values);
            getWritableDatabase().setTransactionSuccessful();
            notify(MyContract.inventoriesUri());

        } catch (final Exception e) {
            Log.e(TAG, "Error during transaction - " + e);
        }
        getWritableDatabase().endTransaction();
        return rv;
    }
}


