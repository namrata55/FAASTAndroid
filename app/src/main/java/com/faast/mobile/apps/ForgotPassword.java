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
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class ForgotPassword extends AppCompatActivity {

    EditText fp_username1;
    Button fp_button;
    TextView login_button;
    TextView fp_lable;

    String username;
    String URL;
    String Screensinch;
    Double ssizedouble;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Window window = ForgotPassword.this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ForgotPassword.this.getResources().getColor(R.color.my_statusbar_color));
        }

        SharedPreferences LoginLink = this.getSharedPreferences("DatabaseLinks", MODE_PRIVATE);
        URL = LoginLink.getString("forgotpassword","");
        fp_username1 = (EditText) findViewById(R.id.fp_username);
        login_button = (TextView) findViewById(R.id.fp_login);
        fp_lable = (TextView) findViewById(R.id.forgot_password_label);

        fp_button = (Button)findViewById(R.id.submit);
        SharedPreferences ScreenSizes = getApplicationContext().getSharedPreferences("ScreenSize", MODE_PRIVATE);
        Screensinch = ScreenSizes.getString("screens","");
        ssizedouble = Double.parseDouble(Screensinch);
        String sss = String.valueOf(ssizedouble);

        Log.d("double size2=",sss);

        if(ssizedouble >= 4.00 || ssizedouble < 4.50)
        {
            //Toast.makeText(getApplicationContext(), "4.0-5.0", Toast.LENGTH_LONG).show();
            fp_username1.setTextSize(15);

            fp_button.setTextSize(15);
            login_button.setTextSize(12);
            fp_lable.setTextSize(25);
        }
        else if(ssizedouble >= 4.50 || ssizedouble <= 5.00)
        {
            // Toast.makeText(getApplicationContext(), "<5.0", Toast.LENGTH_LONG).show();
            fp_username1.setTextSize(20);
            fp_button.setTextSize(20);
            login_button.setTextSize(16);
            fp_lable.setTextSize(30);
        }
        else if(ssizedouble > 5.0 || ssizedouble <= 5.5)
        {
            // Toast.makeText(getApplicationContext(), "5.0-5.5", Toast.LENGTH_LONG).show();
            fp_username1.setTextSize(20);
            fp_button.setTextSize(20);
            login_button.setTextSize(16);
            fp_lable.setTextSize(35);
        }
        else{
            // Toast.makeText(getApplicationContext(), "<4.0", Toast.LENGTH_LONG).show();
            fp_username1.setTextSize(20);
            fp_button.setTextSize(20);
            login_button.setTextSize(14);
            fp_lable.setTextSize(40);
        }

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ForgotPassword.this, Login.class);
                finish();
                startActivity(i);
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
            }
        });
    }
    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
            boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);

            NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            NetworkInfo otherNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

            if (currentNetworkInfo.isConnected()) {


            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPassword.this);
                // Set the Alert Dialog Message
                builder.setMessage("Internet Connection Required")
                        .setCancelable(false)
                        .setPositiveButton("Retry",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        // Restart the Activity
                                        registerReceiver(mConnReceiver,
                                                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    };

    //-----------------------------FORGOT PASSWORD FUNCTION----------------------------------

    public void invokefp(View view) {
        username = fp_username1.getText().toString();
        if(username.equals(""))
        {
            Toast toast = Toast.makeText(getApplicationContext(),"Enter Username Or Mobile No.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        else {
            login(username);
        }
    }

    private void login(final String username) {

        class LoginAsync extends AsyncTask<String, Void, String> {

            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //loadingDialog = ProgressDialog.show(ForgotPassword.this, "Please wait", "Loading...");
                loadingDialog = createProgressDialog(ForgotPassword.this);
                loadingDialog.show();
            }

            @Override
            protected String doInBackground(String... params) {
                String uname = params[0];

                InputStream is = null;
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("username", uname));
                String result = null;

                try {
                    HttpClient httpClient = new DefaultHttpClient();
//                    HttpPost httpPost = new HttpPost(
//                            "http://10.0.2.2/android_faast_db/ForgotPassword.php");
                    HttpPost httpPost = new HttpPost(URL);
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
                loadingDialog.dismiss();
                System.out.println(result);
                String s = result.trim();
                if (s.equalsIgnoreCase("success")) {
                    Toast toast = Toast.makeText(getApplicationContext(),"Password has been sent to your registered mobile number.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    Intent i=new Intent(ForgotPassword.this,Login.class);
                    startActivity(i);
                    finish();

                } else {
                    fp_username1.setText("");
                    Toast toast = Toast.makeText(getApplicationContext(),"Invalid Username or Registerd Mobile No.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    fp_username1.requestFocus();
                }
            }
        }
        LoginAsync la = new LoginAsync();
        la.execute(username);
    }
    @Override
    public void onResume() {
        super.onResume();
        fp_username1.setText("");
        fp_username1.requestFocus();
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

    @Override
    public void onBackPressed() {
        fp_username1.setText("");
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
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

    @Override
    protected void onSaveInstanceState(Bundle oldInstanceState) {
        super.onSaveInstanceState(oldInstanceState);
        oldInstanceState.clear();
    }
}


