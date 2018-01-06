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
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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

public class FaastPrime extends AppCompatActivity {

    Button faastPrimeButton;
    String UserName,faastPrimeURL,planName,checkConnectionModeURL,checkNextSrvidURL;
    TextView FAASTPrimePriceTextview;
    String message = "Are you sure, You will be charged Rs.500 + taxes annually for the FAAST Prime Membership, once activated, Prime subscription cannot be cancelled for the current year.";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faast_prime);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00ba30")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("FAAST Prime");
        Window window = FaastPrime.this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(FaastPrime.this.getResources().getColor(R.color.my_statusbar_color));
        }

        FAASTPrimePriceTextview = (TextView) findViewById(R.id.FAASTPrimePrice);
        SharedPreferences myPrefs = this.getSharedPreferences("contacts", MODE_PRIVATE);
        UserName = myPrefs.getString("Username", "");

        SharedPreferences userDetails = this.getSharedPreferences("UserDetails", MODE_PRIVATE);
        planName = userDetails.getString("srvname", "");
        String[] serviceName = planName.split(" ");
        Log.e("Plan",serviceName[0]);

        Intent i = getIntent();
        String status = i.getStringExtra("faastprime_member_request");

        SharedPreferences Links = getApplicationContext().getSharedPreferences("DatabaseLinks", MODE_PRIVATE);
        faastPrimeURL = Links.getString("faastprimeurl","");
        checkConnectionModeURL = Links.getString("checkconnectionmodeurl","");
        checkNextSrvidURL = Links.getString("checknextsrvidforfaastprimeurl","");

        faastPrimeButton = (Button) findViewById(R.id.faast_prime_sign_up);

        if(status.equals("1")){
            faastPrimeButton.setVisibility(View.INVISIBLE);
        }

        if(serviceName[0].equals("SMB")){
            FAASTPrimePriceTextview.setText("* Prime membership costs Rs.1000 + Taxes yearly.");
            message = "Are you sure, You will be charged Rs.1000 + taxes annually for the FAAST Prime Membership, once activated, Prime subscription cannot be cancelled for the current year.";
        }

        faastPrimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(planName);
                check_nextsrvid(planName);
            }
        });
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
                AlertDialog.Builder builder = new AlertDialog.Builder(FaastPrime.this);
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


    private void check_nextsrvid(final String srvname) {

        class CheckNextSrvidAsync extends AsyncTask<String, Void, String> {

            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = createProgressDialog(FaastPrime.this);
                loadingDialog.show();
            }

            @Override
            protected String doInBackground(String... params) {
                String srvname = params[0];

                InputStream is = null;
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("srvname", srvname));
                String result = null;

                try {
                    HttpClient httpClient = new DefaultHttpClient();

                    HttpPost httpPost = new HttpPost(
                            checkNextSrvidURL);
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse response = httpClient.execute(httpPost);

                    HttpEntity entity = response.getEntity();

                    is = entity.getContent();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                        Log.d("Output=", line);
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
                String s = result.trim();
                System.out.println(s);
                if (s.equalsIgnoreCase("success")) {
                    check_connection_mode(UserName);
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FaastPrime.this);
                    // Set the Alert Dialog Message
                    builder.setMessage("Prime membership is currently not applicable for your current plan.")
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {
                                            // Restart the Activity
                                           dialog.dismiss();
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        }
        CheckNextSrvidAsync la = new CheckNextSrvidAsync();
        la.execute(srvname);
    }

    private void check_connection_mode(final String username) {

        class CheckConnectionModeAsync extends AsyncTask<String, Void, String> {

            private Dialog loadingDialog;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = createProgressDialog(FaastPrime.this);
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

                    HttpPost httpPost = new HttpPost(
                            checkConnectionModeURL);
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse response = httpClient.execute(httpPost);

                    HttpEntity entity = response.getEntity();

                    is = entity.getContent();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                        Log.d("Output=", line);
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
                String s = result.trim();
                System.out.println(s);
                if (s.equalsIgnoreCase("fiber")) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(FaastPrime.this);
                    alertDialog.setMessage(message);
                    alertDialog.setIcon(R.mipmap.arrow_white);
                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int which) {
                            activate_faast_prime(UserName);
                        }
                    });
                    alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
                else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(FaastPrime.this);
                    alertDialog.setMessage("Please note that you are on wireless connection, you may be able to achieve anywhere between your plan speed and upto 50Mbps, do you want to continue with prime membership? ");
                    alertDialog.setIcon(R.mipmap.arrow_white);
                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int which) {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(FaastPrime.this);
                            alertDialog.setMessage(message);
                            alertDialog.setIcon(R.mipmap.arrow_white);
                            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int which) {
                                    activate_faast_prime(UserName);
                                }
                            });
                            alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            alertDialog.show();                        }
                    });
                    alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
            }
        }

        CheckConnectionModeAsync la = new CheckConnectionModeAsync();
        la.execute(username);
    }

    private void activate_faast_prime(final String username) {

        class FaastPrimeAsync extends AsyncTask<String, Void, String> {

            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
//                loadingDialog = ProgressDialog.show(Login.this, "Please wait", "Loading...");
                loadingDialog = createProgressDialog(FaastPrime.this);
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

                    HttpPost httpPost = new HttpPost(
                            faastPrimeURL);
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse response = httpClient.execute(httpPost);

                    HttpEntity entity = response.getEntity();

                    is = entity.getContent();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                        Log.d("Output=", line);
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
                String s = result.trim();
                System.out.println(s);
                if (s.equalsIgnoreCase("success")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FaastPrime.this);
                    // Set the Alert Dialog Message
                    builder.setMessage("Congratulations, your FAAST Prime membership request has been activated successfully")
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {
                                            dialog.dismiss();
                                            Intent i = new Intent(FaastPrime.this,HomeInternetStatus.class);
                                            startActivity(i);
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else {
                    Toast toast = Toast.makeText(getApplicationContext(),"Please, try once again", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        }
        FaastPrimeAsync la = new FaastPrimeAsync();
        la.execute(username);
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

