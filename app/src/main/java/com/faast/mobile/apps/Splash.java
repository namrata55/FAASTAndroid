package com.faast.mobile.apps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.text.DecimalFormat;

public class Splash extends AppCompatActivity {
    SessionManager manager;
    TextView version_num;
    String versionName;

    String msg=null;
      String server_path="http://rm.faast.in/ucp/android_ucp1/";
//      String server_path="http://rm.faast.in/ucp/android_ucp_demo/";
//      String server_path="http://192.168.1.17/hotspot_android/";
//    String server_path="http://10.0.2.2:81/android_faast_db/";
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    String token;

    WifiManager wifiManager;
    WifiInfo wInfo;
    String macAddress;

    final String razorpay_key_id = "rzp_live_yjf2wTSV05B9Fq";
    final String razorpay_key_secret = "hbjVhO5y7rbpapfk5cAGEtEJ";

//    final String razorpay_key_id = "rzp_test_uDpwVNvR5hvR9N";
//    final String razorpay_key_secret = "aYyJ1mipvXYX0RVfYKX2LCVH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        version_num = (TextView)findViewById(R.id.version);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00ba30")));

        Window window = Splash.this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Splash.this.getResources().getColor(R.color.my_statusbar_color));
        }
        Intent i = getIntent();
        msg = i.getStringExtra("Message");

        SharedPreferences databaseLinks = getApplicationContext().getSharedPreferences("DatabaseLinks", MODE_PRIVATE);
        SharedPreferences.Editor editor = databaseLinks.edit();
        editor.putString("login",server_path+"login.php");
//        editor.putString("login",server_path+"login_with_notif_token.php");
        editor.putString("forgotpassword",server_path+"forgot_password.php");
        editor.putString("changepassword",server_path+"change_password.php");
        editor.putString("homeuserdata",server_path+"home_page.php");
//        editor.putString("homeuserdata",server_path+"home_page_user_details.php");
        editor.putString("homeduedata",server_path+"get_due_amount.php");
        editor.putString("userdetails",server_path+"get_user_details.php");
        editor.putString("usagereport",server_path+"usage_report.php");
        editor.putString("updateinvoice",server_path+"update_invoice.php");
        editor.putString("rzcharge",server_path+"razorpay_payment/charge.php");
        editor.putString("unpaidinvoices",server_path+"get_invoices.php");
        editor.putString("paymenthistorylink",server_path+"payment_history.php");
        editor.putString("planchangeactivation",server_path+"plan_activation.php");
        editor.putString("getallplans",server_path+"current_planwise_all_plans.php");
        editor.putString("currentplandetails",server_path+"current_plan.php");
        editor.putString("topupsactivation",server_path+"topups_activation.php");
        editor.putString("detailedusage",server_path+"usage_detailed_report.php");
        editor.putString("submitticketurl",server_path+"support_create_ticket.php");
        editor.putString("checkstatusurl",server_path+"check_support_tickets.php");
        editor.putString("supportableurl",server_path+"support_tickets_table.php");
        editor.putString("gettopupsurl",server_path+"get_topups_list.php");
        editor.putString("getnonverifiedticket",server_path+"get_non_verified_ticket.php");
        editor.putString("getsupportcomments",server_path+"support_comments.php");
        editor.putString("faastprimeurl",server_path+"faast_prime_activation.php");
        editor.putString("imagesliderurl",server_path+"get_images.php");
        editor.putString("requestforreactivationurl",server_path+"request_for_reactivation.php");
        editor.putString("checkconnectionmodeurl",server_path+"check_connection_mode.php");
        editor.putString("checknextsrvidforfaastprimeurl",server_path+"check_nextsrvid_for_faast_prime.php");
        editor.commit();

        SharedPreferences razorpay = getApplicationContext().getSharedPreferences("RazorpayKeysDetails", MODE_PRIVATE);
        SharedPreferences.Editor razorpayEditor = razorpay.edit();
        razorpayEditor.putString("razorpaykeyid",razorpay_key_id);
        razorpayEditor.putString("razorpaykeysecret",razorpay_key_secret);
        razorpayEditor.commit();

        DisplayMetrics met = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(met);// get display metrics object
        String strSize =
                new DecimalFormat("##.##").format(Math.sqrt(((met.widthPixels / met.xdpi) *
                        (met.widthPixels / met.xdpi)) +
                        ((met.heightPixels / met.ydpi) * (met.heightPixels / met.ydpi))));
        Double screens = Double.parseDouble(strSize);
        String sss = String.valueOf(screens);
        Log.d("size=",strSize);
        Log.d("double size=",sss);

        SharedPreferences ss = getApplicationContext().getSharedPreferences("ScreenSize", MODE_PRIVATE);
        SharedPreferences.Editor sseditor = ss.edit();
        sseditor.putString("screens", sss);
        sseditor.commit();

        //-----------------------------BROADCAST RECEIVER----------------------------------

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {

            //When the broadcast received
            //We are sending the broadcast from GCMRegistrationIntentService

            @Override
            public void onReceive(Context context, Intent intent) {

                if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)){
                    //Getting the registration token from the intent
                    token = intent.getStringExtra("token");
                    //Displaying the token as toast
                    Toast.makeText(getApplicationContext(), "Registration token:" + token, Toast.LENGTH_LONG).show();

                    //if the intent is not with success then displaying error messages
                } else if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)){
                    Toast.makeText(getApplicationContext(), "GCM registration error!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Error occurred", Toast.LENGTH_LONG).show();
                }
            }
        };

        //Checking play service is available or not
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        //if play service is not available
        if(ConnectionResult.SUCCESS != resultCode) {
            //If play service is supported but not installed
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                //Displaying message that play service is not installed
                Toast.makeText(getApplicationContext(), "Google Play Service is not install/enabled in this device!", Toast.LENGTH_LONG).show();
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());

                //If play service is not supported
                //Displaying an error message
            } else {
                Toast.makeText(getApplicationContext(), "This device does not support for Google Play Service!", Toast.LENGTH_LONG).show();
            }

            //If play service is available
        } else {
            //Starting intent to register device
            Intent itent = new Intent(this, GCMRegistrationIntentService.class);
            startService(itent);
        }


        manager=new SessionManager();

        Thread background = new Thread() {
            public void run() {

                try {
                    // Thread will sleep for 5 seconds
                    sleep(2000);
                    String status = manager.getPreferences(Splash.this,"status");
                    Log.d("status",status);
                    if (status.equals("1")){
                        Intent i = new Intent(Splash.this,HomeInternetStatus.class);
                        i.putExtra("Message",msg);
                        finish();
                        startActivity(i);
                        overridePendingTransition(R.anim.enter, R.anim.exit);

                    }else{
                        Intent i = new Intent(Splash.this,Login.class);
                        i.putExtra("Message",msg);
                        finish();
                        startActivity(i);
                        overridePendingTransition(R.anim.enter, R.anim.exit);

                    }
                    finish();

                } catch (Exception e) {

                }
            }
        };
        // start thread
        background.start();
    }

}





