package com.macadamian.smartpantry.database.readers;

import android.database.Cursor;

import com.macadamian.smartpantry.content.MyContract;


public class ItemTemplateReader extends AbstractReader {

    protected ItemTemplateReader(Cursor cursor) {
        super(cursor);
    }

    public static ItemTemplateReader getInstance(Cursor cursor) {
        return new ItemTemplateReader(cursor);
    }

    public String getName() {
        return getString(MyContract.TemplateEntry.COLUMN_NAME);
    }

    public Integer getCategory() {
        return getInt(MyContract.TemplateEntry.COLUMN_CATEGORY);
    }

    public String getAliasedInventoryName() { return getString(MyContract.InventoryEntry.ALIAS_NAME); }
}
