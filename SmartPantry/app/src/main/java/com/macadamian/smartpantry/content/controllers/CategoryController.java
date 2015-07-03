package com.macadamian.smartpantry.content.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.macadamian.smartpantry.content.MyContract;
import com.macadamian.smartpantry.database.repositories.CategoryRepository;

public class CategoryController extends AbstractController {

    private final static int MATCH_CATEGORIES = 200;
    private final static int MATCH_CATEGORY = 201;
    private final static int MATCH_CATEGORY_BY_NAME = 202;

    private final CategoryRepository mCategoryRepo;

    public CategoryController(Context context) {
        super(context);
        mCategoryRepo = new CategoryRepository(context);
    }

    @Override
    protected void setupMatcher() {
        mMatcher.addURI(MyContract.AUTHORITY, MyContract.CategoryEntry.PATH_CATEGORIES, MATCH_CATEGORIES);
        mMatcher.addURI(MyContract.AUTHORITY, MyContract.CategoryEntry.PATH_CATEGORY, MATCH_CATEGORY);
        mMatcher.addURI(MyContract.AUTHORITY, MyContract.CategoryEntry.PATH_CATEGORIES_BY_NAME, MATCH_CATEGORY_BY_NAME);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        switch(mMatcher.match(uri)) {
            case MATCH_CATEGORIES:
                cursor = mCategoryRepo.getAllCategoryNames();
                break;
            case MATCH_CATEGORY:
                cursor = mCategoryRepo.getCategory(uri.getLastPathSegment());
                break;
            case MATCH_CATEGORY_BY_NAME:
                cursor = mCategoryRepo.getCategoryByName(uri.getLastPathSegment());
                break;
        }

        if (cursor != null) {
            cursor.setNotificationUri(mContext.getContentResolver(), uri);
        }
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return -1;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return -1;
    }
}
