package com.macadamian.smartpantry.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.macadamian.smartpantry.R;

public class NavDrawerAdapter extends RecyclerView.Adapter<NavDrawerAdapter.ViewHolder> {

    public final static int NAVIGATION_ITEM_INSERT_LOCATION = 0;
    public final static int NAVIGATION_ITEM_ABOUT = 1;

    private final Context mContext;
    private final NavDrawerAdapterListener mListener;

    private final int[] mNavigationItemList = new int[] {
            R.string.action_insert_location,
            R.string.action_about
    };

    private final int[] mNavigationDrawableList = new int[] {
            R.drawable.ic_action_place,
            R.drawable.ic_launcher
    };

    public NavDrawerAdapter(final Context context, final NavDrawerAdapterListener listener) {
        mContext = context;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_nav_drawer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setupViewHolder(position);
    }

    @Override
    public int getItemCount() {
        return mNavigationItemList.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mNavigationItem;

        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClicked(getPosition());
            }
        };

        public ViewHolder(View itemView) {
            super(itemView);
            mNavigationItem = (TextView) itemView.findViewById(R.id.navigation_item);
            itemView.setOnClickListener(mOnClickListener);
        }

        public void setupViewHolder(final int position) {
            mNavigationItem.setText(mContext.getString(mNavigationItemList[position]));
            mNavigationItem.setCompoundDrawablesWithIntrinsicBounds(mContext.getDrawable(mNavigationDrawableList[position]), null, null, null);
        }
    }

    public interface NavDrawerAdapterListener {
        public void onItemClicked(final int position);
    }
}
