package com.macadamian.smartpantry.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.macadamian.smartpantry.R;
import com.macadamian.smartpantry.utility.DateUtility;

import org.apache.commons.lang3.StringUtils;

import java.sql.Date;
import java.util.Calendar;

public class ExpirationWidget extends ImageView {

    public enum States {
        None,       // No expiration date set
        Expired,    // Expired
        VerySoon,   // Tomorrow
        Soon,       // Between tomorrow and one week
        InAWeek     // Between one week and more
    }

    private Calendar mCalendar;
    private States mState = States.None;

    public ExpirationWidget(Context context) {
        this(context, null);
    }

    public ExpirationWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpirationWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ExpirationWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setDate(final String dateString) {
        if (StringUtils.isEmpty(dateString)) {
            mCalendar = null;
            mState = States.None;
        }
        else {
            final Date date = Date.valueOf(dateString);
            mCalendar = Calendar.getInstance();
            mCalendar.setTime(date);
            updateState();
        }
        updateDisplay();
    }

    private void updateState() {
        final Calendar today = Calendar.getInstance();
        today.setTimeInMillis(System.currentTimeMillis());

        mCalendar.set(Calendar.HOUR, today.get(Calendar.HOUR));
        mCalendar.set(Calendar.HOUR_OF_DAY, today.get(Calendar.HOUR_OF_DAY));
        mCalendar.set(Calendar.MINUTE, today.get(Calendar.MINUTE));
        mCalendar.set(Calendar.SECOND, today.get(Calendar.SECOND));
        mCalendar.set(Calendar.MILLISECOND, today.get(Calendar.MILLISECOND));

        final long comparison = DateUtility.compareCalendars(today, mCalendar);
        if (comparison < DateUtility.ONE_DAY) {
            mState = States.Expired;
        }
        else if (comparison < DateUtility.ONE_WEEK) {
            mState = States.VerySoon;
        }
        else if (comparison < DateUtility.ONE_WEEK * 2) {
            mState = States.Soon;
        }
        else {
            mState = States.InAWeek;
        }
    }

    private void updateDisplay() {
        switch(mState) {
            case None:
                setImageResource(R.drawable.ic_expiration_none);
                break;
            case Expired:
                setImageResource(R.drawable.ic_expiration_red);
                break;
            case VerySoon:
                setImageResource(R.drawable.ic_expiration_orange);
                break;
            case Soon:
                setImageResource(R.drawable.ic_expiration_blue);
                break;
            case InAWeek:
                setImageResource(R.drawable.ic_expiration_green);
                break;
        }
    }
}
