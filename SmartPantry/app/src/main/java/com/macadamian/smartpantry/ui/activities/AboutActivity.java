package com.macadamian.smartpantry.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.macadamian.smartpantry.R;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        String[] urlArray = getResources().getStringArray(R.array.about_url_array);

        LinearLayout urls = (LinearLayout) findViewById(R.id.url_container);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Resources r = getResources();
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                r.getDimension(R.dimen.about_url_margin),
                r.getDisplayMetrics());

        params.setMargins(0, px, 0, 0);

        for (int urlIndex = 1; urlIndex < urlArray.length; urlIndex += 2) {
            int titleIndex = urlIndex - 1;

            TextView label = new TextView(this);
            label.setText(urlArray[titleIndex]);
            label.setLayoutParams(params);
            label.setGravity(Gravity.CENTER);
            urls.addView(label);

            TextView view = new TextView(this);
            view.setAutoLinkMask(Linkify.WEB_URLS);
            view.setText(urlArray[urlIndex]);
            view.setMovementMethod(LinkMovementMethod.getInstance());
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.about_text_size));
            view.setGravity(Gravity.CENTER);
            urls.addView(view);
        }

        findViewById(R.id.view_Apache_license_btn).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                final TextView message = new TextView(AboutActivity.this);
                message.setTextColor(getResources().getColor(R.color.black));
                final SpannableString s = new SpannableString(getApplicationContext().getText(R.string.about_dialog_Apache_message_url));
                Linkify.addLinks(s, Linkify.WEB_URLS);
                message.setText(s);
                message.setGravity(Gravity.CENTER);
                message.setMovementMethod(LinkMovementMethod.getInstance());
                new AlertDialog.Builder(AboutActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                        .setTitle(getString(R.string.about_dialog_Apache_title))
                        .setView(message)
                        .setPositiveButton(getString(R.string.item_dialog_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
            }});

        findViewById(R.id.view_MIT_license_btn).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                new AlertDialog.Builder(AboutActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                        .setTitle(getString(R.string.about_dialog_MIT_title))
                        .setMessage(R.string.about_dialog_MIT_message)
                        .setPositiveButton(getString(R.string.item_dialog_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }});
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
