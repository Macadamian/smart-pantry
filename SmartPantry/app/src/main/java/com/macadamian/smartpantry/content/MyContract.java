package com.macadamian.smartpantry.content;

import android.net.Uri;
import android.provider.BaseColumns;

public final class MyContract {

    public final static String AUTHORITY = "com.macadamian.smartpantry.content.MyContentProvider";

    public MyContract() {}

    public static Uri inventoryItemsUri() {
        return Uri.withAppendedPath(authorityUri(), InventoryItemEntry.PATH_INVENTORY_ITEMS);
    }

    public static Uri inventoryItemsUuidUri() {
        return Uri.withAppendedPath(authorityUri(), InventoryItemEntry.PATH_INVENTORY_ITEM_UUID);
    }

    public static Uri inventoryItemUri(String itemId) {
        return Uri.withAppendedPath(inventoryItemsUuidUri(),itemId);
    }

    public static Uri inventoryItemsBarcodeUri() {
        return Uri.withAppendedPath(authorityUri(), InventoryItemEntry.PATH_INVENTORY_ITEMS_BARCODE);
    }

    public static Uri inventoryItemByBarcodeUri(String barcode) {
        return Uri.withAppendedPath(inventoryItemsBarcodeUri(),barcode);
    }

    public static Uri inventoryItemsInventoryUri() {
        return Uri.withAppendedPath(authorityUri(), InventoryItemEntry.PATH_INVENTORY_ITEMS_INVENTORY);
    }


    public static Uri inventoryItemsByInventoryUuidUri(String inventoryUUID) {
        return Uri.withAppendedPath(inventoryItemsInventoryUri(),inventoryUUID);
    }

    public static Uri inventoryItemActiveUri(String itemId) {
        return Uri.withAppendedPath(inventoryItemUri(itemId), InventoryItemEntry.PATH_ACTIVE);
    }

    public static Uri inventoryItemNotifiedUri(String itemId) {
        return Uri.withAppendedPath(inventoryItemUri(itemId), InventoryItemEntry.PATH_NOTIFIED);
    }

    public static Uri inventoryItemsActiveUri() {
        return Uri.withAppendedPath(authorityUri(), InventoryItemEntry.PATH_INVENTORY_ITEMS_ACTIVE);
    }

    public static Uri inventoryItemsInactiveUri() {
        return Uri.withAppendedPath(authorityUri(), InventoryItemEntry.PATH_INVENTORY_ITEMS_INACTIVE);
    }

    public static Uri inventoryItemsActiveCountUri() {
        return Uri.withAppendedPath(authorityUri(), InventoryItemEntry.PATH_INVENTORY_ITEMS_ACTIVE_COUNT);
    }

    public static Uri inventoryItemsInactiveCountUri() {
        return Uri.withAppendedPath(authorityUri(), InventoryItemEntry.PATH_INVENTORY_ITEMS_INACTIVE_COUNT);
    }

    public static Uri inventoryItemsFilterActiveUri(){
        return Uri.withAppendedPath(authorityUri(), InventoryItemEntry.PATH_INVENTORY_ITEMS_FILTER_ACTIVE);
    }

    public static Uri inventoryItemsFilterInactiveUri(){
        return Uri.withAppendedPath(authorityUri(), InventoryItemEntry.PATH_INVENTORY_ITEMS_FILTER_INACTIVE);
    }

    public static Uri inventoryItemsFilterActiveByInventoryUuidUri(String inventoryUUID) {
        return Uri.withAppendedPath(inventoryItemsFilterActiveUri(), inventoryUUID);
    }

    public static Uri inventoryItemsFilterInactiveByInventoryUuidUri(String inventoryUUID) {
        return Uri.withAppendedPath(inventoryItemsFilterInactiveUri(), inventoryUUID);
    }

    public static Uri inventoriesUri() {
        return Uri.withAppendedPath(authorityUri(), InventoryEntry.PATH_INVENTORIES);
    }

    public static Uri inventoryUri(String itemId) {
        return Uri.withAppendedPath(inventoriesUri(), itemId);
    }

    public static Uri inventoryItemsActiveNotNotified() {
        return Uri.withAppendedPath(authorityUri(), InventoryItemEntry.PATH_INVENTORY_ITEMS_ACTIVE_NOT_NOTIFIED);
    }

    public static Uri inventoryItemsSearchUri() {
        return Uri.withAppendedPath(authorityUri(), InventoryItemEntry.PATH_INVENTORY_ITEMS_SEARCH);
    }

    public static Uri inventoryItemsSearchUri(String query) {
        return Uri.withAppendedPath(inventoryItemsSearchUri(), query);
    }

    public static Uri inventoriesNameUri() {
        return Uri.withAppendedPath(authorityUri(), InventoryEntry.PATH_INVENTORIES_NAME);
    }

