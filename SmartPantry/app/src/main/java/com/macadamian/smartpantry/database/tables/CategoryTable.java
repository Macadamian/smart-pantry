package com.macadamian.smartpantry.database.tables;

import android.database.Cursor;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.macadamian.smartpantry.content.MyContract.CategoryEntry;
import com.macadamian.smartpantry.database.readers.CategoryReader;

public class CategoryTable {

    public static final List<String> AllColumns
            = new ArrayList<String>(Arrays.asList(CategoryEntry._ID,
            CategoryEntry.COLUMN_NAME));

    public static String tableQualifiedColumn (String column)
    {
        assert (AllColumns.contains(column));
        return CategoryEntry.TABLE_NAME + "." + column;
    }

    public static ArrayList<String> extractAllCategoryName(Cursor cursor) {
        final ArrayList<String> rv = Lists.newArrayList();

        if (cursor != null) {
            while(cursor.moveToNext()) {
                rv.add(CategoryReader.getInstance(cursor).getName());
            }
        }
        return rv;
    }
}
