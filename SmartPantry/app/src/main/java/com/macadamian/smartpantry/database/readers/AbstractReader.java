package com.macadamian.smartpantry.database.readers;

import android.database.Cursor;
import android.provider.BaseColumns;

public class AbstractReader {

    protected final Cursor mCursor;

    protected AbstractReader(Cursor cursor) {
        mCursor = cursor;
    }

    public String getString(String columnName) {
        if(mCursor.getCount() >0){
        return mCursor.getString(mCursor.getColumnIndex(columnName));}
        else{
            return null;
        }
    }

    public long getLong(String columnName) {
        return mCursor.getLong(mCursor.getColumnIndex(columnName));
    }

    public int getInt(String columnName) {
        return mCursor.getInt(mCursor.getColumnIndex(columnName));
    }

    public double getDouble(String columnName) {
        return mCursor.getDouble(mCursor.getColumnIndex(columnName));
    }

    public long getId() {
        return getLong(BaseColumns._ID);
    }

    public Integer getCount() { return getInt(BaseColumns._COUNT); }
}
