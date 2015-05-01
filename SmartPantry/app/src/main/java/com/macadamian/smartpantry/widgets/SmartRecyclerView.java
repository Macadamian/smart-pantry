package com.macadamian.smartpantry.widgets;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

public class SmartRecyclerView extends RecyclerView {

    private View mEmptyView;

    public SmartRecyclerView(Context context) {
        super(context);
    }

    public SmartRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SmartRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void showEmptyView(final boolean show) {
        if (mEmptyView != null) {
            mEmptyView.setVisibility(show ? VISIBLE : GONE);
        }
    }

    public void setEmptyView(final View view) {
        mEmptyView = view;
    }
}
