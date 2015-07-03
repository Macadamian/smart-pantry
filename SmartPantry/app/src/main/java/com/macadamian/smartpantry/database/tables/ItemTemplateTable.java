package com.macadamian.smartpantry.database.tables;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.common.collect.Lists;
import com.macadamian.smartpantry.content.MyContract;
import com.macadamian.smartpantry.database.readers.ItemTemplateReader;
import com.macadamian.smartpantry.utility.comparators.AlphabeticalComparator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ItemTemplateTable {

    public static final List<String> AllColumns
            = new ArrayList<>(Arrays.asList(MyContract.TemplateEntry._ID,
            MyContract.CategoryEntry.COLUMN_NAME));

    public static String tableQualifiedColumn (String column)
    {
        assert (AllColumns.contains(column));
        return MyContract.TemplateEntry.TABLE_NAME + "." + column;
    }

    public static ContentValues makeTemplate(String barcode, String name,long category, String inventoryUUID) {
        ContentValues values = new ContentValues();
        values.put(MyContract.TemplateEntry.COLUMN_BARCODE,           barcode);
        values.put(MyContract.TemplateEntry.COLUMN_NAME,              name);
        values.put(MyContract.TemplateEntry.COLUMN_CATEGORY,          category);
        values.put(MyContract.TemplateEntry.COLUMN_INVENTORY_UUID,    inventoryUUID);

        return values;
    }

    public static ArrayList<String> extractAllNames(Cursor cursor) {
        final ArrayList<String> rv = Lists.newArrayList();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                rv.add(ItemTemplateReader.getInstance(cursor).getName());
            }
        }
        Collections.sort(rv, new AlphabeticalComparator());
        return rv;
    }
}
