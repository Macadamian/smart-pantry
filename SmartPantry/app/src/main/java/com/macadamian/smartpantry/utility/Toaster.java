package com.macadamian.smartpantry.utility;

import android.content.Context;
import android.widget.Toast;

public class Toaster {

    private static Toast mToast;

    public static void makeText(final Context context, final String message, final int length) {
        maybeDismissToast();
        mToast = Toast.makeText(context, message, length);
        mToast.show();
    }

    private static void maybeDismissToast() {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
    }
}
