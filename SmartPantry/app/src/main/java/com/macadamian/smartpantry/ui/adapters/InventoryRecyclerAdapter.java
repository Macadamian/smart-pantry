package com.macadamian.smartpantry.ui.adapters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonFloat;
import com.google.common.collect.Lists;
import com.macadamian.smartpantry.R;
import com.macadamian.smartpantry.content.MyContract;
import com.macadamian.smartpantry.content.action.ActionExecuter;
import com.macadamian.smartpantry.content.action.DeleteAction;
import com.macadamian.smartpantry.content.action.InactiveAction;
import com.macadamian.smartpantry.database.readers.InventoryItemReader;
import com.macadamian.smartpantry.database.tables.InventoryItemTable;
import com.macadamian.smartpantry.ui.SwipeToDismissTouchListener;
import com.macadamian.smartpantry.ui.UIConstants;
import com.macadamian.smartpantry.ui.activities.EditItemActivity;
import com.macadamian.smartpantry.ui.adapters.recycler.RecyclerCursorAdapter;
import com.macadamian.smartpantry.utility.AnimationUtility;
import com.macadamian.smartpantry.widgets.ExpirationWidget;
import com.macadamian.smartpantry.widgets.QuantityWidget;
import com.macadamian.smartpantry.widgets.UndoActionWidget;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class InventoryRecyclerAdapter extends RecyclerCursorAdapter<InventoryRecyclerAdapter.ViewHolder>{

    private final static int VIEW_TYPE_ACTIVE = 1;
    private final static int VIEW_TYPE_INACTIVE = 2;

    private int mCurrentSwipePose = -1;
    private final Context mContext;
    private MultiSelectionInterface mMultiSelection;
    private final SparseBooleanArray mSelectedPositions = new SparseBooleanArray();

    public InventoryRecyclerAdapter(final Context context, final Cursor cursor) {
        super(cursor);
        mContext = context;
    }

    public void setMultiSelection(final MultiSelectionInterface multiSelection) {
        mMultiSelection = multiSelection;
    }

    @Override
    public void onBindViewHolderCursor(final InventoryRecyclerAdapter.ViewHolder holder, Cursor cursor) {
        InventoryItemReader reader = InventoryItemReader.getInstance(cursor);

        String name = reader.getAliasedName();
        Integer quantity = reader.getQuantity();
        String exp = reader.getExpiry();
        String loc = reader.getString(MyContract.InventoryEntry.ALIAS_NAME);

        if (!mMultiSelection.isMultiSelectionModeEnabled())
            holder.itemView.setSelected(false);
        if (getItemViewType(holder.getPosition()) == VIEW_TYPE_ACTIVE) {
            RelativeLayout card = (RelativeLayout) holder.itemView.findViewById(R.id.card_root_cont);
            card.setBackgroundResource(getItemBackgroundDrawableResId(cursor.getPosition()));
            holder.mQuantityWidget.setSelectedQuantity(quantity);
            switch(quantity) {
                case 0:
                    holder.quantity.setDrawableIcon(holder.itemView.getResources().getDrawable(R.drawable.q_none));
                    break;
                case 25:
                    holder.quantity.setDrawableIcon(holder.itemView.getResources().getDrawable(R.drawable.q_some));
                    break;
                case 50:
                    holder.quantity.setDrawableIcon(holder.itemView.getResources().getDrawable(R.drawable.q_enough));
                    break;
                case 75:
                    holder.quantity.setDrawableIcon(holder.itemView.getResources().getDrawable(R.drawable.q_lots));
                    break;
            }
            holder.expiration.setDate(exp);

            setQuickQuantityClick(holder, reader.getItemUUID());
        }
        holder.itemName.setText(name);
        holder.location.setText(loc);
        if(holder.getPosition() != mCurrentSwipePose) {
            holder.cardView.setTranslationX(0);
            holder.cardView.setAlpha(1.0f);
        }
        else {
            holder.cardView.setTranslationX(holder.cardView.getWidth());
            holder.cardView.setAlpha(1.0f);
        }
    }

    private void setQuickQuantityClick(final ViewHolder holder, final String uuid) {

        holder.mQuantityWidget.setButtonClickListener(new QuantityWidget.ClickCallback() {
            @Override
            public void onClick() {
                holder.collapse(false);
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(UIConstants.ACTION_RESET_SUBAPPBARR));
                if (holder.mQuantityWidget.getSelectedQuantity() == UIConstants.QUANTITY_NONE) {
                    ActionExecuter.getInstance().executeImmediately(new InactiveAction(mContext, uuid));
                } else {
                    mContext.getContentResolver().update(MyContract.inventoryItemUri(uuid), InventoryItemTable.setNewQuantity(holder.mQuantityWidget.mSelectedQuantity), null, null);
                }
            }
        });
    }

    @Override
    public InventoryRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int layoutResId = -1;
        switch (viewType) {
            case VIEW_TYPE_ACTIVE:
                layoutResId = R.layout.adapter_inventory_active;
                break;
            case VIEW_TYPE_INACTIVE:
                layoutResId = R.layout.adapter_inventory_inactive;
                break;
            case VIEW_TYPE_FOOTER:
                layoutResId = R.layout.adapter_inventory_footer;
                break;
        }
        final View view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        return new ViewHolder(view, viewType);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {

        if (super.getItemViewType(position) == VIEW_TYPE_UNKNOWN) {
            // Workaround to fix a known crash happening in recycler view
            if (getCursor() == null || getCursor().isClosed()) {
                return VIEW_TYPE_ACTIVE;
            }
            final Cursor cursor = getCursor();
            cursor.moveToPosition(position);
            final InventoryItemReader reader = InventoryItemReader.getInstance(cursor);
            return reader.getActive() ? VIEW_TYPE_ACTIVE : VIEW_TYPE_INACTIVE;
        }
        return super.getItemViewType(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView itemName;
        public ButtonFloat quantity;
        public ExpirationWidget expiration;
        public TextView location;
        public RelativeLayout cardView;
        public LinearLayout drawer;
        public QuantityWidget mQuantityWidget;
        public int viewType;
        public boolean mIsExpanded = false;

        private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case UIConstants.ACTION_VIEW_HOLDER_COLLAPSE:
                        final int positionToCollapse = intent.getIntExtra(UIConstants.EXTRA_VIEW_HOLDER_COLLAPSE, -1);
                        if (positionToCollapse == getPosition()) {
                            collapse(false);
                        }
                        break;
                    case UIConstants.ACTION_VIEW_HOLDER_EXPAND:
                        final int expandedPosition = intent.getIntExtra(UIConstants.EXTRA_VIEW_HOLDER_EXPAND, -1);
                        if (expandedPosition != getPosition()) {
                            collapse(true);
                        }
                        break;
                    case UIConstants.ACTION_VIEW_HOLDER_COLLAPSE_ALL:
                        collapse(false);
                        break;
                }
            }
        };

        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent collapseIntent = new Intent(UIConstants.ACTION_VIEW_HOLDER_COLLAPSE_ALL);
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(collapseIntent);
                if (!mMultiSelection.isMultiSelectionModeEnabled()) {
                    final Cursor cursor = getCursor();
                    cursor.moveToPosition(getPosition());
                    final InventoryItemReader reader = InventoryItemReader.getInstance(cursor);
                    final Intent intent = new Intent(mContext, EditItemActivity.class);
                    intent.putExtra(EditItemActivity.EXTRA_ITEM_ID, reader.getItemUUID());
                    mContext.startActivity(intent);
                }
                else if (getIsItemSelectable(getPosition())){
                    mSelectedPositions.put(getPosition(), !mSelectedPositions.get(getPosition(), false));
                    mMultiSelection.onMultiSelectionChanged(getPosition(), mSelectedPositions.get(getPosition()));
                    itemView.setSelected(mSelectedPositions.get(getPosition()));
                }
            }
        };

        private final View.OnClickListener mQuantityClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mIsExpanded = !mIsExpanded;
                if (drawer.getVisibility() == View.GONE) {
                    expand();
                } else {
                    collapse(true);
                }
            }
        };

        private final View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (getIsItemSelectable(getPosition())) {
                    mMultiSelection.onMultiSelectionChanged(getPosition(), true);
                    mSelectedPositions.put(getPosition(), true);
                    itemView.setSelected(true);
                    return true;
                }
                return false;
            }
        };

        public ViewHolder(final View view, final int type) {
            super(view);
            if (type == VIEW_TYPE_FOOTER) {
                return;
            }
            ImageButton swipeButtonDo = (ImageButton) view.findViewById(R.id.do_btn);
            viewType = type;


            cardView = (RelativeLayout) view.findViewById(R.id.card_root_cont);
            cardView.setOnClickListener(mOnClickListener);
            cardView.setLongClickable(true);
            cardView.setOnLongClickListener(mOnLongClickListener);
            itemName = (TextView) view.findViewById(R.id.item_name_txt);
            location = (TextView) view.findViewById(R.id.item_location_txt);
            mQuantityWidget = (QuantityWidget) view.findViewById(R.id.quantity_widget);

            if (viewType == VIEW_TYPE_ACTIVE) {
                quantity = (ButtonFloat) view.findViewById(R.id.item_quantity_txt);
                quantity.setBackgroundColor(mContext.getResources().getColor(R.color.primary));
                expiration = (ExpirationWidget) view.findViewById(R.id.expiration_widget);
                drawer = (LinearLayout) view.findViewById(R.id.inline_quantity_cont);
                mQuantityWidget.setBackgroundColor(mContext.getResources().getColor(R.color.quantity_widget_gray));
                quantity.setOnClickListener(mQuantityClickListener);
                swipeButtonDo.setImageResource(R.drawable.shopping_list_icon);
                swipeButtonDo.setBackground(mContext.getDrawable(R.drawable.rounded_border_ripple));
            } else {
                swipeButtonDo.setImageResource(R.drawable.ic_delete_white_48dp);
                swipeButtonDo.setBackground(new ColorDrawable(android.R.color.transparent));
            }

            UndoActionWidget undoAction = (UndoActionWidget) view.findViewById(R.id.undo_widget);

            undoAction.setButtonsClick(new UndoActionWidget.undoInterface() {
                @Override
                public void callback() {
                    cardView.setTranslationX(0);
                    cardView.setAlpha(1.0f);
                    mCurrentSwipePose = -1;
                }
            });

            cardView.setOnTouchListener(new SwipeToDismissTouchListener(cardView, null, new SwipeToDismissTouchListener.DismissCallbacks() {
                @Override
                public boolean canDismiss(Object token) {
                    if (drawer != null && drawer.getVisibility() == View.VISIBLE) {
                        AnimationUtility.collapse(drawer, AnimationUtility.ANIMATION_DURATION_NONE);
                    }
                    return canBeDismissed();
                }

                @Override
                public void onDismiss(View view, Object token) {
                    final Cursor cursor = getCursor();
                    cursor.moveToPosition(getPosition());
                    if(getPosition() > mCurrentSwipePose && mCurrentSwipePose != -1){
                        mCurrentSwipePose = getPosition() -1;
                    }
                    else{
                        mCurrentSwipePose = getPosition();
                    }

                    if (cursor.isClosed() || getPosition() >= cursor.getCount()){
                        return;
                    }
                    final InventoryItemReader reader = InventoryItemReader.getInstance(cursor);
                    if (reader.getActive()) {
                        ActionExecuter.getInstance().execute(new InactiveAction(mContext,reader.getItemUUID()));
                    } else {
                        ActionExecuter.getInstance().execute(new DeleteAction(mContext, reader.getItemUUID()));
                    }

                }
            }));
        }

        private void expand() {
            notifyItemChanged(getPosition());
            AnimationUtility.expand(drawer);
            final Intent collapseIntent = new Intent(UIConstants.ACTION_VIEW_HOLDER_EXPAND);
            collapseIntent.putExtra(UIConstants.EXTRA_VIEW_HOLDER_EXPAND, getPosition());
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(collapseIntent);
            register();
        }

        private void collapse(final boolean animate) {
            mIsExpanded = false;
            notifyItemChanged(getPosition());

            if (drawer !=null) {
                if (animate) {
                    AnimationUtility.collapse(drawer);
                } else {
                    drawer.setVisibility(View.GONE);
                }
            }
            unregister();
        }

        private void register() {
            final IntentFilter filter = new IntentFilter();
            filter.addAction(UIConstants.ACTION_VIEW_HOLDER_COLLAPSE);
            filter.addAction(UIConstants.ACTION_VIEW_HOLDER_EXPAND);
            filter.addAction(UIConstants.ACTION_VIEW_HOLDER_COLLAPSE_ALL);
            LocalBroadcastManager.getInstance(mContext).registerReceiver(mReceiver, filter);
        }

        private void unregister() {
            LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mReceiver);
        }
    }


    private boolean getIsItemSelectable(final int position) {
        final Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        return InventoryItemReader.getInstance(cursor).getActive();
    }

    private int getItemBackgroundDrawableResId(int position) {
        final Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        InventoryItemReader reader = InventoryItemReader.getInstance(cursor);
        //TODO if expiration has been converted to epoch will have to update comparison
        String itemExpDate = reader.getExpiry();
        if (itemExpDate != null) {
            Date exp = stringToDate(itemExpDate);
            //TODO get current date on create or somewhere once
            if (exp.before(getCurrentDate())) { //expired
                return R.drawable.ripple_item_expired;
            } else {
                return R.drawable.ripple_item_not_expired;
            }
        } else {
            return R.drawable.ripple_item_no_expiration;
        }
    }

    private Date stringToDate(String stringDate) {
        SimpleDateFormat dateFormat = getDateFormatter();
        Date date = new Date();
        try {
            date = dateFormat.parse(stringDate);
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    private SimpleDateFormat getDateFormatter() {
        return new SimpleDateFormat(mContext.getString(R.string.expiration_date_format));
    }

    private Date getCurrentDate() {
        SimpleDateFormat dateFormat = getDateFormatter();
        Calendar c = Calendar.getInstance();
        String currentDate = dateFormat.format(c.getTime());
        return stringToDate(currentDate);
    }

    public boolean canBeDismissed() {
        return !mMultiSelection.isMultiSelectionModeEnabled();
    }

    public List<Integer> getSelectedPositions() {
        final List<Integer> selectedPosition = Lists.newArrayList();
        for(int i=0; i < getItemCount(); i++) {
            if (mSelectedPositions.get(i, false)) {
                selectedPosition.add(i);
            }
        }
        return selectedPosition;
    }

    public void clearSelectedPositions() {
        mSelectedPositions.clear();
        notifyItemRangeChanged(0, getItemCount());
    }

    public interface MultiSelectionInterface {
        void onMultiSelectionChanged(final int position, final boolean selected);
        boolean isMultiSelectionModeEnabled();
    }
}
