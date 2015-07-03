package com.macadamian.smartpantry.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.macadamian.smartpantry.R;

public class EmptyViewWidget extends LinearLayout {

    private TextView mLabel;
    private Button mAction;

    public EmptyViewWidget(Context context) {
        this(context, null);
    }

    public EmptyViewWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptyViewWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public EmptyViewWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.widget_empty_view, this, true);

        mLabel = (TextView) findViewById(R.id.pantry_empty_view_label);
        mAction = (Button) findViewById(R.id.pantry_empty_view_button);
    }

    public void setLabel(final int resId) {
        mLabel.setText(resId);
    }

    public void setAction(final int resId) {
        mAction.setText(resId);
    }

    public void setActionVisibility(final int visibility) {
        mAction.setVisibility(visibility);
    }

    public void setActionListener(final OnClickListener listener) {
        mAction.setOnClickListener(listener);
    }
}
