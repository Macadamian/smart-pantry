package com.macadamian.smartpantry.database.repositories;

        import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteQueryBuilder;
        import android.util.Log;

        import com.macadamian.smartpantry.content.MyContract;
        import com.macadamian.smartpantry.database.tables.InventoryTable;
        import com.macadamian.smartpantry.database.tables.ItemTemplateTable;

public class ItemTemplateRepository extends AbstractRepository {

    private static final String TAG = "inventoryItemTemplateRepository";

    public ItemTemplateRepository(Context context) {
        super(context);
    }

    public Cursor getTemplateItems() {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(MyContract.TemplateEntry.TABLE_NAME);
        return queryBuilder.query(getReadableDatabase(), null, null, null, null, null, null, null);
    }

    public Cursor getTemplateItemByBarcode(String barcode) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String templateTableName = MyContract.TemplateEntry.TABLE_NAME;
        String inventoryTableName = MyContract.InventoryEntry.TABLE_NAME;

        queryBuilder.setTables(String.format("%s LEFT OUTER JOIN %s ON (%s = %s)",
                templateTableName, inventoryTableName,
                ItemTemplateTable.tableQualifiedColumn(MyContract.InventoryItemEntry.COLUMN_INVENTORY_UUID),
                InventoryTable.tableQualifiedColumn(MyContract.InventoryEntry.COLUMN_INVENTORY_UUID)));

//        queryBuilder.setTables(MyContract.TemplateEntry.TABLE_NAME);
        String[] columns = getDisplayColumns();
        String selection = ItemTemplateTable.tableQualifiedColumn(MyContract.TemplateEntry.COLUMN_BARCODE) + "= ?";
        String[] selectionArgs = {barcode};
        return queryBuilder.query(getReadableDatabase(), columns, selection, selectionArgs, null, null, null, null);
    }

    public Cursor getTemplateItemByName(String name) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(MyContract.TemplateEntry.TABLE_NAME);
        String[] columns = getDisplayColumns();
        String selection = ItemTemplateTable.tableQualifiedColumn(MyContract.TemplateEntry.COLUMN_NAME) + "= ?";
        String[] selectionArgs = {name};
        String limit = "1";
        return queryBuilder.query(getReadableDatabase(), columns, selection, selectionArgs, null, null, null, limit);
    }

    public long addTemplateItem(final ContentValues values){
        long id = -1;
        getWritableDatabase().beginTransaction();
        try {
            id = getWritableDatabase().insert(MyContract.TemplateEntry.TABLE_NAME, null, values);
            getWritableDatabase().setTransactionSuccessful();

        } catch (Exception e) {
            Log.e(TAG, "Error during transaction - " + e);
        }
        getWritableDatabase().endTransaction();
        return id;
    }

    public int editTemplateItemByBarcode(String barcode, ContentValues values) {
        int rv = -1;
        String filter = MyContract.TemplateEntry.COLUMN_BARCODE + "= ?";
        String[] filterArgs = {barcode};

        getWritableDatabase().beginTransaction();
        try {
            rv = getWritableDatabase().update(MyContract.TemplateEntry.TABLE_NAME, values, filter, filterArgs);
            getWritableDatabase().setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error during transaction - " + e);
        }
        getWritableDatabase().endTransaction();
        return rv;
    }

    private static String[] getDisplayColumns(){
        return new String []{
                ItemTemplateTable.tableQualifiedColumn(MyContract.TemplateEntry.COLUMN_CATEGORY),
                ItemTemplateTable.tableQualifiedColumn(MyContract.TemplateEntry.COLUMN_NAME),
                ItemTemplateTable.tableQualifiedColumn(MyContract.TemplateEntry.COLUMN_INVENTORY_UUID),
                InventoryTable.tableQualifiedColumn(MyContract.InventoryEntry.COLUMN_NAME) + " AS " + MyContract.InventoryEntry.ALIAS_NAME,
                ItemTemplateTable.tableQualifiedColumn(MyContract.TemplateEntry.COLUMN_BARCODE)};
    }
}
