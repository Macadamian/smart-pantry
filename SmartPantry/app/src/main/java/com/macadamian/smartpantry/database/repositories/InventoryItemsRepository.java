package com.macadamian.smartpantry.database.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;
import android.util.Log;

import com.macadamian.smartpantry.content.MyContract;
import com.macadamian.smartpantry.content.MyContract.CategoryEntry;
import com.macadamian.smartpantry.content.MyContract.InventoryEntry;
import com.macadamian.smartpantry.content.MyContract.InventoryItemEntry;
import com.macadamian.smartpantry.database.tables.CategoryTable;
import com.macadamian.smartpantry.database.tables.InventoryItemTable;
import com.macadamian.smartpantry.database.tables.InventoryTable;


public class InventoryItemsRepository extends AbstractRepository {
    private static final String TAG = "InventoryItemsRepo";
    private static final int ACTIVE = 1;
    private static final int INACTIVE = 0;
    private static final int NOTIFIED = 1;
    private static final int NOT_NOTIFIED = 0;

    public InventoryItemsRepository(Context context) {
        super(context);
    }

    public Cursor getAllItemsFromAllInventories() {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String inventoryItemName = MyContract.InventoryItemEntry.TABLE_NAME;
        String inventoryName = MyContract.InventoryEntry.TABLE_NAME;

        queryBuilder.setTables(String.format("%s LEFT OUTER JOIN %s ON (%s = %s)",
                inventoryItemName, inventoryName,
                InventoryItemTable.tableQualifiedColumn(MyContract.InventoryItemEntry.COLUMN_INVENTORY_UUID),
                InventoryTable.tableQualifiedColumn(MyContract.InventoryItemEntry.COLUMN_INVENTORY_UUID)));

        String[] columns = getItemDisplayColumns();
        String orderBy = InventoryItemTable.tableQualifiedColumn(MyContract.InventoryItemEntry.COLUMN_ACTIVE)  +" DESC, CASE WHEN " + MyContract.InventoryItemEntry.COLUMN_EXPIRY +
                " IS NULL THEN 1 ELSE 0 END ASC, " + MyContract.InventoryItemEntry.COLUMN_EXPIRY;

        return queryBuilder.query(getReadableDatabase(), columns, null, null, null, null, orderBy);
    }

    public Cursor getAllInventoryItemsByActive(Boolean isActive) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        final String inventoryItemName = MyContract.InventoryItemEntry.TABLE_NAME;
        final String inventoryName = MyContract.InventoryEntry.TABLE_NAME;

        queryBuilder.setTables(String.format("%s LEFT OUTER JOIN %s ON (%s = %s)",
                inventoryItemName, inventoryName,
                InventoryItemTable.tableQualifiedColumn(MyContract.InventoryItemEntry.COLUMN_INVENTORY_UUID),
                InventoryTable.tableQualifiedColumn(MyContract.InventoryItemEntry.COLUMN_INVENTORY_UUID)));

        String[] columns = getItemDisplayColumns();
        String selection = InventoryItemTable.tableQualifiedColumn(MyContract.InventoryItemEntry.COLUMN_ACTIVE) + "= ?";
        String[] selectionArgs = new String[]{isActive ? Integer.toString(ACTIVE) : Integer.toString(INACTIVE)};
        String orderBy = InventoryItemTable.tableQualifiedColumn(MyContract.InventoryItemEntry.COLUMN_ACTIVE)  +" DESC, CASE WHEN " + MyContract.InventoryItemEntry.COLUMN_EXPIRY +
                " IS NULL THEN 1 ELSE 0 END ASC, " + MyContract.InventoryItemEntry.COLUMN_EXPIRY;

