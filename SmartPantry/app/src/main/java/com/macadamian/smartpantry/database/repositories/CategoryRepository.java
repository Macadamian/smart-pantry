package com.macadamian.smartpantry.database.repositories;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteQueryBuilder;

import com.macadamian.smartpantry.content.MyContract.CategoryEntry;
import com.macadamian.smartpantry.database.tables.CategoryTable;

public class CategoryRepository extends AbstractRepository {
    private static final String TAG = "/repositories/CategoryRepository";

    public CategoryRepository(final Context context) {
        super(context);
    }

    public Cursor getAllCategoryNames() {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(CategoryEntry.TABLE_NAME);
        return queryBuilder.query(getReadableDatabase(), null, null, null, null, null, null, null);
    }

    public Cursor getCategory(String id) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        queryBuilder.setTables(CategoryEntry.TABLE_NAME);
        String[] columns = { CategoryTable.tableQualifiedColumn(CategoryEntry._ID), CategoryTable.tableQualifiedColumn(CategoryEntry.COLUMN_NAME) };
        String selection = CategoryTable.tableQualifiedColumn(CategoryEntry._ID) + "= ?";
        String[] selectionArgs = {id};
        return queryBuilder.query(getReadableDatabase(), columns, selection, selectionArgs, null, null, null);
    }

    public Cursor getCategoryByName(String categoryName){
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(CategoryEntry.TABLE_NAME);
        String[] columns = { CategoryTable.tableQualifiedColumn(CategoryEntry._ID)};
        String selection = CategoryTable.tableQualifiedColumn(CategoryEntry.COLUMN_NAME) + " = ?";
        DatabaseUtils.appendEscapedSQLString(new StringBuilder(), categoryName);
        String[] selectionArgs = {categoryName};
        String limit = "1";

        return queryBuilder.query(getReadableDatabase(), columns, selection, selectionArgs, null, null, null, limit);
    }
}