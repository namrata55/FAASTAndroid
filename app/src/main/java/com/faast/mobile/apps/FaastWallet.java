package com.faast.mobile.apps;

import android.app.Activity;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.razorpay.Checkout;

import org.json.JSONObject;

import java.util.List;

public class FaastWallet extends AppCompatActivity {
    TextView amountToBePaidTextView;
    TextView walletAmountTextView;
    TextView payBalanceTextView;
    Button payNowButton;
    String  RazorpayKeyId,RazorpayKeySecret;
    Integer Final_Amount;
    String s;
    String razorpayPaymentID1;
    Integer amt1;
    Integer i,i1,uv,uv1;
    String FirstNameP,emailP,mobileP,UserName;
    String value,updateinv;
    String rzChargeURL;
    String updateInvoiceURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faast_wallet);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00ba30")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Complete Your Payment");

        Window window = FaastWallet.this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(FaastWallet.this.getResources().getColor(R.color.my_statusbar_color));
        }

        SharedPreferences myPrefs = this.getSharedPreferences("contacts", MODE_PRIVATE);
        UserName = myPrefs.getString("Username", "");

        SharedPreferences Links = getApplicationContext().getSharedPreferences("DatabaseLinks", MODE_PRIVATE);
        rzChargeURL = Links.getString("rzcharge","");
        updateInvoiceURL = Links.getString("updateinvoice","");

        walletAmountTextView = (TextView) findViewById(R.id.wallet_Amount);
        amountToBePaidTextView = (TextView) findViewById(R.id.amount_to_be_paid);
        payBalanceTextView = (TextView) findViewById(R.id.pay_balance);
        payNowButton = (Button) findViewById(R.id.pay_now);
        payNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPayment(100.22);
            }
        });
    }

    //-----------------------------RAZOR PAYMENT ----------------------------------

    public void startPayment(Double Amount){
        /**
         * Put your key id generated in Razorpay dashboard here
         */

        SharedPreferences myPrefs = this.getSharedPreferences("RazorpayKeysDetails", MODE_PRIVATE);
        RazorpayKeyId = myPrefs.getString("razorpaykeyid", "");
        RazorpayKeySecret = myPrefs.getString("razorpaykeysecret", "");

        SharedPreferences myPrefs1 = this.getSharedPreferences("UserDetails",MODE_PRIVATE);
        FirstNameP = myPrefs1.getString("firstname","");
        mobileP = myPrefs1.getString("mobile","");
        emailP = myPrefs1.getString("email","");
        Checkout razorpayCheckout = new Checkout();
        razorpayCheckout.setKeyID(RazorpayKeyId);

        /**
         * Image for checkout form can passed as reference to a drawable
         */
        razorpayCheckout.setImage(R.mipmap.arrow);

        /**
         * Reference to current activity
         */

        //Total_amount = Double.parseDouble(Amount);
        Double d = new Double(Amount);
        Long L = Math.round(d);
        int i = Integer.valueOf(L.intValue());
        Final_Amount = Integer.valueOf(L.intValue());
        amt1 = Final_Amount*100;

        Activity activity = this;

        try{
            JSONObject options = new JSONObject("{" +
                    "description: '<purchase description>'," +
                    "currency: 'INR'}"
            );

            /**
             * Image for checkout form can also be set from a URL
             * For this, pass URL inside JSONObject as following:
             *
             * options.put("image", "<link to image>");
             */
            Integer amt = Final_Amount*100;
            options.put("amount", amt);
            options.put("name", FirstNameP);
            options.put("prefill", new JSONObject("{email: '"+emailP+"',contact: '"+mobileP+"',name: '"+FirstNameP+"'}"));
            options.put("theme", new JSONObject("{color: '#00ba30'}"));
            razorpayCheckout.open(activity, options);

        } catch(Exception e){
            e.printStackTrace();
        }
    }


    /**
     * The name of the function has to be
     *   onPaymentSuccess
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */
    public void onPaymentSuccess(String razorpayPaymentID){

        try {

            s=String.valueOf(amt1);
            razorpayPaymentID1=razorpayPaymentID;
            final GetData getdb = new GetData();

            new Thread(new Runnable() {
                public void run() {
                    value= getdb.getData(RazorpayKeyId,RazorpayKeySecret,s,razorpayPaymentID1,rzChargeURL);

                    System.out.println(value);
                    i=new Integer(value);
                    i1=i.intValue();

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if(i1 == 1)
                            {
                                final UpdateInvoice ui = new UpdateInvoice();


                                new Thread(new Runnable() {
                                    public void run() {
                                        updateinv= ui.updateInvoice(UserName,razorpayPaymentID1,updateInvoiceURL);

                                        System.out.println("upadted invoice:"+updateinv);
                                        uv=new Integer(value);
                                        uv1=uv.intValue();

                                        runOnUiThread(new Runnable() {

                                            @Override
                                            public void run() {
                                                if(updateinv.equalsIgnoreCase("success"))
                                                {
                                                    Toast.makeText(getApplicationContext(), "Payment Successful", Toast.LENGTH_LONG).show();
                                                    Intent i=new Intent(FaastWallet.this,HomeInternetStatus.class);
                                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    finishAffinity();
                                                    startActivity(i);
                                                }
                                                else
                                                {
                                                    Toast.makeText(getApplicationContext(), "Payment Successful", Toast.LENGTH_LONG).show();
                                                    Intent i=new Intent(getApplicationContext(),HomeInternetStatus.class);
                                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    finishAffinity();
                                                    startActivity(i);
                                                }
                                            }
                                        });

                                    }
                                }).start();

                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "Payment UnSuccessful", Toast.LENGTH_LONG).show();
                                Intent i=new Intent(getApplicationContext(),HomeInternetStatus.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                finishAffinity();
                                startActivity(i);
                            }
                        }
                    });

                }
            }).start();
        }
        catch (Exception e){
            Log.e("com.merchant", e.getMessage(), e);
        }
    }
    /**
     * The name of the function has to be
     *   onPaymentError
     * Wrap your code in try catch, as shown, to ensure that this method runs correctl8q
     */
    public void onPaymentError(int code, String response) {
        try {

//            Toast.makeText(this, "Payment failed: " + Integer.toString(code) + " " + response, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Payment Cancelled", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e("com.merchant", e.getMessage(), e);
        }
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
                AlertDialog.Builder builder = new AlertDialog.Builder(FaastWallet.this);
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
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
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
