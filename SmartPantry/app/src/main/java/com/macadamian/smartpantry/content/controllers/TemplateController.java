package com.macadamian.smartpantry.content.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.macadamian.smartpantry.content.MyContract;
import com.macadamian.smartpantry.database.repositories.ItemTemplateRepository;

public class TemplateController extends AbstractController {

    private final static int MATCH_TEMPLATES = 300;
    private final static int MATCH_TEMPLATE_BY_BARCODE = 301;
    private final static int MATCH_TEMPLATE = 302;
    private final static int MATCH_TEMPLATE_BY_NAME = 303;

    private final ItemTemplateRepository mTemplateRepo;

    public TemplateController(Context context) {
        super(context);
        mTemplateRepo = new ItemTemplateRepository(context);
    }

    @Override
    protected void setupMatcher() {
        mMatcher.addURI(MyContract.AUTHORITY, MyContract.TemplateEntry.PATH_TEMPLATES, MATCH_TEMPLATES);
        mMatcher.addURI(MyContract.AUTHORITY, MyContract.TemplateEntry.PATH_TEMPLATE_BY_BARCODE, MATCH_TEMPLATE_BY_BARCODE);
        mMatcher.addURI(MyContract.AUTHORITY, MyContract.TemplateEntry.PATH_TEMPLATE_BY_NAME, MATCH_TEMPLATE_BY_NAME);
        mMatcher.addURI(MyContract.AUTHORITY, MyContract.TemplateEntry.PATH_TEMPLATE, MATCH_TEMPLATE);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        switch(mMatcher.match(uri)) {
            case MATCH_TEMPLATES:
                cursor = mTemplateRepo.getTemplateItems();
                break;
            case MATCH_TEMPLATE_BY_BARCODE:
                cursor = mTemplateRepo.getTemplateItemByBarcode(uri.getLastPathSegment());
                break;
            case MATCH_TEMPLATE_BY_NAME:
                cursor = mTemplateRepo.getTemplateItemByName(uri.getLastPathSegment());
                break;
        }

        if (cursor != null) {
            cursor.setNotificationUri(mContext.getContentResolver(), uri);
        }
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch(mMatcher.match(uri)) {
            case MATCH_TEMPLATES:
                return MyContract.templateUri(String.valueOf(mTemplateRepo.addTemplateItem(values)));
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return -1;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch(mMatcher.match(uri)) {
            case MATCH_TEMPLATE_BY_BARCODE:
                return mTemplateRepo.editTemplateItemByBarcode(uri.getLastPathSegment(), values);
        }
        return -1;
    }
}
