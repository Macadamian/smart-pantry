package com.macadamian.smartpantry.content.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.macadamian.smartpantry.content.MyContract;
import com.macadamian.smartpantry.database.repositories.InventoryItemsRepository;

public class InventoryItemsController extends AbstractController {

    private final static int MATCH_INVENTORY_ITEMS = 1;
    private final static int MATCH_INVENTORY_ITEM_ACTIVE = 2;
    private final static int MATCH_INVENTORY_ITEM = 3;
    private final static int MATCH_INVENTORY_ITEMS_ACTIVE = 4;
    private final static int MATCH_INVENTORY_ITEM_NOTIFIED = 5;
    private final static int MATCH_INVENTORY_ITEMS_ACTIVE_NOT_NOTIFIED = 6;
    private final static int MATCH_INVENTORY_ITEM_BY_BARCODE = 7;
    private final static int MATCH_INVENTORY_ITEMS_BY_INVENTORY_UUID = 8;
    private final static int MATCH_INVENTORY_ITEM_SEARCH = 9;
    private final static int MATCH_INVENTORY_ITEM_INACTIVE = 10;
    private final static int MATCH_INVENTORY_ITEM_FILTER_INACTIVE = 11;
    private final static int MATCH_INVENTORY_ITEM_FILTER_ACTIVE = 12;
    private final static int MATCH_INVENTORY_ITEM_ACTIVE_COUNT = 13;
    private final static int MATCH_INVENTORY_ITEM_INACTIVE_COUNT = 14;


    private final InventoryItemsRepository mInventoryItemsRepo;

    public InventoryItemsController(Context context) {
        super(context);
        mInventoryItemsRepo = new InventoryItemsRepository(context);
    }

