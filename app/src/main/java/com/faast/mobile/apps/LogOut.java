package com.faast.mobile.apps;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

public class LogOut extends AppCompatActivity {
    SessionManager manager;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logout);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Window window = LogOut.this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(LogOut.this.getResources().getColor(R.color.my_statusbar_color));
        }

        manager = new SessionManager();
        manager.setPreferences(LogOut.this, "status", "0");

        SharedPreferences myPrefs = this.getSharedPreferences("contacts",MODE_PRIVATE);
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.clear();
        editor.commit();

        SharedPreferences myPrefs1 = this.getSharedPreferences("AmountData",MODE_PRIVATE);
        SharedPreferences.Editor editor1=myPrefs1.edit();
        editor1.clear();
        editor1.commit();

        SharedPreferences myPrefs2 = this.getSharedPreferences("UserDetails",MODE_PRIVATE);
        SharedPreferences.Editor editor2 = myPrefs2.edit();
        editor2.clear();
        editor2.commit();

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            public void run() {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Intent in = new Intent(getApplicationContext(), Login.class);
                        finishAffinity();
                        startActivity(in);
                        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                        timer.cancel();
                    }
                });

            }

        }, 1000);

    }
    @Override
    protected void onSaveInstanceState(Bundle oldInstanceState) {
        super.onSaveInstanceState(oldInstanceState);
        oldInstanceState.clear();
    }
}
