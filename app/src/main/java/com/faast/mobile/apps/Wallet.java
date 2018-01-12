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
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class Wallet extends AppCompatActivity {

    String UserName,getWalletHistoryURL;
    String data = "";
    public TableLayout walletHistoryTableLayout;
    TableRow tr;
    Integer num =1;
    String walletbalance;
    TextView walletBalanceTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallet);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00ba30")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("FAAST Wallet");

        Window window = Wallet.this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Wallet.this.getResources().getColor(R.color.my_statusbar_color));
        }
        SharedPreferences myPrefs = this.getSharedPreferences("contacts", MODE_PRIVATE);
        UserName = myPrefs.getString("Username", "");

        SharedPreferences Links = this.getSharedPreferences("DatabaseLinks", MODE_PRIVATE);
        getWalletHistoryURL = Links.getString("getwallethistory", "");

        walletHistoryTableLayout = (TableLayout) findViewById(R.id.maintable);
        walletBalanceTextView = (TextView) findViewById(R.id.availaible_wallet_balance);
        final GetWalletHistory getdb = new GetWalletHistory();
        new Thread(new Runnable() {
            public void run() {
                data = getdb.getWalletHistory(UserName, getWalletHistoryURL);

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        ArrayList<Users> users = parseJSON(data);
                        if(users.size()==0){
                            NumberFormat formatter2 = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
                            String zero_in_string = formatter2.format(0.00);

                            TextView tickets_status_table_textview = (TextView) findViewById(R.id.wallet_history_title);
                            tickets_status_table_textview.setVisibility(View.INVISIBLE);
                            walletBalanceTextView.setText(zero_in_string);
                        }
                        else {
                            addData(users);
                        }
                        System.out.println("Output:" + users);
                    }
                });
            }
        }).start();
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
                AlertDialog.Builder builder = new AlertDialog.Builder(Wallet.this);
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

    //-----------------------------PARSE JSON----------------------------------

    public ArrayList<Users> parseJSON(String result) {
        ArrayList<Users> users = new ArrayList<Users>();
        try {
            JSONArray jArray = new JSONArray(result);
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json_data = jArray.getJSONObject(i);
                Users user = new Users();
                user.setWallerCredit(json_data.getString("credit"));
                user.setWalletDebit(json_data.getString("debit"));
                user.setWalletBalance(json_data.getString("balance"));
                user.setWalletDate(json_data.getString("date"));
                users.add(user);
            }
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
        return users;
    }

    //Table Header
    void addHeader() {
        /** Create a TableRow dynamically **/

        tr = new TableRow(this);

        /** Creating a TextView to add to the row **/
        TextView numTextView = new TextView(this);
        numTextView.setText("No.");
        numTextView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        numTextView.setBackgroundColor(Color.parseColor("#00b1ba"));
        numTextView.setTextColor(Color.parseColor("#FFFFFF"));
        numTextView.setGravity(Gravity.CENTER);
        numTextView.setPadding(7, 7, 7, 7);
        numTextView.setGravity(Gravity.CENTER);
        numTextView.setWidth(30);
        LinearLayout Ll = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        Ll.addView(numTextView, params);
        tr.addView((View) Ll); // Adding textView to tablerow.

        /** Creating a TextView to add to the row **/
        TextView dateTextView = new TextView(this);
        dateTextView.setText("Date");
        dateTextView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        dateTextView.setBackgroundColor(Color.parseColor("#00b1ba"));
        dateTextView.setTextColor(Color.parseColor("#FFFFFF"));
        dateTextView.setGravity(Gravity.CENTER);
        dateTextView.setPadding(7, 7, 7, 7);
        dateTextView.setGravity(Gravity.CENTER);
        dateTextView.setWidth(30);
        LinearLayout L2 = new LinearLayout(this);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        L2.addView(dateTextView, params2);
        tr.addView((View) L2); // Adding textView to tablerow.

        TextView CreditTextView = new TextView(this);
        CreditTextView.setText("Credit");
        CreditTextView.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        CreditTextView.setBackgroundColor(Color.parseColor("#00b1ba"));
        CreditTextView.setTextColor(Color.parseColor("#FFFFFF"));

        CreditTextView.setGravity(Gravity.CENTER);
        CreditTextView.setWidth(30);

        CreditTextView.setPadding(7, 7, 7, 7);
        CreditTextView.setGravity(Gravity.CENTER);

        LinearLayout L3 = new LinearLayout(this);
        params = new LinearLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
        L3.addView(CreditTextView, params);
        tr.addView((View) L3); // Adding textview to tablerow.

        TextView debitTextView = new TextView(this);
        debitTextView.setText("Debit");
        debitTextView.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        debitTextView.setBackgroundColor(Color.parseColor("#00b1ba"));
        debitTextView.setTextColor(Color.parseColor("#FFFFFF"));

        debitTextView.setPadding(7, 7, 7, 7);
        debitTextView.setGravity(Gravity.CENTER);

        Ll = new LinearLayout(this);
        params = new LinearLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
        Ll.addView(debitTextView, params);
        tr.addView((View) Ll); // Adding textview to tablerow.

        // Add the TableRow to the TableLayout

        /** Creating Qty Button **/
        TextView balanceTextViewHeader = new TextView(this);
        balanceTextViewHeader.setText("Balance");
        balanceTextViewHeader.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        balanceTextViewHeader.setBackgroundColor(Color.parseColor("#00b1ba"));
        balanceTextViewHeader.setTextColor(Color.parseColor("#FFFFFF"));

        balanceTextViewHeader.setPadding(7, 7, 7, 7);
        balanceTextViewHeader.setGravity(Gravity.CENTER);

        Ll = new LinearLayout(this);
        params = new LinearLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
        Ll.addView(balanceTextViewHeader, params);
        tr.addView((View) Ll); // Adding textview to tablerow.

        // Add the TableRow to the TableLayout
        walletHistoryTableLayout.addView(tr, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
    }

    public void addData(ArrayList<Users> users) {

        addHeader();

        for (Iterator i = users.iterator(); i.hasNext(); ) {

            Users p = (Users) i.next();

            /** Create a TableRow dynamically **/
            tr = new TableRow(this);

            /** Creating a TextView to add to the row **/
            TextView numTextView = new TextView(this);
            numTextView.setText(String.valueOf(num));
            num = num + 1;
            numTextView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            numTextView.setBackground(getResources().getDrawable(
                    R.drawable.grey_border));
            // numTextView.setPadding(3, 3, 3, 3);
            numTextView.setGravity(Gravity.CENTER);
            LinearLayout Ll = new LinearLayout(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT);
            Ll.addView(numTextView, params);
            tr.addView((View) Ll); // Adding textView to tablerow.

            TextView dateTextView = new TextView(this);
            dateTextView.setText(p.getWalletDate());
            dateTextView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            dateTextView.setBackground(getResources().getDrawable(
                    R.drawable.grey_border));
            // dateTextView.setPadding(3, 3, 3, 3);
            dateTextView.setGravity(Gravity.CENTER);
            LinearLayout L2 = new LinearLayout(this);
            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT);
            L2.addView(dateTextView, params2);
            tr.addView((View) L2); // Adding textView to tablerow.

            /** Creating Qty Button **/
            TextView creditTextView = new TextView(this);
            creditTextView.setText(p.getWallerCredit());
            creditTextView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            creditTextView.setBackground(getResources().getDrawable(
                    R.drawable.grey_border));
            creditTextView.setGravity(Gravity.CENTER);
            Ll = new LinearLayout(this);
            params = new LinearLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT);
            Ll.addView(creditTextView, params);
            tr.addView((View) Ll); // Adding textview to tablerow.

            TextView debitTextView = new TextView(this);
            debitTextView.setText(p.getWalletDebit());
            debitTextView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            debitTextView.setBackground(getResources().getDrawable(
                    R.drawable.grey_border));
            debitTextView.setGravity(Gravity.CENTER);
            Ll = new LinearLayout(this);
            params = new LinearLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT);
            Ll.addView(debitTextView, params);
            tr.addView((View) Ll); // Adding textview to tablerow.

            TextView balanceTextView = new TextView(this);
            balanceTextView.setText(p.getWalletBalance()+" ");
            if(walletbalance == null){
                walletbalance =  p.getWalletBalance();
            }
            balanceTextView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            balanceTextView.setBackground(getResources().getDrawable(
                    R.drawable.grey_border));
            balanceTextView.setGravity(Gravity.RIGHT);
            Ll = new LinearLayout(this);
            params = new LinearLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT);
            Ll.addView(balanceTextView, params);
            tr.addView((View) Ll); // Adding textview to tablerow.

            // Add the TableRow to the TableLayout
            walletHistoryTableLayout.addView(tr, new TableLayout.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
        }
        System.out.println("Wallet Balance:"+walletbalance);

        Double walletAvailaibleBalance = Double.parseDouble(walletbalance);
        NumberFormat formatter3 = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        String walletBalance = formatter3.format(walletAvailaibleBalance);

        walletBalanceTextView.setText(walletBalance);
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
}
