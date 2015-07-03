package com.macadamian.smartpantry.database.readers;

import android.database.Cursor;

import com.macadamian.smartpantry.content.MyContract;

public class CategoryReader extends AbstractReader {


    private CategoryReader(Cursor cursor) {
        super(cursor);
    }

    public static CategoryReader getInstance(Cursor cursor) {
        return new CategoryReader(cursor);
    }

    public String getName() {
        return getString(MyContract.CategoryEntry.COLUMN_NAME);
    }
}
