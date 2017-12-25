package com.faast.mobile.apps;

import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class ChangePassword extends AppCompatActivity {

    private EditText old_password;
    private EditText new_password;
    private EditText confirm_password;
    private Button change_password;
    String old_password1;
    String new_password1;
    String confirm_password1;
    String URL;
    String Screensinch;
    Double ssizedouble;
    ImageView showoldpassword,shownewpassword,showcnfrmpassword,hideoldpassword,hidenewpassword,hidecnfrmpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00ba30")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Change Password");

        Window window = ChangePassword.this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ChangePassword.this.getResources().getColor(R.color.my_statusbar_color));
        }

        SharedPreferences LoginLink = this.getSharedPreferences("DatabaseLinks", MODE_PRIVATE);
        URL = LoginLink.getString("changepassword","");

        change_password = (Button) findViewById(R.id.change_password);
        old_password = (EditText) findViewById(R.id.old_password);
        new_password = (EditText) findViewById(R.id.new_password);
        confirm_password = (EditText) findViewById(R.id.confirm_password);
        showoldpassword = (ImageView)findViewById(R.id.show_old_password);
        shownewpassword = (ImageView)findViewById(R.id.show_new_password);
        showcnfrmpassword = (ImageView)findViewById(R.id.show_cnfrm_password);
        hideoldpassword = (ImageView)findViewById(R.id.hide_old_password);
        hidenewpassword = (ImageView)findViewById(R.id.hide_new_password);
        hidecnfrmpassword = (ImageView)findViewById(R.id.hide_cnfrm_password);

        SharedPreferences ScreenSizes = getApplicationContext().getSharedPreferences("ScreenSize", MODE_PRIVATE);
        Screensinch = ScreenSizes.getString("screens","");
        ssizedouble = Double.parseDouble(Screensinch);

        if(ssizedouble >= 4.00 && ssizedouble <= 4.50)
        {
            old_password.setTextSize(15);
            new_password.setTextSize(15);
            confirm_password.setTextSize(15);
            change_password.setTextSize(15);
        }
        else if(ssizedouble > 4.50 && ssizedouble <= 5.00) {
            old_password.setTextSize(16);
            new_password.setTextSize(16);
            confirm_password.setTextSize(16);
            change_password.setTextSize(16);
        }
        else if(ssizedouble > 5.0 && ssizedouble <= 5.5)
        {
            old_password.setTextSize(17);
            new_password.setTextSize(17);
            confirm_password.setTextSize(17);
            change_password.setTextSize(17);
        }
        else{
            old_password.setTextSize(18);
            new_password.setTextSize(18);
            confirm_password.setTextSize(18);
            change_password.setTextSize(18);
        }

        hideoldpassword.setOnClickListener(new View.OnClickListener() {
            Typeface typeface = old_password.getTypeface();
            @Override
            public void onClick(View view) {
                showoldpassword.setVisibility(View.VISIBLE);
                hideoldpassword.setVisibility(View.INVISIBLE);
                old_password.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD );
                old_password.setTypeface(typeface);
                old_password.setSelection(old_password.length());
            }
        });

        showoldpassword.setOnClickListener(new View.OnClickListener() {
            Typeface typeface = old_password.getTypeface();
            @Override
            public void onClick(View view) {
                hideoldpassword.setVisibility(View.VISIBLE);
                showoldpassword.setVisibility(View.INVISIBLE);
                old_password.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD );
                old_password.setTypeface(typeface);
                old_password.setSelection(old_password.length());
            }
        });

        hidenewpassword.setOnClickListener(new View.OnClickListener() {
            Typeface typeface = new_password.getTypeface();
            @Override
            public void onClick(View view) {
                shownewpassword.setVisibility(View.VISIBLE);
                hidenewpassword.setVisibility(View.INVISIBLE);
                new_password.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD );
                new_password.setTypeface(typeface);
                new_password.setSelection(new_password.length());
            }
        });

        shownewpassword.setOnClickListener(new View.OnClickListener() {
            Typeface typeface = new_password.getTypeface();
            @Override
            public void onClick(View view) {
                hidenewpassword.setVisibility(View.VISIBLE);
                shownewpassword.setVisibility(View.INVISIBLE);
                new_password.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD );
                new_password.setTypeface(typeface);
                new_password.setSelection(new_password.length());
            }
        });


        hidecnfrmpassword.setOnClickListener(new View.OnClickListener() {
            Typeface typeface = confirm_password.getTypeface();
            @Override
            public void onClick(View view) {
                showcnfrmpassword.setVisibility(View.VISIBLE);
                hidecnfrmpassword.setVisibility(View.INVISIBLE);
                confirm_password.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD );
                confirm_password.setTypeface(typeface);
                confirm_password.setSelection(confirm_password.length());
            }
        });

        showcnfrmpassword.setOnClickListener(new View.OnClickListener() {
            Typeface typeface = confirm_password.getTypeface();
            @Override
            public void onClick(View view) {
                hidecnfrmpassword.setVisibility(View.VISIBLE);
                showcnfrmpassword.setVisibility(View.INVISIBLE);
                confirm_password.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD );
                confirm_password.setTypeface(typeface);
                confirm_password.setSelection(confirm_password.length());
            }
        });
        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                invokeChangePassword(view);
            }
        });
    }

    //-----------------------------BROADCAST RECEIVER----------------------------------

    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {

            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
            boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);

            NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            NetworkInfo otherNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

            if (currentNetworkInfo.isConnected()) {

            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChangePassword.this);
                // Set the Alert Dialog Message
                builder.setMessage("Internet Connection Required")
                        .setCancelable(false)
                        .setPositiveButton("Retry",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        // Restart the Activity
                                        dialog.cancel();
                                        registerReceiver(mConnReceiver,
                                                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    };

    //-----------------------------CHANGE PASSWORD FUNCTION----------------------------------

    public void invokeChangePassword(View view) {
        old_password1 = old_password.getText().toString();
        new_password1 = new_password.getText().toString();
        confirm_password1 = confirm_password.getText().toString();
        String space="";

        if (old_password1.equals(space) && new_password1.equals(space) && confirm_password1.equals(space) ) {
            Toast toast = Toast.makeText(getApplicationContext(),"Enter Passwords", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            old_password.requestFocus();
        }
        else if(old_password1.equals(space)){
            Toast toast = Toast.makeText(getApplicationContext(), "Enter Old Password", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            old_password.requestFocus();
        }
        else if(new_password1.equals(space)){
            Toast toast = Toast.makeText(getApplicationContext(),"Enter New Password", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            new_password.requestFocus();
        }
        else if(confirm_password1.equals(space)){
            Toast toast = Toast.makeText(getApplicationContext(),"Enter Confirm Password", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            confirm_password.requestFocus();
        }
        else if (old_password1.equals(new_password1) && old_password1.equals(confirm_password1)) {
            old_password.setText("");
            new_password.setText("");
            confirm_password.setText("");
            old_password.requestFocus();
            Toast toast = Toast.makeText(getApplicationContext(),"Old password and New password are same", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

        else if (new_password1.equals(confirm_password1) && old_password1 != null) {
            update_password(old_password1, new_password1);
        }
        else if( new_password1 != confirm_password1)
        {
            new_password.setText("");
            confirm_password.setText("");
            new_password.requestFocus();
            Toast toast = Toast.makeText(getApplicationContext(),"New Password and Confirm Password should be same", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

    }

    private void update_password(final String old_password1, final String new_password1) {

        class LoginAsync extends AsyncTask<String, Void, String> {

            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //loadingDialog = ProgressDialog.show(ChangePassword.this, "Please wait", "Loading...");
                loadingDialog = createProgressDialog(ChangePassword.this);
                loadingDialog.show();
            }

            @Override
            protected String doInBackground(String... params) {

                SharedPreferences myPrefs = getApplicationContext().getSharedPreferences("contacts",MODE_PRIVATE);
                final String final_username = myPrefs.getString("Username","");
                Log.d("val",final_username);

                InputStream is = null;
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("username",final_username));
                nameValuePairs.add(new BasicNameValuePair("old_password", old_password1));
                nameValuePairs.add(new BasicNameValuePair("new_password", new_password1));
                String result = null;

                try {
                    HttpClient httpClient = new DefaultHttpClient();
//                    HttpPost httpPost = new HttpPost(
//                            "http://10.0.2.2/android_faast_db/change_password.php");
                    HttpPost httpPost = new HttpPost(
                            URL);
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse response = httpClient.execute(httpPost);

                    HttpEntity entity = response.getEntity();

                    is = entity.getContent();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                        Log.d("value=", line);
                    }
                    result = sb.toString();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                String s = result.trim();

                loadingDialog.dismiss();
                if (s.equalsIgnoreCase("success")) {
                    Log.d("value=", result);
                    Toast toast = Toast.makeText(getApplicationContext(),"Password changed successfully", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    Intent intent = new Intent(ChangePassword.this, HomeInternetStatus.class);
                    finish();
                    startActivity(intent);

                } else {
                    old_password.setText("");
                    new_password.setText("");
                    confirm_password.setText("");
                    old_password.requestFocus();

                    Toast toast = Toast.makeText(getApplicationContext(),"Invalid Old Password", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        }

        LoginAsync la = new LoginAsync();
        la.execute(old_password1, new_password1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);

    }

    @Override
    public void onResume() {
        super.onResume();
        this.registerReceiver(this.mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        this.unregisterReceiver(mConnReceiver);
        if(isApplicationSentToBackground(this))
        {
            finishAffinity();
        }
    }

    public boolean isApplicationSentToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public static ProgressDialog createProgressDialog(Context mContext) {
        ProgressDialog dialog = new ProgressDialog(mContext);
        try {
            dialog.show();
        } catch (WindowManager.BadTokenException e) {

        }
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.progress_dialogue);
        // dialog.setMessage(Message);
        return dialog;
    }
    @Override
    protected void onSaveInstanceState(Bundle oldInstanceState) {
        super.onSaveInstanceState(oldInstanceState);
        oldInstanceState.clear();
    }

}
