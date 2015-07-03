package com.macadamian.smartpantry.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageButton;

import com.macadamian.smartpantry.R;

public class ImageToggleButton extends ImageButton {

    private Drawable mDrawableOn;
    private Drawable mDrawableOff;
    private boolean mIsChecked = false;

    public ImageToggleButton(Context context) {
        this(context, null);
    }

    public ImageToggleButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageToggleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ImageToggleButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.ImageToggleButton, defStyleAttr, defStyleRes);

        final int drawableOnReference = a.getResourceId(R.styleable.ImageToggleButton_drawable_on, -1);
        final int drawableOffReference = a.getResourceId(R.styleable.ImageToggleButton_drawable_off, -1);

        mDrawableOn = getResources().getDrawable(drawableOnReference);
        mDrawableOff = getResources().getDrawable(drawableOffReference);
        mIsChecked = a.getBoolean(R.styleable.ImageToggleButton_drawable_checked, false);
        syncDrawableState();
        a.recycle();
    }

    private void syncDrawableState() {
        setImageDrawable(mIsChecked ? mDrawableOn : mDrawableOff);
    }

    public void setChecked(final boolean checked) {
        mIsChecked = checked;
        syncDrawableState();
    }

    public boolean getIsChecked() {
        return mIsChecked;
    }
}
