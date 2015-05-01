package com.macadamian.smartpantry.database.repositories;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.macadamian.smartpantry.content.MySqlAssetHelper;

public class AbstractRepository {

    private final MySqlAssetHelper mHelper;
    private final Context mContext;

    public AbstractRepository(Context context) {
        mContext = context;
        mHelper = new MySqlAssetHelper(context);
    }

    protected SQLiteDatabase getWritableDatabase() {
        return mHelper.getWritableDatabase();
    }

    protected SQLiteDatabase getReadableDatabase() {
        return mHelper.getReadableDatabase();
    }

    protected void notify(final Uri uri) {
        mContext.getContentResolver().notifyChange(uri, null);
    }
}
