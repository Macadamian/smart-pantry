package com.macadamian.smartpantry.widgets;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.macadamian.smartpantry.R;
import com.macadamian.smartpantry.content.action.ActionExecuter;
import com.macadamian.smartpantry.ui.UIConstants;

public class UndoActionWidget extends RelativeLayout {

    private final Context mContext;

    public UndoActionWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.widget_undo_action, this, true);
    }

    public interface undoInterface{
        void callback();
    }

    public void setButtonsClick(final undoInterface callback) {
        ImageButton doBtn = (ImageButton) findViewById(R.id.do_btn);
        doBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionExecuter.getInstance().execute();
                callback.callback();
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(UIConstants.ACTION_ANIMATE_SPLAT));
            }
        });

        Button undo = (Button) findViewById(R.id.undo_btn);
        undo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionExecuter.getInstance().undo();
                callback.callback();
            }
        });
    }
}
