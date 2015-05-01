package com.macadamian.smartpantry.content;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;


public class MySqlAssetHelper extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = "smartpantry.db";
    private static final int DATABASE_VERSION = 1;

    public MySqlAssetHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
}
