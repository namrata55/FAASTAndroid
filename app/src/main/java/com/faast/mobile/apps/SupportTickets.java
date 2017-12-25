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
import android.widget.LinearLayout;
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

public class SupportTickets extends AppCompatActivity {

    String UserName,submitTicketURL,checkStatusURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.support_ticket);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00ba30")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Support");

        Window window = SupportTickets.this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(SupportTickets.this.getResources().getColor(R.color.my_statusbar_color));
        }

        SharedPreferences myPrefs =getApplicationContext().getSharedPreferences("contacts", Context.MODE_PRIVATE);
        UserName = myPrefs.getString("Username","");

        SharedPreferences Links =getApplicationContext().getSharedPreferences("DatabaseLinks", Context.MODE_PRIVATE);
        submitTicketURL = Links.getString("submitticketurl","");
        checkStatusURL = Links.getString("checkstatusurl","");

        LinearLayout SlowSpeedInternet = (LinearLayout) findViewById (R.id.slow_speed);
        SlowSpeedInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SupportTickets.this);
                alertDialog.setMessage("Have you performed the speed test by going to www.speedtest.net on a laptop while being connected to the Router directly via the LAN cable?");
                alertDialog.setIcon(R.mipmap.arrow_white);
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        createTicket(UserName,"Slow Speed Internet","Slow Speed Internet");
                    }
                });
                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        AlertDialog.Builder builder111 = new AlertDialog.Builder(SupportTickets.this);
                        builder111.setMessage("Kindly perform the speed test by going to www.speedtest.net on a laptop while being connected to the Router directly via the LAN cable.")
                                .setCancelable(false)
                                .setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {
                                                dialog.dismiss();
                                            }
                                        });
                        AlertDialog alert = builder111.create();
                        if(!isFinishing()) {
                            alert.show();
                        }
                    }
                });
                if(!isFinishing()) {
                    alertDialog.show();
                }
            }
        });


        LinearLayout NoPowerOnModem = (LinearLayout) findViewById (R.id.no_power_fiber_modem);
        NoPowerOnModem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SupportTickets.this);
                alertDialog.setMessage("Have you verified if the ON/OFF switch behind the fiber modem is at ON position? and checked the power socket by connecting any other device like mobile phone charger? ");
                alertDialog.setIcon(R.mipmap.arrow_white);
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        createTicket(UserName,"No power on fiber modem","No power on fiber modem");
                    }
                });

                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        AlertDialog.Builder builder111 = new AlertDialog.Builder(SupportTickets.this);
                        builder111.setMessage("Kindly verify the ON/OFF switch behind the fiber modem and also the power socket by connecting any other device like mobile phone charger.")
                                .setCancelable(false)
                                .setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {
                                                dialog.dismiss();
                                            }
                                        });
                        AlertDialog alert = builder111.create();
                        if(!isFinishing()) {
                            alert.show();
                        }
                    }
                });
                if(!isFinishing()) {
                    alertDialog.show();
                }
            }
        });

        LinearLayout RedLightOnModem = (LinearLayout) findViewById (R.id.red_light_on);
        RedLightOnModem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SupportTickets.this);
                alertDialog.setMessage("Have you tried restarting (Powering Off and On) your Fiber modem? ");
                alertDialog.setIcon(R.mipmap.arrow_white);

                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        createTicket(UserName,"Red LOS","Red LOS On fiber modem");
                    }
                });
                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        AlertDialog.Builder builder111 = new AlertDialog.Builder(SupportTickets.this);
                        // Set the Alert Dialog Message
                        builder111.setMessage("Kindly try restarting your Fiber modem once.")
                                .setCancelable(false)
                                .setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {
                                                // Restart the Activity
                                                dialog.dismiss();
                                            }
                                        });
                        AlertDialog alert = builder111.create();
                        if(!isFinishing()) {
                            alert.show();
                        }
                    }
                });
                if(!isFinishing()) {
                    alertDialog.show();
                }
            }
        });

        LinearLayout NoLightOnAdapter = (LinearLayout) findViewById (R.id.no_light_on_adapter);
        NoLightOnAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SupportTickets.this);
                alertDialog.setMessage("Have you tried putting the adaptor into a different power socket? ");
                alertDialog.setIcon(R.mipmap.arrow_white);
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        createTicket(UserName,"No light on PoE adaptor","No light on PoE adaptor");
                        }
                });
                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        AlertDialog.Builder builder111 = new AlertDialog.Builder(SupportTickets.this);
                        // Set the Alert Dialog Message
                        builder111.setMessage("Kindly try shifting to a different socket and check for lights.")
                                .setCancelable(false)
                                .setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {
                                                // Restart the Activity
                                                dialog.dismiss();
                                            }
                                        });
                        AlertDialog alert = builder111.create();
                        if(!isFinishing()) {
                            alert.show();
                        }
                    }
                });
                alertDialog.show();
            }
        });

        LinearLayout other = (LinearLayout) findViewById (R.id.others);
        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SupportTickets.this,SupportOthersTickets.class);
                startActivity(i);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        LinearLayout TicketHistory = (LinearLayout) findViewById (R.id.ticket_history);
        TicketHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SupportTickets.this,SupportTicketsTable.class);
                startActivity(i);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
    }

    private void createTicket(String username,String subject,String comment) {

        class CreateTicketSync extends AsyncTask<String, Void, String> {

            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = createProgressDialog(SupportTickets.this );
                loadingDialog.show();
            }

            @Override
            protected String doInBackground(String... params) {
                String uname = params[0];
                String subject1 = params[1];
                String comment1 = params[2];


                InputStream is = null;
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("username", uname));
                nameValuePairs.add(new BasicNameValuePair("subject", subject1));
                nameValuePairs.add(new BasicNameValuePair("comment", comment1));
                String result = null;

                try {
                    HttpClient httpClient = new DefaultHttpClient();
//                    HttpPost httpPost = new HttpPost(
//                            "http://10.0.2.2/android_faast_db/plan_activation.php");
//                    HttpPost httpPost = new HttpPost(
//                            "http://192.168.1.17/hotspot_android/plan_activation.php");
                    HttpPost httpPost = new HttpPost(submitTicketURL);
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
                String s = result.trim();
                if(s.equals("success")){
//                    Toast toast = Toast.makeText(getApplicationContext(),"Ticket has been created successfully", Toast.LENGTH_SHORT);
//                    toast.setGravity(Gravity.CENTER, 0, 0);
//                    toast.show();

                    AlertDialog.Builder builder111 = new AlertDialog.Builder(SupportTickets.this);
                    builder111.setMessage("Ticket has been created successfully")
                            .setCancelable(false)
                            .setPositiveButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {
                                            dialog.cancel();
                                            Intent i = new Intent(getApplicationContext(),HomeInternetStatus.class);
                                            startActivity(i);
                                        }
                                    });
                    AlertDialog alert = builder111.create();
                    if(!isFinishing()) {
                        alert.show();
                    }
                }
                else{
                    Toast toast = Toast.makeText(getApplicationContext(),"Ticket is not created, please try once again.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        }

        CreateTicketSync la = new CreateTicketSync();
        la.execute(username,subject,comment);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(SupportTickets.this);
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
                if(!isFinishing()) {
                    alert.show();
                }
            }
        }
    };
    @Override
    protected void onSaveInstanceState(Bundle oldInstanceState) {
        super.onSaveInstanceState(oldInstanceState);
        oldInstanceState.clear();
    }
}