    @Override
    protected void setupMatcher() {
        mMatcher.addURI(MyContract.AUTHORITY, MyContract.InventoryItemEntry.PATH_INVENTORY_ITEMS, MATCH_INVENTORY_ITEMS);
        mMatcher.addURI(MyContract.AUTHORITY, MyContract.InventoryItemEntry.PATH_INVENTORY_ITEM_ACTIVE, MATCH_INVENTORY_ITEM_ACTIVE);
        mMatcher.addURI(MyContract.AUTHORITY, MyContract.InventoryItemEntry.PATH_INVENTORY_ITEM_NOTIFIED, MATCH_INVENTORY_ITEM_NOTIFIED);
        mMatcher.addURI(MyContract.AUTHORITY, MyContract.InventoryItemEntry.PATH_INVENTORY_ITEM_BY_UUID, MATCH_INVENTORY_ITEM);
        mMatcher.addURI(MyContract.AUTHORITY, MyContract.InventoryItemEntry.PATH_INVENTORY_ITEMS_BY_INVENTORY_UUID, MATCH_INVENTORY_ITEMS_BY_INVENTORY_UUID);
        mMatcher.addURI(MyContract.AUTHORITY, MyContract.InventoryItemEntry.PATH_INVENTORY_ITEMS_ACTIVE, MATCH_INVENTORY_ITEMS_ACTIVE);
        mMatcher.addURI(MyContract.AUTHORITY, MyContract.InventoryItemEntry.PATH_INVENTORY_ITEMS_INACTIVE, MATCH_INVENTORY_ITEM_INACTIVE);
        mMatcher.addURI(MyContract.AUTHORITY, MyContract.InventoryItemEntry.PATH_INVENTORY_ITEMS_ACTIVE_NOT_NOTIFIED, MATCH_INVENTORY_ITEMS_ACTIVE_NOT_NOTIFIED);
        mMatcher.addURI(MyContract.AUTHORITY, MyContract.InventoryItemEntry.PATH_INVENTORY_ITEM_BY_BARCODE, MATCH_INVENTORY_ITEM_BY_BARCODE);
        mMatcher.addURI(MyContract.AUTHORITY, MyContract.InventoryItemEntry.PATH_INVENTORY_ITEMS_SEARCH_LIKE, MATCH_INVENTORY_ITEM_SEARCH);
        mMatcher.addURI(MyContract.AUTHORITY, MyContract.InventoryItemEntry.PATH_INVENTORY_ITEMS_FILTER_ACTIVE_BY_INVENTORY_UUID, MATCH_INVENTORY_ITEM_FILTER_ACTIVE);
        mMatcher.addURI(MyContract.AUTHORITY, MyContract.InventoryItemEntry.PATH_INVENTORY_ITEMS_FILTER_INACTIVE_BY_INVENTORY_UUID, MATCH_INVENTORY_ITEM_FILTER_INACTIVE);
        mMatcher.addURI(MyContract.AUTHORITY, MyContract.InventoryItemEntry.PATH_INVENTORY_ITEMS_ACTIVE_COUNT, MATCH_INVENTORY_ITEM_ACTIVE_COUNT);
        mMatcher.addURI(MyContract.AUTHORITY, MyContract.InventoryItemEntry.PATH_INVENTORY_ITEMS_INACTIVE_COUNT, MATCH_INVENTORY_ITEM_INACTIVE_COUNT);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        switch(mMatcher.match(uri)) {
            case MATCH_INVENTORY_ITEMS:
                cursor =  mInventoryItemsRepo.getAllItemsFromAllInventories();
                break;
            case MATCH_INVENTORY_ITEM:
                cursor = mInventoryItemsRepo.getItem(uri.getLastPathSegment());
                break;
            case MATCH_INVENTORY_ITEM_BY_BARCODE:
                cursor = mInventoryItemsRepo.getItemByBarcode(uri.getLastPathSegment());
                break;
            case MATCH_INVENTORY_ITEMS_BY_INVENTORY_UUID:
                cursor = mInventoryItemsRepo.getAllItemsAssociatedWithInventoryByInventoryUUID(uri.getLastPathSegment());
                break;
            case MATCH_INVENTORY_ITEMS_ACTIVE:
                cursor = mInventoryItemsRepo.getAllInventoryItemsByActive(true);
                break;
            case MATCH_INVENTORY_ITEM_INACTIVE:
                cursor = mInventoryItemsRepo.getAllInventoryItemsByActive(false);
                break;
            case MATCH_INVENTORY_ITEM_FILTER_ACTIVE:
                cursor = mInventoryItemsRepo.getAllInventoryItemsByActiveAndLocation(true, uri.getLastPathSegment());
                break;
            case MATCH_INVENTORY_ITEM_FILTER_INACTIVE:
                cursor = mInventoryItemsRepo.getAllInventoryItemsByActiveAndLocation(false, uri.getLastPathSegment());
                break;
            case MATCH_INVENTORY_ITEMS_ACTIVE_NOT_NOTIFIED:
                cursor = mInventoryItemsRepo.getAllActiveNotNotifiedInventoryItems();
                break;
            case MATCH_INVENTORY_ITEM_SEARCH:
                cursor = mInventoryItemsRepo.getSearchResult(uri.getLastPathSegment());
                cursor.setNotificationUri(mContext.getContentResolver(), MyContract.inventoryItemsSearchUri());
                return cursor;
            case MATCH_INVENTORY_ITEM_ACTIVE_COUNT:
                cursor = mInventoryItemsRepo.getAllInventoryItemsCountByActive(true);
                break;
            case MATCH_INVENTORY_ITEM_INACTIVE_COUNT:
                cursor = mInventoryItemsRepo.getAllInventoryItemsCountByActive(false);
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
            case MATCH_INVENTORY_ITEMS:
                return MyContract.inventoryItemUri(String.valueOf(mInventoryItemsRepo.addItem(values)));
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch(mMatcher.match(uri)) {
            case MATCH_INVENTORY_ITEM:
                return mInventoryItemsRepo.removeItem(uri.getLastPathSegment());
        }
        return -1;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (mMatcher.match(uri)) {
            case MATCH_INVENTORY_ITEM_ACTIVE:
                return mInventoryItemsRepo.setItemActive(getPathSegment(uri, 2), values.getAsBoolean(MyContract.InventoryItemEntry.COLUMN_ACTIVE));
            case MATCH_INVENTORY_ITEM_NOTIFIED:
                return mInventoryItemsRepo.setItemNotified(getPathSegment(uri, 2), values.getAsBoolean(MyContract.InventoryItemEntry.COLUMN_NOTIFIED));
            case MATCH_INVENTORY_ITEM:
                return mInventoryItemsRepo.editItem(uri.getLastPathSegment(), values);
        }
        return -1;
    }
}
