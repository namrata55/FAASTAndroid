package com.faast.mobile.apps;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class AppVersion extends AppCompatActivity {
    TextView version_num;
    ImageView backbutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_version);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Window window = AppVersion.this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(AppVersion.this.getResources().getColor(R.color.my_statusbar_color));
        }
        version_num = (TextView) findViewById(R.id.version);
        String appVersion = BuildConfig.VERSION_NAME;

        version_num.setText("version : "+appVersion);

        backbutton = (ImageView) findViewById(R.id.back_button);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

}
