package com.macadamian.smartpantry.widgets;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.macadamian.smartpantry.R;
import com.macadamian.smartpantry.ui.UIConstants;

public class QuantityWidget extends LinearLayout {

    private ImageToggleButton mNone;
    private ImageToggleButton mSome;
    private ImageToggleButton mEnough;
    private ImageToggleButton mLots;
    public Integer mSelectedQuantity;

    public QuantityWidget(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.widget_quantity, this, true);

        mNone = (ImageToggleButton) findViewById(R.id.quantity_none_btn);
        mSome = (ImageToggleButton) findViewById(R.id.quantity_some_btn);
        mEnough = (ImageToggleButton) findViewById(R.id.quantity_enough_btn);
        mLots = (ImageToggleButton) findViewById(R.id.quantity_lots_btn);

    }

    public interface ClickCallback{
        void onClick();
    }

    public void setButtonClickListener(final ClickCallback callback){

       Button.OnClickListener quantityClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.quantity_none_btn:
                        mSelectedQuantity = UIConstants.QUANTITY_NONE;
                        updateCheckOfQuantity(v.getId());
                        callback.onClick();
                        break;
                    case R.id.quantity_some_btn:
                        mSelectedQuantity = UIConstants.QUANTITY_SOME;
                        updateCheckOfQuantity(v.getId());
                        callback.onClick();
                        break;
                    case R.id.quantity_enough_btn:
                        mSelectedQuantity = UIConstants.QUANTITY_ENOUGH;
                        updateCheckOfQuantity(v.getId());
                        callback.onClick();
                        break;
                    case R.id.quantity_lots_btn:
                        mSelectedQuantity = UIConstants.QUANTITY_LOTS;
                        updateCheckOfQuantity(v.getId());
                        callback.onClick();
                        break;
                }
            }
        };

        mNone.setOnClickListener(quantityClickListener);
        mSome.setOnClickListener(quantityClickListener);
        mEnough.setOnClickListener(quantityClickListener);
        mLots.setOnClickListener(quantityClickListener);
    }

    public void setSelectedQuantity(Integer quantity, Boolean isActive) {
        if(isActive){
            setSelectedQuantity(quantity);
        }
        else{
            setSelectedQuantity(UIConstants.QUANTITY_NONE);
        }
    }

    public int getSelectedQuantity(){
        return mSelectedQuantity;
    }

    public void setSelectedQuantity(Integer quantity) {
        mSelectedQuantity = quantity;
        switch (quantity){
            case UIConstants.QUANTITY_NONE:
                mNone.setChecked(true);
                mSome.setChecked(false);
                mEnough.setChecked(false);
                mLots.setChecked(false);
                break;
            case UIConstants.QUANTITY_SOME:
                mNone.setChecked(false);
                mSome.setChecked(true);
                mEnough.setChecked(false);
                mLots.setChecked(false);
                break;
            case UIConstants.QUANTITY_ENOUGH:
                mNone.setChecked(false);
                mSome.setChecked(false);
                mEnough.setChecked(true);
                mLots.setChecked(false);
                break;
            case UIConstants.QUANTITY_LOTS:
                mNone.setChecked(false);
                mSome.setChecked(false);
                mEnough.setChecked(false);
                mLots.setChecked(true);
                break;
        }
    }

    public void updateCheckOfQuantity(int selfID){
        mNone.setChecked(selfID == R.id.quantity_none_btn);
        mSome.setChecked(selfID == R.id.quantity_some_btn);
        mEnough.setChecked(selfID == R.id.quantity_enough_btn);
        mLots.setChecked(selfID == R.id.quantity_lots_btn);
    }
}
