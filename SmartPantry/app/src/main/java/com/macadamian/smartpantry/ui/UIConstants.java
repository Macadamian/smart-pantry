package com.macadamian.smartpantry.ui;

public class UIConstants {

    // Loaders
    public static final int LOADER_INVENTORY_ITEMS    = 1000;
    public static final int LOADER_EDIT_INVENTORY_ITEM = 2000;
    public static final int LOADER_LOCATION_LIST = 3000;
    public static final int LOADER_SEARCH = 4000;
    public static final int LOADER_ACTIVE_INVENTORY_ITEMS = 5000;
    public static final int LOADER_INACTIVE_INVENTORY_ITEMS = 6000;
    public static final int LOADER_ACTIVE_FILTER_INVENTORY_ITEMS = 7000;
    public static final int LOADER_INACTIVE_FILTER_INVENTORY_ITEMS = 8000;
    public static final int LOADER_TEMPLATE_NAME_ITEM = 9000;
    public static final int LOADER_INACTIVE_COUNT_INVENTORY_ITEMS = 10000;

    public static final int QUANTITY_NONE = 0;
    public static final int QUANTITY_SOME = 25;
    public static final int QUANTITY_ENOUGH = 50;
    public static final int QUANTITY_LOTS = 75;

    // Preferences
    public final static String PREF_APP_FIRST_LAUNCH = "PREF_APP_FIRST_LAUNCH";
    public final static String PREF_MAIN_INITIAL_SHOWCASE = "PREF_MAIN_INITIAL_SHOWCASE";
    public final static String PREF_MAIN_SWIPE_SHOWCASE = "PREF_MAIN_SWIPE_SHOWCASE";
    public final static String PREF_EDIT_ADD_SHOWCASE = "PREF_EDIT_ADD_SHOWCASE";
    public final static String PREF_DEFAULT_LOCATION = "PREF_DEFAULT_LOCATION";

    // Intent actions
    public static final String ACTION_LOCATION_FILTER = "ACTION_LOCATION_FILTER";
    public static final String ACTION_VIEW_HOLDER_COLLAPSE = "ACTION_VIEW_HOLDER_COLLAPSE";
    public static final String ACTION_VIEW_HOLDER_EXPAND = "ACTION_VIEW_HOLDER_EXPAND";
    public static final String ACTION_VIEW_HOLDER_COLLAPSE_ALL = "ACTION_VIEW_HOLDER_COLLAPSE_ALL";
    public static final String ACTION_ANIMATE_SPLAT = "ACTION_ANIMATE_SPLAT" ;

    // Extras
    public static final String EXTRA_LOCATION_FILTER = "EXTRA_LOCATION_FILTER";
    public static final String EXTRA_VIEW_HOLDER_COLLAPSE = "EXTRA_VIEW_HOLDER_COLLAPSE";
    public static final String EXTRA_VIEW_HOLDER_EXPAND = "EXTRA_VIEW_HOLDER_EXPAND";
    public static final String ACTION_RESET_SUBAPPBARR = "ACTION_RESET_SUBAPPBARR";
}
