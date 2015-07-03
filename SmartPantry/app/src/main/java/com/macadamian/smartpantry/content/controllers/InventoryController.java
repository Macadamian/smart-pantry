package com.macadamian.smartpantry.content.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.macadamian.smartpantry.content.MyContract;
import com.macadamian.smartpantry.database.repositories.InventoryRepository;

public class InventoryController extends AbstractController {

    private final static int MATCH_INVENTORIES = 100;
    private final static int MATCH_INVENTORY = 101;
    private final static int MATCH_INVENTORY_BY_NAME = 102;
    private final static int MATCH_INVENTORY_BY_UUID = 103;

    private InventoryRepository mInventoryRepo;

    public InventoryController(Context context) {
        super(context);
        mInventoryRepo = new InventoryRepository(context);
    }

    @Override
    protected void setupMatcher() {
        mMatcher.addURI(MyContract.AUTHORITY, MyContract.InventoryEntry.PATH_INVENTORIES, MATCH_INVENTORIES);
        mMatcher.addURI(MyContract.AUTHORITY, MyContract.InventoryEntry.PATH_INVENTORY, MATCH_INVENTORY);
        mMatcher.addURI(MyContract.AUTHORITY, MyContract.InventoryEntry.PATH_INVENTORIES_BY_NAME, MATCH_INVENTORY_BY_NAME);
        mMatcher.addURI(MyContract.AUTHORITY, MyContract.InventoryEntry.PATH_INVENTORIES_BY_UUID, MATCH_INVENTORY_BY_UUID);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        switch(mMatcher.match(uri)) {
            case MATCH_INVENTORIES:
                cursor = mInventoryRepo.getAllInventories();
                break;
            case MATCH_INVENTORY_BY_NAME:
                cursor = mInventoryRepo.getInventoryUUIDByName(uri.getLastPathSegment());
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
            case MATCH_INVENTORIES:
                return MyContract.inventoryUri(String.valueOf(mInventoryRepo.addInventory(values)));
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch(mMatcher.match(uri)) {
            case MATCH_INVENTORY_BY_UUID:
                return mInventoryRepo.removeItem(uri.getLastPathSegment());
        }
        return -1;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (mMatcher.match(uri)) {
            case MATCH_INVENTORY_BY_UUID:
                return mInventoryRepo.editInventory(uri.getLastPathSegment(), values);
        }
        return -1;
    }
}