    public static Uri inventoriesByNameUri(String inventoryName) {
        return Uri.withAppendedPath(inventoriesNameUri(), inventoryName);
    }

    public static Uri inventoriesUuidUri() {
        return Uri.withAppendedPath(authorityUri(), InventoryEntry.PATH_INVENTORIES_UUID);
    }

    public static Uri inventoriesByUuidUri(String inventoryUUID) {
        return Uri.withAppendedPath(inventoriesUuidUri(), inventoryUUID);
    }

    public static Uri categoriesUri() {
        return Uri.withAppendedPath(authorityUri(), CategoryEntry.PATH_CATEGORIES);
    }

    public static Uri categoriesNameUri() {
        return Uri.withAppendedPath(authorityUri(), CategoryEntry.PATH_CATEGORIES_NAME);
    }

    public static Uri categoriesByNameUri(String categoryName) {
        return Uri.withAppendedPath(categoriesNameUri(), categoryName);
    }

    public static Uri categoryUri(String id) {
        return Uri.withAppendedPath(categoriesUri(), id);
    }

    public static Uri templatesUri() {
        return Uri.withAppendedPath(authorityUri(), TemplateEntry.PATH_TEMPLATES);
    }

    public static Uri templateBarcodeUri() {
        return Uri.withAppendedPath(authorityUri(), TemplateEntry.PATH_TEMPLATE_BARCODE);
    }

    public static Uri templateNameUri() {
        return Uri.withAppendedPath(authorityUri(), TemplateEntry.PATH_TEMPLATE_NAME);
    }

    public static Uri templateByBarcodeUri(String barcode) {
        return Uri.withAppendedPath(templateBarcodeUri(), barcode);
    }

    public static Uri templateByNameUri(String name) {
        return Uri.withAppendedPath(templateNameUri(), name);
    }

    public static Uri templateUri(String id) {
        return Uri.withAppendedPath(templatesUri(), id);
    }

    public static Uri authorityUri() {
        return Uri.parse("content://" + AUTHORITY);
    }

    public static abstract class CategoryEntry implements BaseColumns {
        public static final String PATH_CATEGORIES = "categories";
        public static final String PATH_CATEGORY = "categories/#";
        public static final String PATH_NAME = "name";
        public static final String PATH_CATEGORIES_NAME = PATH_CATEGORIES + "/" + PATH_NAME;
        public static final String PATH_CATEGORIES_BY_NAME = PATH_CATEGORIES_NAME + "/*";

        public static final String TABLE_NAME = "category";
        public static final String COLUMN_NAME = "name";
        public static final String ALIAS_NAME = "categoryName";
    }

    public static abstract class InventoryItemEntry implements BaseColumns {

        public static final String PATH_INVENTORY_ITEMS = "inventoryItems";
        public static final String PATH_UUID = "by_uuid";
        public static final String PATH_INVENTORY_UUID = "by_inventory_uuid";
        public static final String PATH_INVENTORY_ITEM_UUID = PATH_INVENTORY_ITEMS + "/" + PATH_UUID;
        public static final String PATH_INVENTORY_ITEMS_INVENTORY = PATH_INVENTORY_ITEMS + "/" + PATH_INVENTORY_UUID;
        public static final String PATH_INVENTORY_ITEM_BY_UUID =PATH_INVENTORY_ITEM_UUID + "/*";
        public static final String PATH_INVENTORY_ITEMS_BY_INVENTORY_UUID =PATH_INVENTORY_ITEMS_INVENTORY + "/*";
        public static final String PATH_BARCODE = "by_barcode";
        public static final String PATH_INVENTORY_ITEMS_BARCODE = PATH_INVENTORY_ITEMS + "/" + PATH_BARCODE;
        public static final String PATH_INVENTORY_ITEM_BY_BARCODE = PATH_INVENTORY_ITEMS_BARCODE+"/*";
        public static final String PATH_ACTIVE = "active";
        public static final String PATH_INACTIVE = "inactive";
        public static final String PATH_NOTIFIED = "notified";
        public static final String PATH_NOT_NOTIFIED = "notNotified";
        public static final String PATH_SEARCH = "search";
        public static final String PATH_FILTER = "filter";
        public static final String PATH_COUNT = "count";
        public static final String PATH_INVENTORY_ITEMS_SEARCH = PATH_INVENTORY_ITEMS + "/" + PATH_SEARCH;
        public static final String PATH_INVENTORY_ITEMS_SEARCH_LIKE = PATH_INVENTORY_ITEMS_SEARCH + "/*";
        public static final String PATH_INVENTORY_ITEM_NOTIFIED = PATH_INVENTORY_ITEM_UUID + "/" + PATH_NOTIFIED;
        public static final String PATH_INVENTORY_ITEMS_ACTIVE = PATH_INVENTORY_ITEMS + "/" + PATH_ACTIVE;
        public static final String PATH_INVENTORY_ITEMS_INACTIVE = PATH_INVENTORY_ITEMS + "/" + PATH_INACTIVE;
        public static final String PATH_INVENTORY_ITEMS_ACTIVE_COUNT = PATH_INVENTORY_ITEMS_ACTIVE + "/" + PATH_COUNT;
        public static final String PATH_INVENTORY_ITEMS_INACTIVE_COUNT = PATH_INVENTORY_ITEMS_INACTIVE + "/" + PATH_COUNT;
        public static final String PATH_INVENTORY_ITEM_ACTIVE = PATH_INVENTORY_ITEM_BY_UUID + "/" + PATH_ACTIVE;
        public static final String PATH_INVENTORY_ITEMS_ACTIVE_NOT_NOTIFIED = PATH_INVENTORY_ITEMS_ACTIVE + "/" + PATH_NOT_NOTIFIED;
        public static final String PATH_INVENTORY_ITEMS_FILTER = PATH_INVENTORY_ITEMS + "/" + PATH_FILTER;
        public static final String PATH_INVENTORY_ITEMS_FILTER_ACTIVE = PATH_INVENTORY_ITEMS_FILTER + "/" +PATH_ACTIVE ;
        public static final String PATH_INVENTORY_ITEMS_FILTER_INACTIVE = PATH_INVENTORY_ITEMS_FILTER + "/" +PATH_INACTIVE ;
        public static final String PATH_INVENTORY_ITEMS_FILTER_ACTIVE_BY_INVENTORY_UUID = PATH_INVENTORY_ITEMS_FILTER_ACTIVE + "/*";
        public static final String PATH_INVENTORY_ITEMS_FILTER_INACTIVE_BY_INVENTORY_UUID = PATH_INVENTORY_ITEMS_FILTER_INACTIVE + "/*";