        return queryBuilder.query(getReadableDatabase(), columns, selection, selectionArgs, null, null, orderBy);
    }

    public Cursor getAllInventoryItemsCountByActive(final boolean isActive) {
        final String inventoryItemName = MyContract.InventoryItemEntry.TABLE_NAME;
        final String inventoryName = MyContract.InventoryEntry.TABLE_NAME;
        final String tables = String.format("%s LEFT OUTER JOIN %s ON (%s = %s)",
                inventoryItemName, inventoryName,
                InventoryItemTable.tableQualifiedColumn(MyContract.InventoryItemEntry.COLUMN_INVENTORY_UUID),
                InventoryTable.tableQualifiedColumn(MyContract.InventoryItemEntry.COLUMN_INVENTORY_UUID));
        final String selection = InventoryItemTable.tableQualifiedColumn(MyContract.InventoryItemEntry.COLUMN_ACTIVE) + "= ?";
        final String[] selectionArgs = new String[]{isActive ? Integer.toString(ACTIVE) : Integer.toString(INACTIVE)};
        return getReadableDatabase().rawQuery("SELECT COUNT(*) AS " + BaseColumns._COUNT + " FROM " + tables + " WHERE " + selection, selectionArgs);
    }

    public Cursor getAllInventoryItemsByActiveAndLocation(Boolean isActive, String inventoryUUID) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String inventoryItemName = MyContract.InventoryItemEntry.TABLE_NAME;
        String inventoryName = MyContract.InventoryEntry.TABLE_NAME;

        queryBuilder.setTables(String.format("%s LEFT OUTER JOIN %s ON (%s = %s)",
                inventoryItemName, inventoryName,
                InventoryItemTable.tableQualifiedColumn(MyContract.InventoryItemEntry.COLUMN_INVENTORY_UUID),
                InventoryTable.tableQualifiedColumn(MyContract.InventoryItemEntry.COLUMN_INVENTORY_UUID)));

        String[] columns = getItemDisplayColumns();

        String selection = InventoryItemTable.tableQualifiedColumn(InventoryItemEntry.COLUMN_ACTIVE) + "= ?" +
                " AND " + InventoryItemTable.tableQualifiedColumn(InventoryItemEntry.COLUMN_INVENTORY_UUID) + "=?";
        String[] selectionArgs = new String[]{isActive ? Integer.toString(ACTIVE) : Integer.toString(INACTIVE), inventoryUUID};
        String orderBy = InventoryItemTable.tableQualifiedColumn(MyContract.InventoryItemEntry.COLUMN_ACTIVE)  +" DESC, CASE WHEN " + MyContract.InventoryItemEntry.COLUMN_EXPIRY +
                " IS NULL THEN 1 ELSE 0 END ASC, " + MyContract.InventoryItemEntry.COLUMN_EXPIRY;

        return queryBuilder.query(getReadableDatabase(), columns, selection, selectionArgs, null, null, orderBy);
    }


    public Cursor getAllActiveNotNotifiedInventoryItems() {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String inventoryItemName = MyContract.InventoryItemEntry.TABLE_NAME;
        String inventoryName = MyContract.InventoryEntry.TABLE_NAME;

        queryBuilder.setTables(String.format("%s LEFT OUTER JOIN %s ON (%s = %s)",
                inventoryItemName, inventoryName,
                InventoryItemTable.tableQualifiedColumn(MyContract.InventoryItemEntry.COLUMN_INVENTORY_UUID),
                InventoryTable.tableQualifiedColumn(MyContract.InventoryItemEntry.COLUMN_INVENTORY_UUID)));

        String[] columns = getItemDisplayColumns();
        String selection = InventoryItemTable.tableQualifiedColumn(MyContract.InventoryItemEntry.COLUMN_ACTIVE) + "= ? AND "
                + InventoryItemTable.tableQualifiedColumn(MyContract.InventoryItemEntry.COLUMN_NOTIFIED) + "= ?";
        String[] selectionArgs = new String[] { Integer.toString(ACTIVE), Integer.toString(NOT_NOTIFIED) };
        String orderBy = InventoryItemTable.tableQualifiedColumn(MyContract.InventoryItemEntry.COLUMN_ACTIVE)  +" DESC," + MyContract.InventoryItemEntry.COLUMN_EXPIRY;

        return queryBuilder.query(getReadableDatabase(), columns, selection, selectionArgs, null, null, orderBy);
    }


    public long addItem(final ContentValues values){
        long id = -1;
        getWritableDatabase().beginTransaction();
        try {
            id = getWritableDatabase().insert(MyContract.InventoryItemEntry.TABLE_NAME, null, values);
            getWritableDatabase().setTransactionSuccessful();
            if (id != -1) {
                notify(MyContract.inventoryItemsUri());
                notify(MyContract.inventoryItemsActiveCountUri());
                notify(MyContract.inventoryItemsInactiveCountUri());
            }

        } catch (Exception e) {
            Log.e(TAG, "Error during transaction - " + e);
        }
        getWritableDatabase().endTransaction();
        return id;
    }

    /**
     * Remove an item from the database. This should ONLY occur when the item was temporary
     * and the user changed their mind about the input
     * @param itemID
     */
    public int removeItem(String itemID){
        int rv = -1;
        String whereClause = MyContract.InventoryItemEntry.COLUMN_ITEM_UUID + "= ?";
        String[] selectionArgs = {itemID};

        getWritableDatabase().beginTransaction();
        try {
            rv = getWritableDatabase().delete(MyContract.InventoryItemEntry.TABLE_NAME, whereClause, selectionArgs);
            getWritableDatabase().setTransactionSuccessful();
            notify(MyContract.inventoryItemsUri());
            notify(MyContract.inventoryItemsActiveCountUri());
            notify(MyContract.inventoryItemsInactiveCountUri());
        } catch (Exception e) {
            Log.e(TAG, "Error during transaction - " + e);
        }
        getWritableDatabase().endTransaction();
        return rv;
    }

    /**
     * Alter an item to be active/inactive
     *
     * @param itemID the ID (PK) corresponding to the item to be altered
     * @param active boolean to indicate whether the item is to be active (true) or inactive (false
     */
    public int setItemActive(String itemID, boolean active){
        int rv = -1;
        String filter = MyContract.InventoryItemEntry.COLUMN_ITEM_UUID + "= ?";
        String[] filterArgs = {itemID};
        ContentValues value = new ContentValues();
        value.put(MyContract.InventoryItemEntry.COLUMN_ACTIVE, active ? ACTIVE : INACTIVE);

        getWritableDatabase().beginTransaction();
        try {
            rv = getWritableDatabase().update(MyContract.InventoryItemEntry.TABLE_NAME, value, filter, filterArgs);
            getWritableDatabase().setTransactionSuccessful();
            notify(MyContract.inventoryItemsUri());
            notify(MyContract.inventoryItemsActiveCountUri());
            notify(MyContract.inventoryItemsInactiveCountUri());
        } catch (Exception e) {
            Log.e(TAG, "Error while processing transaction - " + e);
        }
        getWritableDatabase().endTransaction();
        return rv;
    }

    public Cursor getSearchResult(final String name) {
        final SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        final String inventoryItemName = InventoryItemEntry.TABLE_NAME;
        final String inventoryName = InventoryEntry.TABLE_NAME;
        final String categoryName = CategoryEntry.TABLE_NAME;
        final StringBuilder tableBuilder = new StringBuilder();
        tableBuilder.append(String.format("%s LEFT OUTER JOIN %s ON (%s = %s)", inventoryItemName, inventoryName, InventoryItemTable.tableQualifiedColumn(MyContract.InventoryItemEntry.COLUMN_INVENTORY_UUID),
                InventoryTable.tableQualifiedColumn(MyContract.InventoryItemEntry.COLUMN_INVENTORY_UUID
                )));
        tableBuilder.append(String.format(" LEFT OUTER JOIN %s ON (%S = %s)", categoryName, InventoryItemTable.tableQualifiedColumn(MyContract.InventoryItemEntry.COLUMN_CATEGORY),
                CategoryTable.tableQualifiedColumn(MyContract.CategoryEntry._ID)));
        queryBuilder.setTables(tableBuilder.toString());
        final StringBuilder selectionBuilder = new StringBuilder();
        selectionBuilder.append(InventoryItemTable.tableQualifiedColumn(MyContract.InventoryItemEntry.COLUMN_NAME) + " LIKE ?");
        selectionBuilder.append(" OR " + CategoryTable.tableQualifiedColumn(CategoryEntry.COLUMN_NAME) + " LIKE ?");
        selectionBuilder.append(" OR " + InventoryTable.tableQualifiedColumn(InventoryEntry.COLUMN_NAME) + " LIKE ?");
        final String[] selectionArgs = new String[]{"%" + name + "%","%" + name + "%","%" + name + "%"};
        final String orderBy = InventoryItemTable.tableQualifiedColumn(InventoryItemEntry.COLUMN_ACTIVE) + " DESC," + InventoryItemEntry.COLUMN_EXPIRY;
        return queryBuilder.query(getReadableDatabase(), getSearchColumns(), selectionBuilder.toString(), selectionArgs, null, null, orderBy);
    }


    /**
     * Alter an item to be notified/not notified
     *
     * @param itemID the ID (PK) corresponding to the item to be altered
     * @param notified boolean to indicate whether the item is to be notified (true) or not notified (false
     */
    public int setItemNotified(String itemID, boolean notified){
        int rv = -1;
        String filter = MyContract.InventoryItemEntry._ID + "= ?";
        String[] filterArgs = {itemID};
        ContentValues value = new ContentValues();
        value.put(MyContract.InventoryItemEntry.COLUMN_NOTIFIED, notified ? NOTIFIED : NOT_NOTIFIED);

        getWritableDatabase().beginTransaction();
        try {
            rv = getWritableDatabase().update(MyContract.InventoryItemEntry.TABLE_NAME, value, filter, filterArgs);
            getWritableDatabase().setTransactionSuccessful();
            notify(MyContract.inventoryItemsUri());
        } catch (Exception e) {
            Log.e(TAG, "Error while processing transaction - " + e);
        }
        getWritableDatabase().endTransaction();
        return rv;
    }

    public int editItem(String itemID, ContentValues values){
        int rv = -1;
        String filter = MyContract.InventoryItemEntry.COLUMN_ITEM_UUID + "= ?";
        String[] filterArgs = {itemID};

        getWritableDatabase().beginTransaction();
        try {
            rv = getWritableDatabase().update(MyContract.InventoryItemEntry.TABLE_NAME, values, filter, filterArgs);
            getWritableDatabase().setTransactionSuccessful();
            notify(MyContract.inventoryItemsUri());
            notify(MyContract.inventoryItemUri(itemID));
            notify(MyContract.inventoryItemsActiveUri());
            notify(MyContract.inventoryItemsInactiveUri());
            notify(MyContract.inventoryItemsActiveCountUri());
            notify(MyContract.inventoryItemsInactiveCountUri());
        } catch (Exception e) {
            Log.e(TAG, "Error during transaction - " + e);
        }
        getWritableDatabase().endTransaction();
        return rv;
    }

    public Cursor getItem(String itemID){
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String inventoryItemName = MyContract.InventoryItemEntry.TABLE_NAME;
        String inventoryName = MyContract.InventoryEntry.TABLE_NAME;

        queryBuilder.setTables(String.format("%s LEFT OUTER JOIN %s ON (%s = %s)",
                inventoryItemName, inventoryName,
                InventoryItemTable.tableQualifiedColumn(MyContract.InventoryItemEntry.COLUMN_INVENTORY_UUID),
                InventoryTable.tableQualifiedColumn(MyContract.InventoryEntry.COLUMN_INVENTORY_UUID)));
        String[] columns = getItemDisplayColumns();
        String selection = InventoryItemTable.tableQualifiedColumn(MyContract.InventoryItemEntry.COLUMN_ITEM_UUID) + "= ?";
        String[] selectionArguments = {itemID};
        String orderBy = MyContract.InventoryItemEntry.COLUMN_EXPIRY;

        return queryBuilder.query(getReadableDatabase(), columns, selection, selectionArguments, null, null, orderBy);
    }

    /**
     * Checks whether an item already exists linked to this barcode, and returns it
     *
     * @param barcode the barcode string for the item to be found
     */
    public Cursor getItemByBarcode(String barcode){
        if(barcode == null) {
            return null;
        }
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String inventoryItemName = MyContract.InventoryItemEntry.TABLE_NAME;
        String inventoryName = MyContract.InventoryEntry.TABLE_NAME;

        queryBuilder.setTables(String.format("%s LEFT OUTER JOIN %s ON (%s = %s)",
                inventoryItemName, inventoryName,
                InventoryItemTable.tableQualifiedColumn(MyContract.InventoryItemEntry.COLUMN_INVENTORY_UUID),
                InventoryTable.tableQualifiedColumn(MyContract.InventoryEntry.COLUMN_INVENTORY_UUID)));
        String[] columns = getItemDisplayColumns();
        String selection = InventoryItemTable.tableQualifiedColumn(MyContract.InventoryItemEntry.COLUMN_BARCODE) + " LIKE ?";
        DatabaseUtils.appendEscapedSQLString(new StringBuilder(), barcode);
        String[] selectionArgs = {barcode};
        String orderBy = InventoryItemTable.tableQualifiedColumn(MyContract.InventoryItemEntry.COLUMN_UPDATED_AT +" DESC");
        String limit = "1";

        return queryBuilder.query(getReadableDatabase(), columns, selection, selectionArgs, null, null, orderBy, limit);
    }

    public Cursor getAllItemsAssociatedWithInventoryByInventoryUUID(String inventoryUUID){
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String inventoryItemName = MyContract.InventoryItemEntry.TABLE_NAME;
        String inventoryName = MyContract.InventoryEntry.TABLE_NAME;

        queryBuilder.setTables(String.format("%s LEFT OUTER JOIN %s ON (%s = %s)",
                inventoryItemName, inventoryName,
                InventoryItemTable.tableQualifiedColumn(MyContract.InventoryItemEntry.COLUMN_INVENTORY_UUID),
                InventoryTable.tableQualifiedColumn(MyContract.InventoryEntry.COLUMN_INVENTORY_UUID)));
        String selection = InventoryTable.tableQualifiedColumn(MyContract.InventoryItemEntry.COLUMN_INVENTORY_UUID) + "= ?";
        String[] selectionArguments = {inventoryUUID};
        //TODO if we want to use this for more than just validating if an inventory is in use do the following:
        //1) remove limit
        //2) add return columns
        String limit = "1";

        return queryBuilder.query(getReadableDatabase(), null, selection, selectionArguments, null, null, null, limit);
    }

    private static String[] getItemDisplayColumns(){
        return new String []{
                InventoryItemTable.tableQualifiedColumn(MyContract.InventoryItemEntry._ID),
                InventoryItemTable.tableQualifiedColumn(MyContract.InventoryItemEntry.COLUMN_NAME) + " AS " + MyContract.InventoryItemEntry.ALIAS_NAME,
                InventoryItemTable.tableQualifiedColumn(MyContract.InventoryItemEntry.COLUMN_INVENTORY_UUID),
                InventoryItemTable.tableQualifiedColumn(MyContract.InventoryItemEntry.COLUMN_ITEM_UUID),
                "date(" + InventoryItemTable.tableQualifiedColumn(MyContract.InventoryItemEntry.COLUMN_EXPIRY) + ") AS " + MyContract.InventoryItemEntry.COLUMN_EXPIRY,
                InventoryItemTable.tableQualifiedColumn(MyContract.InventoryItemEntry.COLUMN_RELATIVE_QUANTITY),
                InventoryItemTable.tableQualifiedColumn(MyContract.InventoryItemEntry.COLUMN_CATEGORY),
                InventoryItemTable.tableQualifiedColumn(MyContract.InventoryItemEntry.COLUMN_BARCODE),
                InventoryTable.tableQualifiedColumn(MyContract.InventoryEntry.COLUMN_NAME) + " AS " + MyContract.InventoryEntry.ALIAS_NAME,
                InventoryItemTable.tableQualifiedColumn(MyContract.InventoryItemEntry.COLUMN_ACTIVE),
                InventoryItemTable.tableQualifiedColumn(MyContract.InventoryItemEntry.COLUMN_NOTIFIED),
                InventoryItemTable.tableQualifiedColumn(MyContract.InventoryItemEntry.COLUMN_UPDATED_AT)};
    }

    private String[] getSearchColumns() {
        return new String []{
                InventoryItemTable.tableQualifiedColumn(InventoryItemEntry._ID),
                InventoryItemTable.tableQualifiedColumn(InventoryItemEntry.COLUMN_NAME) + " AS " + InventoryItemEntry.ALIAS_NAME,
                InventoryItemTable.tableQualifiedColumn(InventoryItemEntry.COLUMN_INVENTORY_UUID),
                InventoryItemTable.tableQualifiedColumn(InventoryItemEntry.COLUMN_ITEM_UUID),
                "date(" + InventoryItemTable.tableQualifiedColumn(InventoryItemEntry.COLUMN_EXPIRY) + ") AS " + InventoryItemEntry.COLUMN_EXPIRY,
                InventoryItemTable.tableQualifiedColumn(InventoryItemEntry.COLUMN_RELATIVE_QUANTITY),
                InventoryItemTable.tableQualifiedColumn(InventoryItemEntry.COLUMN_CATEGORY),
                InventoryItemTable.tableQualifiedColumn(InventoryItemEntry.COLUMN_BARCODE),
                InventoryTable.tableQualifiedColumn(InventoryEntry.COLUMN_NAME) + " AS " + InventoryEntry.ALIAS_NAME,
                InventoryItemTable.tableQualifiedColumn(InventoryItemEntry.COLUMN_ACTIVE),
                InventoryItemTable.tableQualifiedColumn(InventoryItemEntry.COLUMN_NOTIFIED),
                InventoryItemTable.tableQualifiedColumn(InventoryItemEntry.COLUMN_UPDATED_AT),
                CategoryTable.tableQualifiedColumn(CategoryEntry.COLUMN_NAME) + " AS " + CategoryEntry.ALIAS_NAME};
    }
}


