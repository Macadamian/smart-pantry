package com.macadamian.smartpantry.content;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import com.google.common.collect.Lists;
import com.macadamian.smartpantry.content.controllers.AbstractController;
import com.macadamian.smartpantry.content.controllers.CategoryController;
import com.macadamian.smartpantry.content.controllers.InventoryController;
import com.macadamian.smartpantry.content.controllers.InventoryItemsController;
import com.macadamian.smartpantry.content.controllers.TemplateController;
import java.util.ArrayList;

public class MyContentProvider extends ContentProvider {

    private ArrayList<AbstractController> mControllers = Lists.newArrayList();
    private MySqlAssetHelper mHelper;

    @Override
    public boolean onCreate() {
        mHelper = new MySqlAssetHelper(getContext());
        mControllers.add(new InventoryController(getContext()));
        mControllers.add(new CategoryController(getContext()));
        mControllers.add(new InventoryItemsController(getContext()));
        mControllers.add(new TemplateController(getContext()));

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor rv = null;
        for (int i=0; i < mControllers.size() && rv == null; i++) {
            rv = mControllers.get(i).query(uri, projection, selection, selectionArgs, sortOrder);
        }
        return rv;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri rv = null;
        for (int i=0; i < mControllers.size() && rv == null; i++) {
            rv = mControllers.get(i).insert(uri, values);
        }
        return rv;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rv = -1;
        for (int i=0; i < mControllers.size() && rv == -1; i++) {
            rv = mControllers.get(i).delete(uri, selection, selectionArgs);
        }
        return rv;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int rv = -1;
        for (int i=0; i < mControllers.size() && rv == -1; i++) {
            rv = mControllers.get(i).update(uri, values, selection, selectionArgs);
        }
        return rv;
    }
}