        public static final String TABLE_NAME = "inventoryItem";
        public static final String COLUMN_ITEM_UUID         = "itemID";
        public static final String COLUMN_INVENTORY_UUID    = "inventoryID";
        public static final String COLUMN_BARCODE           = "barcode";
        public static final String COLUMN_NAME              = "name";
        public static final String COLUMN_RELATIVE_QUANTITY = "relQuantity";
        public static final String COLUMN_CATEGORY          = "category";
        public static final String COLUMN_EXPIRY            = "expiry";
        public static final String COLUMN_LAST_SYNCHED      = "lastSynched";
        public static final String COLUMN_UPDATED_AT        = "updatedAt";
        public static final String COLUMN_ACTIVE            = "active";
        public static final String COLUMN_NOTIFIED          = "notified";
        public static final String ALIAS_NAME               = "itemName";
    }

    public static abstract class InventoryEntry implements BaseColumns {
        public static final String PATH_INVENTORIES = "inventories";
        public static final String PATH_INVENTORY = "inventories/#";
        public static final String PATH_NAME = "by_name";
        public static final String PATH_UUID = "by_uuid";
        public static final String PATH_INVENTORIES_NAME = PATH_INVENTORIES + "/" + PATH_NAME;
        public static final String PATH_INVENTORIES_UUID = PATH_INVENTORIES + "/" + PATH_UUID;
        public static final String PATH_INVENTORIES_BY_NAME = PATH_INVENTORIES_NAME + "/*";
        public static final String PATH_INVENTORIES_BY_UUID = PATH_INVENTORIES_UUID + "/*";

        public static final String TABLE_NAME               = "inventory";
        public static final String COLUMN_INVENTORY_UUID    = "inventoryID";
        public static final String COLUMN_NAME              = "name";
        public static final String COLUMN_LAST_SYNCHED      = "lastSynched";

        public static final String ALIAS_NAME               = "inventoryName";

    }

    public static abstract class TemplateEntry implements BaseColumns {
        public static final String PATH_TEMPLATES = "templates";
        public static final String PATH_TEMPLATE = "templates/#";
        public static final String PATH_BARCODE = "by_barcode";
        public static final String PATH_NAME = "by_name";
        public static final String PATH_TEMPLATE_BARCODE = PATH_TEMPLATES + "/"+ PATH_BARCODE;
        public static final String PATH_TEMPLATE_NAME = PATH_TEMPLATES + "/"+ PATH_NAME;
        public static final String PATH_TEMPLATE_BY_BARCODE =  PATH_TEMPLATE_BARCODE+"/*";
        public static final String PATH_TEMPLATE_BY_NAME =  PATH_TEMPLATE_NAME+"/*";

        public static final String TABLE_NAME               = "inventoryItemTemplate";
        public static final String COLUMN_NAME              = "name";
        public static final String COLUMN_BARCODE           = "barcode";
        public static final String COLUMN_CATEGORY          = "category";
        public static final String COLUMN_INVENTORY_UUID    = "inventoryID";

    }
}
