package com.faast.mobile.apps;

import android.app.ActivityManager;
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
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class PaymentHistory extends AppCompatActivity {
    String UserName;
    String paymenthistoryURL;
    String data = "";
    ProgressDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_history);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00ba30")));
        getSupportActionBar().setTitle("Payment History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        loadingDialog = createProgressDialog(PaymentHistory.this);
        loadingDialog.show();

        Window window = PaymentHistory.this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(PaymentHistory.this.getResources().getColor(R.color.my_statusbar_color));
        }

        SharedPreferences myPrefs = this.getSharedPreferences("contacts",MODE_PRIVATE);
        UserName = myPrefs.getString("Username","");

        SharedPreferences Links = this.getSharedPreferences("DatabaseLinks", MODE_PRIVATE);
        paymenthistoryURL = Links.getString("paymenthistorylink","");

        final GetPaymentHistory getdb = new GetPaymentHistory();
        new Thread(new Runnable() {
            public void run() {
                data = getdb.getDataFromDb(UserName,paymenthistoryURL);
                System.out.println("payment history"+data);

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        ArrayList<Users> users = parseJSON(data);
                        addData(users);

                    }
                });

            }
        }).start();


    }

    public void addData(ArrayList<Users> users) {

        if(users.size() == 1){

            LinearLayout paymenthistorylayout1 = (LinearLayout)findViewById(R.id.payment_history1);
            paymenthistorylayout1.setVisibility(View.VISIBLE);

            LinearLayout paymenthistorylayout2 = (LinearLayout)findViewById(R.id.payment_history2);
            paymenthistorylayout2.setVisibility(View.GONE);

            LinearLayout paymenthistorylayout3 = (LinearLayout)findViewById(R.id.payment_history3);
            paymenthistorylayout3.setVisibility(View.GONE);

            TextView Payment_header = (TextView) findViewById(R.id.payment_header);
            Payment_header.setVisibility(View.VISIBLE);
            Payment_header.setText("Last 1 Payment");

            Users p1 = users.get(0);
            String paymentdate = getconvertdate1(p1.getPaymentDate());
            TextView paymentdatetextview1 = (TextView) findViewById(R.id.pdatevalue1);
            paymentdatetextview1.setText(paymentdate);

            Double d = Double.valueOf(p1.getPaymentAmount()).doubleValue();
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            String paymentamount = formatter.format(d);

            TextView paymentamounttextview1 = (TextView) findViewById(R.id.pamountvalue1);
            paymentamounttextview1.setText(paymentamount);

            TextView payment_method1 = (TextView) findViewById(R.id.pmethodvalue1);
            payment_method1.setText(p1.getPaymentMethod());

            System.out.print("pamount1:"+p1.getPaymentAmount());
            System.out.print("pdate1:"+p1.getPaymentDate());
        }
        else if(users.size() == 2) {

            LinearLayout paymenthistorylayout1 = (LinearLayout)findViewById(R.id.payment_history1);
            paymenthistorylayout1.setVisibility(View.VISIBLE);

            LinearLayout paymenthistorylayout2 = (LinearLayout)findViewById(R.id.payment_history2);
            paymenthistorylayout2.setVisibility(View.VISIBLE);

            LinearLayout paymenthistorylayout3 = (LinearLayout)findViewById(R.id.payment_history3);
            paymenthistorylayout3.setVisibility(View.GONE);

            TextView Payment_header = (TextView) findViewById(R.id.payment_header);
            Payment_header.setVisibility(View.VISIBLE);
            Payment_header.setText("Last 2 Payments");

            Users p1 = users.get(0);

            String paymentdate1 = getconvertdate1(p1.getPaymentDate());
            TextView paymentdatetextview1 = (TextView) findViewById(R.id.pdatevalue1);
            paymentdatetextview1.setText(paymentdate1);

            Double d = Double.valueOf(p1.getPaymentAmount()).doubleValue();
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            String paymentamount = formatter.format(d);

            TextView paymentamounttextview1 = (TextView) findViewById(R.id.pamountvalue1);
            paymentamounttextview1.setText(paymentamount);
            System.out.print("pamount1:" + p1.getPaymentAmount());
            System.out.print("pdate1:" + p1.getPaymentDate());

            TextView payment_method1 = (TextView) findViewById(R.id.pmethodvalue1);
            payment_method1.setText(p1.getPaymentMethod());

            Users p2 = users.get(1);
            String paymentdate2 = getconvertdate1(p2.getPaymentDate());
            TextView paymentdatetextview2 = (TextView) findViewById(R.id.pdatevalue2);
            paymentdatetextview2.setText(paymentdate2);

            Double d1 = Double.valueOf(p2.getPaymentAmount()).doubleValue();
            NumberFormat formatter1 = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            String paymentamount1 = formatter1.format(d1);

            TextView paymentamounttextview2 = (TextView) findViewById(R.id.pamountvalue2);
            paymentamounttextview2.setText(paymentamount1);

            TextView payment_method2 = (TextView) findViewById(R.id.pmethodvalue2);
            payment_method2.setText(p2.getPaymentMethod());

            System.out.print("pamount1:" + p2.getPaymentAmount());
            System.out.print("pdate1:" + p2.getPaymentDate());
        }
        else if(users.size() == 3){

            LinearLayout paymenthistorylayout1 = (LinearLayout)findViewById(R.id.payment_history1);
            paymenthistorylayout1.setVisibility(View.VISIBLE);

            LinearLayout paymenthistorylayout2 = (LinearLayout)findViewById(R.id.payment_history2);
            paymenthistorylayout2.setVisibility(View.VISIBLE);

            LinearLayout paymenthistorylayout3 = (LinearLayout)findViewById(R.id.payment_history3);
            paymenthistorylayout3.setVisibility(View.VISIBLE);

            TextView Payment_header = (TextView) findViewById(R.id.payment_header);
            Payment_header.setVisibility(View.VISIBLE);
            Payment_header.setText("Last 3 Payments");

            Users p1 = users.get(0);
            String paymentdate1 = getconvertdate1(p1.getPaymentDate());
            TextView paymentdatetextview1 = (TextView) findViewById(R.id.pdatevalue1);
            paymentdatetextview1.setText(paymentdate1);

            Double d = Double.valueOf(p1.getPaymentAmount()).doubleValue();
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            String paymentamount = formatter.format(d);
            TextView paymentamounttextview1 = (TextView) findViewById(R.id.pamountvalue1);
            paymentamounttextview1.setText(paymentamount);


            TextView payment_method1 = (TextView) findViewById(R.id.pmethodvalue1);
            payment_method1.setText(p1.getPaymentMethod());

            System.out.print("pamount1:"+p1.getPaymentAmount());
            System.out.print("pdate1:"+p1.getPaymentDate());

            Users p2 = users.get(1);
            String paymentdate2 = getconvertdate1(p2.getPaymentDate());
            TextView paymentdatetextview2 = (TextView) findViewById(R.id.pdatevalue2);
            paymentdatetextview2.setText(paymentdate2);

            Double d1 = Double.valueOf(p2.getPaymentAmount()).doubleValue();
            NumberFormat formatter1 = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            String paymentamount1 = formatter1.format(d1);
            TextView paymentamounttextview2 = (TextView) findViewById(R.id.pamountvalue2);
            paymentamounttextview2.setText(paymentamount1);

            TextView payment_method2 = (TextView) findViewById(R.id.pmethodvalue2);
            payment_method2.setText(p2.getPaymentMethod());

            System.out.print("pamount1:"+p2.getPaymentAmount());
            System.out.print("pdate1:"+p2.getPaymentDate());

            Users p3 = users.get(2);
            String paymentdate3 = getconvertdate1(p3.getPaymentDate());
            TextView paymentdatetextview3 = (TextView) findViewById(R.id.pdatevalue3);
            paymentdatetextview3.setText(paymentdate3);

            Double d2 = Double.valueOf(p3.getPaymentAmount()).doubleValue();
            NumberFormat formatter2 = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            String paymentamount2 = formatter2.format(d2);
            TextView paymentamounttextview3 = (TextView) findViewById(R.id.pamountvalue3);
            paymentamounttextview3.setText(paymentamount2);

            TextView payment_method3 = (TextView) findViewById(R.id.pmethodvalue3);
            payment_method3.setText(p3.getPaymentMethod());

            System.out.print("pamount1:"+p3.getPaymentAmount());
            System.out.print("pdate1:"+p3.getPaymentDate());
        }
        else{

            TextView Payment_header = (TextView) findViewById(R.id.payment_header);
            Payment_header.setVisibility(View.VISIBLE);
            Payment_header.setText("Sorry! No Payment History");

            LinearLayout paymenthistorylayout1 = (LinearLayout)findViewById(R.id.payment_history1);
            paymenthistorylayout1.setVisibility(View.GONE);

            LinearLayout paymenthistorylayout2 = (LinearLayout)findViewById(R.id.payment_history2);
            paymenthistorylayout2.setVisibility(View.GONE);

            LinearLayout paymenthistorylayout3 = (LinearLayout)findViewById(R.id.payment_history3);
            paymenthistorylayout3.setVisibility(View.GONE);
        }
        loadingDialog.dismiss();
    }

    //-----------------------------DATE FORMAT CONVERSION FUNCTION----------------------------------

    public String getconvertdate1(String date)
    {
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date parsed = new Date();
        try
        {
            parsed = inputFormat.parse(date);
        }
        catch (ParseException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String outputText = outputFormat.format(parsed);
        return outputText;
    }

    public ArrayList<Users> parseJSON(String result) {
        ArrayList<Users> users = new ArrayList<Users>();
        try {
            JSONArray jArray = new JSONArray(result);
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json_data = jArray.getJSONObject(i);
                Users user = new Users();

                user.setPaymentDate(json_data.getString("payment_date"));
                user.setPaymentAmount(json_data.getString("payment_amount"));
                user.setPaymentMethod(json_data.getString("payment_method"));
                users.add(user);
            }
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
        return users;
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
                AlertDialog.Builder builder = new AlertDialog.Builder(PaymentHistory.this);
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

}
