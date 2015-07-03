package com.macadamian.smartpantry.utility;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import java.util.List;


public class InputUtility {

    public static void setupUI(final View view, final Activity activity, List<Integer> excluded) {

        if (excluded.contains(view.getId())){
            return;
        }

        //Set up touch listener for non-text box views to hide keyboard.
        view.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard(activity);
                return false;
            }

        });

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setupUI(innerView, activity, excluded);
            }
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            activity.getCurrentFocus().clearFocus();
        }
    }

    public static void showSoftKeyboard(final Activity activity, final View view) {
        final InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (view != null) {
            view.requestFocus();
            inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        }
    }
}
