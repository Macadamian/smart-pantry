package com.macadamian.smartpantry.content.action;


import android.content.Context;
import android.widget.Toast;

import com.macadamian.smartpantry.R;
import com.macadamian.smartpantry.content.MyContract;
import com.macadamian.smartpantry.database.tables.InventoryItemTable;
import com.macadamian.smartpantry.utility.Toaster;

public class InactiveAction implements ActionInterface {

    private final Context mContext;
    private String mItemID;

    public InactiveAction(Context context, String itemID) {
        mContext = context;
        mItemID = itemID;
    }

    @Override
    public void execute() {
        mContext.getContentResolver().update(MyContract.inventoryItemActiveUri(mItemID), InventoryItemTable.makeItemActive(false), null, null);
        Toaster.makeText(mContext, mContext.getResources().getQuantityString(R.plurals.toast_item_moved_to_shopping_list, 1, 1), Toast.LENGTH_SHORT);
    }

    @Override
    public void revert() {
        mContext.getContentResolver().update(MyContract.inventoryItemActiveUri(mItemID), InventoryItemTable.makeItemActive(true), null, null);
    }
}
