package com.macadamian.smartpantry.content.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public abstract class AbstractController {

    protected final UriMatcher mMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    protected final Context mContext;

    public AbstractController(final Context context) {
        mContext = context;
        setupMatcher();
    }

    protected abstract void setupMatcher();
    public abstract Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder);
    public abstract Uri insert(Uri uri, ContentValues values);
    public abstract int delete(Uri uri, String selection, String[] selectionArgs);
    public abstract int update(Uri uri, ContentValues values, String selection, String[] selectionArgs);

    protected String getPathSegment(Uri uri, int positionFromEnd) {
        String path = "";
        for (int i=uri.getPathSegments().size(); i >= 0; i--) {
            if (i + positionFromEnd == uri.getPathSegments().size()) {
                path = uri.getPathSegments().get(i);
            }
        }
        return path;
    }
}
