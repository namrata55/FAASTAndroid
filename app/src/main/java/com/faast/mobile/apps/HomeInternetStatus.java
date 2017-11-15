package com.faast.mobile.apps;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.NotificationManager;
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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.koushikdutta.ion.Ion;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import pl.pawelkleczkowski.customgauge.CustomGauge;

public class HomeInternetStatus extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,PaymentResultListener {
    private CustomGauge gauge1;
    static public String dd="";
    static public String dm="";
    GridView gv;
    public static String[] menu_list = {"Payment", "Unpaid Invoices", "Usage", "TopUps", "Password", "Support"};
    public static int[] menu_images = {R.mipmap.online_payment1, R.mipmap.payment, R.mipmap.usage_report, R.mipmap.topups_green, R.mipmap.change_password2, R.mipmap.support11};
    TextView srvname_textview, due_date_textview, due_amount_textview, graph_value;
    String FirstName, mobile, UserName;
    private Activity mActivity;

    public Double outstanding_amount;


    JSONParser jsonParser= new JSONParser();
    ArrayList<HashMap<String, String>> profileList = new ArrayList<HashMap<String, String>>();
    JSONArray matchFixture = null;
    String faast_prime_member_status,faast_prime_member_request,srvname;

    private static final String TAG_SUCCESS1= "success";
    private static final String TAG_PRODUCTS1= "fixture";
    private static final String TAG_FIRSTNAME1 = "firstname";
    private static final String TAG_LASTNAME1 = "lastname";
    private static final String TAG_SRVNAME1 = "srvname";
    private static final String TAG_USEDDATA1 = "used_data";
    private static final String TAG_REMAININGDATA1 = "remaining_data";
    private static final String TAG_TOTALDATA1 = "total_data";
    private static final String TAG_EMAIL1 = "email";
    private static final String TAG_MOBILE1= "mobile";
    private static final String TAG_STATUS1= "account_status";
    private static final String TAG_ACC_TYPE= "account_type";
    private static final String TAG_FLAG_FAAST_PRIME= "flag_for_faast_prime";
    private static final String TAG_FAAST_PRIME_STATUS= "faast_prime_member";
    private static final String TAG_FAAST_PRIME_REQUEST= "faast_prime_request";
    private static final String TAG_DUEAMOUNT = "due_amount";
    private static final String TAG_DUEDATE = "due_date";

    private ProgressDialog pDialog1;
    JSONParser jsonParser1 = new JSONParser();
    ArrayList<HashMap<String, String>> profileList1 = new ArrayList<HashMap<String, String>>();
    JSONArray matchFixture1 = null;
    String dueAmountURL;
    String homeUserDataURL;
    String updateInvoiceURL;
    String getNonVerifiedTicketsURL;
    String rzChargeURL;
    String imageSliderURL;
    Double ssizedouble;

    String  RazorpayKeyId,RazorpayKeySecret;

    private NotificationManager mNotificationManager;

    Double Total_amount;
    Integer Final_Amount;
    String s;
    String razorpayPaymentID1;
    Integer amt1;
    Integer i,i1,uv,uv1;
    String FirstNameP,emailP,mobileP;
    String Screensinch;

    TextView current_plan_lable,due_amount_lable,due_date_lable;


    //Navigation Bar
    TextView nav_username;

    //----------navigation drawer variables
    ImageView nav_back_button;
    NavigationView navigationView;
    View header;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    String message;
    AlertDialog alertUpdate;
    AlertDialog.Builder BuilderNotification;
    AlertDialog NotificationAlertBox;

    String accountStatus,accountType;
    String value,updateinv;

//    public static final String BROADCAST = "com.faast.mobile.apps.android.action.mConnReceiver";

    private int index1 = 0;
    private ViewFlipper mViewFlipper1;
    com.android.volley.RequestQueue rq1;
    List<String> sliderImg1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_new);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00ba30")));

        Window window = HomeInternetStatus.this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(HomeInternetStatus.this.getResources().getColor(R.color.my_statusbar_color));
        }

        mActivity = HomeInternetStatus.this;
        mNotificationManager=(NotificationManager) this.getApplicationContext().getSystemService(this.NOTIFICATION_SERVICE);

//        ViewFlipper flipper=(ViewFlipper)findViewById(R.id.viewFlipper);
//        final Animation animFlipInNext = AnimationUtils.loadAnimation(this, R.anim.flipinnext);
//        final Animation animFlipOutNext = AnimationUtils.loadAnimation(this, R.anim.flipoutnext);
//
//        flipper.setInAnimation(animFlipInNext);
//        flipper.setOutAnimation(animFlipOutNext);
//        flipper.showNext();
//        flipper.startFlipping();

        SharedPreferences myPrefs = this.getSharedPreferences("contacts", MODE_PRIVATE);
        UserName = myPrefs.getString("Username", "");

        SharedPreferences Links = getApplicationContext().getSharedPreferences("DatabaseLinks", MODE_PRIVATE);
        homeUserDataURL = Links.getString("homeuserdata","");
        dueAmountURL = Links.getString("homeduedata","");
        rzChargeURL = Links.getString("rzcharge","");
        updateInvoiceURL = Links.getString("updateinvoice","");
        imageSliderURL = Links.getString("imagesliderurl","");
        getNonVerifiedTicketsURL = Links.getString("getnonverifiedticket","");

//        SharedPreferences ScreenSizes = getApplicationContext().getSharedPreferences("ScreenSize", MODE_PRIVATE);
//        Screensinch = ScreenSizes.getString("screens","");
//        ssizedouble = Double.parseDouble(Screensinch);

        Intent i = getIntent();
        message = i.getStringExtra("Message");

        gauge1 = (CustomGauge) findViewById(R.id.gauge2);
        graph_value = (TextView) findViewById(R.id.graph_value);
        srvname_textview = (TextView) findViewById(R.id.current_plan_value);
        due_amount_textview = (TextView) findViewById(R.id.total_payment_due);
        due_date_textview = (TextView) findViewById(R.id.payment_due_date);
        current_plan_lable = (TextView) findViewById(R.id.current_plan);
        due_amount_lable = (TextView) findViewById(R.id.total_payment_lable);
        due_date_lable = (TextView) findViewById(R.id.due_date_textview);

        gv = (GridView) findViewById(R.id.gridView1);
        gv.setAdapter(new CustomAdapter(HomeInternetStatus.this, menu_list, menu_images));

        rq1 = Volley.newRequestQueue(HomeInternetStatus.this);
        sliderImg1 = new ArrayList<>();
        sliderImg1.add(0,"http://rm.faast.in/partners/android_img_upload/1504337954.png");
        sliderImg1.add(1,"http://rm.faast.in/partners/android_img_upload/1504337954.png");
        sliderImg1.add(2,"http://rm.faast.in/partners/android_img_upload/1504337954.png");
        sliderImg1.add(3,"http://rm.faast.in/partners/android_img_upload/1504337954.png");
        Log.e("Size====", String.valueOf(sliderImg1.isEmpty()));


//        sliderImg1 =  new LinkedList<String>(Arrays.asList("R.mipmap.slide2","R.mipmap.slide3","R.mipmap.slide4","R.mipmap.slide5"));

        ImageView image = getNewImageView1();
//        image.setImageResource(R.mipmap.loader);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);

        final Animation animFlipInNext = AnimationUtils.loadAnimation(this, R.anim.flipinnext);
        final Animation animFlipOutNext = AnimationUtils.loadAnimation(this, R.anim.flipoutnext);

        mViewFlipper1 = (ViewFlipper) findViewById(R.id.viewFlipper);
        mViewFlipper1.setInAnimation(animFlipInNext);
        mViewFlipper1.setOutAnimation(animFlipOutNext);

        mViewFlipper1.addView(image);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            changeTextStatus(true);
        } else {
            changeTextStatus(false);
        }

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        header = navigationView.getHeaderView(0);

        nav_back_button =(ImageView) header.findViewById(R.id.nav_back_arrow);
        nav_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

    }

    public class MyTimerTask1 extends TimerTask {

        @Override
        public void run() {
            HomeInternetStatus.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    ImageView imageView1 = getNewImageView1();
                    imageView1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    // Where we will place the image
                    Ion.with(imageView1)
//                            .placeholder(R.mipmap.loader)
                            .load(getNextImage1());

                    mViewFlipper1.addView(imageView1); // Adding the image to the flipper
                    mViewFlipper1.showNext();
                }
            });
        }
    }

    public void sendRequest1(){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, imageSliderURL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response1) {
                for(int i=0;i<response1.length();i++){
//                    SliderUtils slideUtils1 = new SliderUtils();

                    try {
                        JSONObject jsonObject = response1.getJSONObject(i);
//                        if(jsonObject.getString("image_url") != "")
//                        {
//                            sliderImg1.clear();
//                           Log.e("Array is empty:", String.valueOf(sliderImg1.isEmpty()));
//                        }
//                           System.out.println("Array is empty:"+ jsonObject.getString("image_url"));                        }
//                        slideUtils1.setSliderImageUrl(jsonObject.getString("image_url"));
                        sliderImg1.add(i,jsonObject.getString("image_url"));

                } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
//                Log.e("URL---", String.valueOf(response1));
//                Log.e("URL---", String.valueOf(sliderImg1));

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }
        );
        rq1.add(jsonArrayRequest);
    }

    protected ImageView getNewImageView1() {
        ImageView image1 = new ImageView(getApplicationContext());
        image1.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        return image1;
    }

    protected String getNextImage1() {
//        if (index == sliderImg.size())
//            index = 0;
//        Log.e("Image====", sliderImg.get(index++).toString());
//        Log.e("ENtered", String.valueOf(index1));
//        Log.e("ENtered",sliderImg1.get(index1));
        if(index1 == 0){
            index1 = 1;
            return String.valueOf(sliderImg1.get(1));
        }
        else if(index1 == 1){
            index1 = 2;
            return String.valueOf(sliderImg1.get(2));
        }
        else if(index1 == 2){
            index1 = 3;
            return String.valueOf(sliderImg1.get(3));
        }
        else{
            index1 = 0;
            return String.valueOf(sliderImg1.get(0));
        }
    }

    public void changeTextStatus(boolean isConnected) {

        // Change status according to boolean value
        if (isConnected) {
            sendRequest1();
            Timer timer1 = new Timer();
            timer1.scheduleAtFixedRate(new MyTimerTask1(), 3000,5000);

            new LoadUserDetails().execute();
            System.out.println("Connected");
        } else {
//            Toast.makeText(getApplicationContext(),"disConnected",Toast.LENGTH_SHORT).show();
            System.out.println("Disconnected");

                  AlertDialog.Builder builder111 = new AlertDialog.Builder(HomeInternetStatus.this);
            // Set the Alert Dialog Message
            builder111.setMessage("Internet Connection Required")
                    .setCancelable(false)
                    .setPositiveButton("Retry",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    // Restart the Activity
                                    dialog.cancel();
//                                    mActivity.startActivity(mActivity.getIntent());
                                    Intent intent = getIntent();
                                    overridePendingTransition(0, 0);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    finish();
                                    overridePendingTransition(0, 0);
                                    startActivity(intent);

                                }
                            });
            AlertDialog alert = builder111.create();
            alert.show();
        }
    }

    //-----------------------------VALIDATING FOR BACK BUTTON PRESSED----------------------------------

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {

        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        else {
            if (doubleBackToExitPressedOnce) {
                finishAffinity();
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press once again to exit" +
                    "", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {

                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    //-------------Navigation Drawer----------------------

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.home_menu) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }else if (id == R.id.faast_prime_menu) {
            Intent faastprime = new Intent(this,FaastPrime.class);
            faastprime.putExtra("faastprime_member_request",faast_prime_member_request);
            startActivity(faastprime);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }else if (id == R.id.payment_hisory_menu) {
            Intent paymenthistory = new Intent(this,PaymentHistory.class);
            startActivity(paymenthistory);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        } else if (id == R.id.upgrade_plan_menu) {
            Intent planChange = new Intent(this,PlanChange.class);
            startActivity(planChange);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        } else if (id == R.id.contact_us_menu) {
            Intent paymenthistory = new Intent(this,ContactUs.class);
            startActivity(paymenthistory);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        } else if (id == R.id.version_menu) {
            Intent version = new Intent(this,AppVersion.class);
            startActivity(version);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }
        else if (id == R.id.offers_menu) {
            Intent offers = new Intent(this, Offers.class);
            startActivity(offers);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }
        else if (id == R.id.logout) {
            Intent offers = new Intent(this, LogOut.class);
            startActivity(offers);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }
//        else if (id == R.id.support_menu) {
//            Intent offers = new Intent(this, SupportTickets.class);
//            startActivity(offers);
//            overridePendingTransition(R.anim.enter, R.anim.exit);
//        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeInternetStatus.this);
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

    class LoadUserDetails extends AsyncTask<String, String, String> {

        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog1 = createProgressDialog(HomeInternetStatus.this);
            pDialog1.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            String success;

            try {
                Log.d("try", "in the try");
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                params.add(new BasicNameValuePair("username", UserName));
                JSONObject json = jsonParser1.makeHttpRequest(
                        homeUserDataURL, "POST", params);
                Log.d("jsonObject", "new json Object");

                matchFixture1 = json.getJSONArray(TAG_PRODUCTS1);
                JSONObject c1 = matchFixture1.getJSONObject(0);
                success = c1.getString(TAG_SUCCESS1);
                if (success.equals("1") ) {

                    for (int i = 0; i < matchFixture1.length(); i++) {
                        System.out.println(matchFixture1);

                        JSONObject c = matchFixture1.getJSONObject(i);

                        String firstname = c.getString(TAG_FIRSTNAME1);
                        String lastname = c.getString(TAG_LASTNAME1);
                        String srvname = c.getString(TAG_SRVNAME1);
                        String used_data = c.getString(TAG_USEDDATA1);
                        String remaining_data = c.getString(TAG_REMAININGDATA1);
                        String total_data = c.getString(TAG_TOTALDATA1);
                        String mobile = c.getString(TAG_MOBILE1);
                        String email = c.getString(TAG_EMAIL1);
                        String acc_status = c.getString(TAG_STATUS1);
                        String acc_type = c.getString(TAG_ACC_TYPE);
                        String flag_faast_prime = c.getString(TAG_FLAG_FAAST_PRIME);
                        String faast_prime_member_status = c.getString(TAG_FAAST_PRIME_STATUS);
                        String faast_prime_member_request = c.getString(TAG_FAAST_PRIME_REQUEST);
                        String due_date = c.getString(TAG_DUEDATE);
                        String due_amount = c.getString(TAG_DUEAMOUNT);
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_FIRSTNAME1, firstname);
                        map.put(TAG_LASTNAME1, lastname);
                        map.put(TAG_SRVNAME1, srvname);
                        map.put(TAG_USEDDATA1, used_data);
                        map.put(TAG_REMAININGDATA1, remaining_data);
                        map.put(TAG_TOTALDATA1, total_data);
                        map.put(TAG_EMAIL1, email);
                        map.put(TAG_MOBILE1, mobile);
                        map.put(TAG_STATUS1, acc_status);
                        map.put(TAG_ACC_TYPE, acc_type);
                        map.put(TAG_FLAG_FAAST_PRIME, flag_faast_prime);
                        map.put(TAG_FAAST_PRIME_STATUS, faast_prime_member_status);
                        map.put(TAG_FAAST_PRIME_REQUEST, faast_prime_member_request);
                        map.put(TAG_DUEDATE, due_date);
                        map.put(TAG_DUEAMOUNT, due_amount);

                        Log.d("Status",acc_status);

                        // adding HashList to ArrayList
                        profileList1.add(map);
                    }
                }
                else if (success.equals("0")) {

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // dismiss the dialog once got all details
            System.out.print(result);
            pDialog1.dismiss();

            for (HashMap<String, String> map : profileList1) {
                final String firstname = map.get(TAG_FIRSTNAME1);
                final String lastname = map.get(TAG_LASTNAME1);
                srvname = map.get(TAG_SRVNAME1);
                final String used_data = map.get(TAG_USEDDATA1);
                final String remaining_data = map.get(TAG_REMAININGDATA1);
                final String total_data = map.get(TAG_TOTALDATA1);
                final String mobile = map.get(TAG_MOBILE1);
                final String email = map.get(TAG_EMAIL1);
                final String flag_faast_prime = map.get(TAG_FLAG_FAAST_PRIME);
                faast_prime_member_status = map.get(TAG_FAAST_PRIME_STATUS);
                faast_prime_member_request = map.get(TAG_FAAST_PRIME_REQUEST);
                accountStatus = map.get(TAG_STATUS1);
                accountType = map.get(TAG_ACC_TYPE);

                dd = map.get(TAG_DUEDATE);
                dm = map.get(TAG_DUEAMOUNT);

                if (accountType.equals("inactive")){
                    Intent i = new Intent(HomeInternetStatus.this, LogOut.class);
                    startActivity(i);
                }

                else{
                    if(dd.equals("Immediate") || dd.equals("PAID"))
                    {
                        due_date_textview.setText(dd);
                    }
                    else
                    {
                        String duedate1 = getconvertdate1(dd);
                        due_date_textview.setText(duedate1);
                    }
                    outstanding_amount = Double.valueOf(dm).doubleValue();
                    NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
                    String moneyString = formatter.format(outstanding_amount);
                    due_amount_textview.setText(moneyString);
                    System.out.println(dd);
                    System.out.println(dm);

                    //graph_value.setText(remaining_data + " GB \n left of \n" + total_data + "GB");


                    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                    navigationView.setNavigationItemSelectedListener(HomeInternetStatus.this);

                    View header = navigationView.getHeaderView(0);
                    nav_username = (TextView) header.findViewById(R.id.nav_bar_email);
                    nav_username.setText("    Hello! " + firstname);
                    if(!flag_faast_prime.equals("1"))
                    {
                        MenuItem item = navigationView.getMenu().getItem(1);
                        item.setVisible(false);
                    }

                    if(faast_prime_member_status.equals("1")){
                        getSupportActionBar().setTitle("Welcome " + firstname + " (Prime Member)");
                    }
                    else{
                        getSupportActionBar().setTitle("Welcome " + firstname );
                    }
                    Double ud2 = Double.parseDouble(used_data);
                    Double td2 = Double.parseDouble(total_data);
                    Integer i;
                    Double graphv;
                    if (td2 == 0.00 && ud2 != 0.00) {
                        graphv = 779.00;
                        graph_value.setText("Consumed Data\n" + used_data + "GB");
                    } else {
                        graphv = (ud2 / td2) * 800;
                        graph_value.setText(remaining_data + " GB \n left of \n" + total_data + "GB");
                    }

                    i = graphv.intValue();
                    System.out.println(i);
                    gauge1.setValue(i);
                    srvname_textview.setText(srvname);
                    System.out.println(srvname);

                    SharedPreferences myPrefs1 = getApplicationContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = myPrefs1.edit();
                    editor1.putString("firstname", firstname);
                    editor1.putString("lastname", lastname);
                    editor1.putString("srvname", srvname);
                    editor1.putString("used_data", used_data);
                    editor1.putString("remaining_data", remaining_data);
                    editor1.putString("total_data", total_data);
                    editor1.putString("mobile", mobile);
                    editor1.putString("faastPrimeMembershipStatus", faast_prime_member_status);
                    editor1.putString("email", email);

                    editor1.commit();

                    if (message!=null){
                        BuilderNotification = new AlertDialog.Builder(HomeInternetStatus.this, R.style.MyDialogTheme);

                        //Setting message manually and performing action on button click
                        BuilderNotification.setMessage(message)
                                .setCancelable(false)
                                .setTitle("FAAST")
                                .setIcon(R.mipmap.arrow_white)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        if(accountStatus.equals("0")){
                                            AlertDialog.Builder AccountStatusAlertBox = new AlertDialog.Builder(HomeInternetStatus.this, R.style.MyDialogTheme);

                                            //Setting message manually and performing action on button click
                                            AccountStatusAlertBox.setMessage("Your account has been disabled due to non payment.")
                                                    .setCancelable(false)
                                                    .setTitle("FAAST")
                                                    .setIcon(R.mipmap.arrow_white)
                                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            dialog.cancel();
                                                            GetVersionCode getVersionCode=new GetVersionCode();
                                                            getVersionCode.execute();
                                                        }
                                                    });
                                            //Creating dialog box
                                            AlertDialog AccountStatusAlert = AccountStatusAlertBox.create();
                                            AccountStatusAlert.show();
                                        }
                                    }
                                });
                        //Creating dialog box
                        NotificationAlertBox = BuilderNotification.create();
                        NotificationAlertBox.show();

                    }
                    else{
                        if(accountStatus.equals("0")){
                            AlertDialog.Builder AccountStatusAlertBox = new AlertDialog.Builder(HomeInternetStatus.this, R.style.MyDialogTheme);

                            //Setting message manually and performing action on button click
                            AccountStatusAlertBox.setMessage("Your account has been disabled due to non payment.")
                                    .setCancelable(false)
                                    .setTitle("FAAST")
                                    .setIcon(R.mipmap.arrow_white)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            GetVersionCode getVersionCode=new GetVersionCode();
                                            getVersionCode.execute();
                                        }
                                    });
                            //Creating dialog box
                            AlertDialog AccountStatusAlert = AccountStatusAlertBox.create();
                            AccountStatusAlert.show();
                        }
                        else{
                            GetVersionCode getVersionCode=new GetVersionCode();
                            getVersionCode.execute();
                        }
                    }
                }

            }
        }
    }


    //-------------------showing alert for due amount-----------------------------------------

    public void alertForDueAmount()
    {
        if(dm.equals("0.0"))
        {

        }
        else
        {
            AlertDialog.Builder AccountStatusAlertBox = new AlertDialog.Builder(HomeInternetStatus.this, R.style.MyDialogTheme);

            //Setting message manually and performing action on button click
            AccountStatusAlertBox.setMessage("Unpaid bill found click to pay ")
                    .setCancelable(false)
                    .setTitle("FAAST")
                    .setIcon(R.mipmap.arrow_white)
                    .setNegativeButton("cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();

                                }
                            })
                    .setPositiveButton("Pay", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            Checkout.preload(getApplicationContext());
                            Total_amount = Double.parseDouble(dm);
                            Toast.makeText(HomeInternetStatus.this,"Processing Payment, Please wait…",Toast.LENGTH_SHORT).show();
                            startPayment(Total_amount);
                        }
                    });
            //Creating dialog box
            AlertDialog AccountStatusAlert = AccountStatusAlertBox.create();
            AccountStatusAlert.show();
        }
    }

    //-----------------------------CUSTOM ADAPTER FOR HOME MENUS GRIDS----------------------------------

    public class CustomAdapter extends BaseAdapter {

        String[] result;
        Context context;
        int [] imageId;
        private LayoutInflater inflater=null;
        public CustomAdapter(HomeInternetStatus mainActivity, String[] prgmNameList, int[] prgmImages) {
            // TODO Auto-generated constructor stub
            result = prgmNameList;
            context = mainActivity;
            imageId = prgmImages;
            inflater = (LayoutInflater)context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return result.length;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public class Holder
        {
            TextView tv;
            ImageView img;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            CustomAdapter.Holder holder=new CustomAdapter.Holder();
            View rowView;

            rowView = inflater.inflate(R.layout.grids_home, null);
            holder.tv = (TextView) rowView.findViewById(R.id.textView1);
            holder.img = (ImageView) rowView.findViewById(R.id.imageView1);

            holder.tv.setText(result[position]);
            holder.img.setImageResource(imageId[position]);
            rowView.setTag(holder);

            rowView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    // Toast.makeText(context, "You Clicked "+result[position], Toast.LENGTH_LONG).show();
                    if(position == 0)
                    {
                        if(dm.equals("0.0"))
                        {
                            Toast.makeText(getApplicationContext(),"No Outstanding Amount",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Checkout.preload(getApplicationContext());
                            Total_amount = Double.parseDouble(dm);
                            Toast.makeText(HomeInternetStatus.this,"Processing Payment, Please wait…",Toast.LENGTH_SHORT).show();
                            startPayment(Total_amount);
                        }
                    }
                    else if(position == 1)
                    {
                        if(dm.equals("0.0"))
                        {
                            Toast.makeText(getApplicationContext(),"No Outstanding Amount",Toast.LENGTH_SHORT).show();
                        }

                        else {
                            Intent intent = new Intent(HomeInternetStatus.this, Invoices.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        }
                    }
                    else if(position == 2)
                    {
                        Intent intent = new Intent(HomeInternetStatus.this,UsageReportTabLayout.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                    else if(position == 3)
                    {
                        if(srvname.toLowerCase().contains("daily".toLowerCase())){

                            Toast toast = Toast.makeText(getApplicationContext(),"Sorry, TopUps are currently not allowed for DAILY plans.", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();                        }
                        else{
                            Intent intent = new Intent(HomeInternetStatus.this, Topups.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        }

                    }
                    else if(position == 4)
                    {
                        Intent intent = new Intent(context,ChangePassword.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                    else if(position == 5)
                    {

                        getNonVerifiedTickets(UserName);
                        //Intent intent = new Intent(context,SupportTickets.class);
//                        startActivity(intent);
//                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                }
            });
            return rowView;
        }
    }

    //----------------------Get non verified status---------------
    private void getNonVerifiedTickets(final String username) {

        class NonVerifiedTickets extends AsyncTask<String, Void, String> {

            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
//                loadingDialog = ProgressDialog.show(Login.this, "Please wait", "Loading...");
                loadingDialog = createProgressDialog(HomeInternetStatus.this);
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
//                            "http://10.0.2.2/android_faast_db/login.php");
                    HttpPost httpPost = new HttpPost(
                            getNonVerifiedTicketsURL);
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
                Log.e("No of tickets",s);
                if (s.equalsIgnoreCase("success")) {
                    Intent i = new Intent(HomeInternetStatus.this, SupportTicketsTable.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.enter, R.anim.exit);

                } else {
                    Intent i = new Intent(HomeInternetStatus.this, SupportTickets.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }
            }
        }

        NonVerifiedTickets la = new NonVerifiedTickets();
        la.execute(username);
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
                                                    Intent i=new Intent(HomeInternetStatus.this,HomeInternetStatus.class);
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

    @Override
    public void onResume() {
        super.onResume();
        this.registerReceiver(this.mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        MyApplication.activityResumed();// On Resume notify the Application

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
    public void onPause() {
        super.onPause();
        this.unregisterReceiver(mConnReceiver);
            MyApplication.activityPaused();
        if(isApplicationSentToBackground(this))
        {
           finishAffinity();
        }
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

    /*Get version of the app from the playstores*/
    public class GetVersionCode extends AsyncTask<Void, String, String> {
        @Override
        protected String doInBackground(Void... voids) {

            String newVersion = null;
            try {
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + HomeInternetStatus.this.getPackageName() + "&hl=it")
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div[itemprop=softwareVersion]")
                        .first()
                        .ownText();
                return newVersion;
            } catch (Exception e) {
                return newVersion;
            }
        }

        @Override
        protected void onPostExecute(String onlineVersion) {
            super.onPostExecute(onlineVersion);
            String appVersion = BuildConfig.VERSION_NAME;
            if (onlineVersion != null && !onlineVersion.isEmpty()) {

                if (Float.valueOf(appVersion) < Float.valueOf(onlineVersion)) {
                    //show dialog
                    AlertDialog.Builder builderUpdate = new AlertDialog.Builder(HomeInternetStatus.this, R.style.MyDialogTheme);

                    //Setting message manually and performing action on button click
                    builderUpdate.setMessage("New version available in the playstore, please update the app.");
                    builderUpdate.setCancelable(false);
                    builderUpdate.setIcon(R.mipmap.arrow_white);
                    builderUpdate.setTitle("FAAST");

                    builderUpdate.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            finishAffinity();
                            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.faast.mobile.apps&hl=en");
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }
                    });
                    //Creating dialog box
                    alertUpdate = builderUpdate.create();
                    alertUpdate.show();

                    alertUpdate.setOnKeyListener(new DialogInterface.OnKeyListener() {

                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                            if(keyCode == KeyEvent.KEYCODE_BACK){
                                alertUpdate.dismiss();
                                finishAffinity();
                            }
                            return false;
                        }
                    });
                }
                else {
                    alertForDueAmount();
                }
            }
            System.out.println("update "+ "Current version " + appVersion + "playstore version " + onlineVersion);
        }
    }
}


//package com.faast.mobile.apps;
//
//import android.app.Activity;
//import android.app.ActivityManager;
//import android.app.Dialog;
//import android.app.NotificationManager;
//import android.app.ProgressDialog;
//import android.content.BroadcastReceiver;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.SharedPreferences;
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.design.widget.NavigationView;
//import android.support.v4.view.GravityCompat;
//import android.support.v4.view.ViewPager;
//import android.support.v4.widget.DrawerLayout;
//import android.support.v7.app.ActionBarDrawerToggle;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.view.WindowManager;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
//import android.widget.BaseAdapter;
//import android.widget.GridView;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//import android.widget.ViewFlipper;
//
//import com.android.volley.Request;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonArrayRequest;
//import com.android.volley.toolbox.Volley;
//import com.koushikdutta.ion.Ion;
//import com.razorpay.Checkout;
//
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.message.BasicNameValuePair;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.jsoup.Jsoup;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.UnsupportedEncodingException;
//import java.text.DateFormat;
//import java.text.NumberFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Locale;
//import java.util.TimeZone;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import pl.pawelkleczkowski.customgauge.CustomGauge;
//
//public class HomeInternetStatus extends AppCompatActivity
//        implements NavigationView.OnNavigationItemSelectedListener {
//    private CustomGauge gauge1;
//    static public String dd="";
//    static public String dm="";
//    GridView gv;
//    public static String[] menu_list = {"Payment", "Unpaid Invoices", "Usage", "Topups", "Password", "Support"};
//    public static int[] menu_images = {R.mipmap.online_payment1, R.mipmap.payment, R.mipmap.usage_report, R.mipmap.topups_green, R.mipmap.change_password2, R.mipmap.support11};
//    TextView srvname_textview, due_date_textview, due_amount_textview, graph_value;
//    String FirstName, mobile, UserName;
//    private Activity mActivity;
//    private static final String TAG_SUCCESS = "success";
//    private static final String TAG_PRODUCTS = "fixture";
//    public Double outstanding_amount;
//    private static final String TAG_DUEAMOUNT = "due_amount";
//    private static final String TAG_DUEDATE = "due_date";
//
//    private ProgressDialog pDialog;
//    JSONParser jsonParser= new JSONParser();
//    ArrayList<HashMap<String, String>> profileList = new ArrayList<HashMap<String, String>>();
//    JSONArray matchFixture = null;
//    String faast_prime_member_status;
//
//    private static final String TAG_SUCCESS1= "success";
//    private static final String TAG_PRODUCTS1= "fixture";
//    private static final String TAG_FIRSTNAME1 = "firstname";
//    private static final String TAG_LASTNAME1 = "lastname";
//    private static final String TAG_SRVNAME1 = "srvname";
//    private static final String TAG_USEDDATA1 = "used_data";
//    private static final String TAG_REMAININGDATA1 = "remaining_data";
//    private static final String TAG_TOTALDATA1 = "total_data";
//    private static final String TAG_EMAIL1 = "email";
//    private static final String TAG_MOBILE1= "mobile";
//    private static final String TAG_STATUS1= "account_status";
//    private static final String TAG_FLAG_FAAST_PRIME= "flag_for_faast_prime";
//    private static final String TAG_FAAST_PRIME_STATUS= "faast_prime_member";
//
//
//    private ProgressDialog pDialog1;
//    JSONParser jsonParser1 = new JSONParser();
//    ArrayList<HashMap<String, String>> profileList1 = new ArrayList<HashMap<String, String>>();
//    JSONArray matchFixture1 = null;
//    String dueAmountURL;
//    String homeUserDataURL;
//    String updateInvoiceURL;
//    String getNonVerifiedTicketsURL;
//    String rzChargeURL;
//    String imageSliderURL;
//    Double ssizedouble;
//
//    final String your_key_id = "rzp_live_yjf2wTSV05B9Fq";
//    final String key_secret = "hbjVhO5y7rbpapfk5cAGEtEJ";
//
//    //  final String your_key_id = "rzp_test_uDpwVNvR5hvR9N";
////    final String key_secret = "aYyJ1mipvXYX0RVfYKX2LCVH";
//    private NotificationManager mNotificationManager;
//
//    Double Total_amount;
//    Integer Final_Amount;
//    String s;
//    String razorpayPaymentID1;
//    Integer amt1;
//    Integer i,i1,uv,uv1;
//    String FirstNameP,emailP,mobileP;
//    String Screensinch;
//
//    TextView current_plan_lable,due_amount_lable,due_date_lable;
//
//    //Notification Variables
//    BroadcastReceiver mRegistrationBroadcastReceiver;
//    String token;
//
//    //Navigation Bar
//    TextView nav_username;
//
//    //----------navigation drawer variables
//    ImageView nav_back_button;
//    NavigationView navigationView;
//    View header;
//    DrawerLayout drawer;
//    ActionBarDrawerToggle toggle;
//    String message;
//    AlertDialog alertUpdate;
//    AlertDialog.Builder BuilderNotification;
//    AlertDialog NotificationAlertBox;
//
//    String accountStatus;
//    boolean internetStatus;
//    String value,updateinv;
//
////    public static final String BROADCAST = "com.faast.mobile.apps.android.action.mConnReceiver";
//
//    ViewPageAdapter viewPageAdapter;
//    ViewPager viewPager;
//
//    com.android.volley.RequestQueue rq;
//    List<SliderUtils> sliderImg;
//    String requesrUrl = "http://10.0.2.2:81/android_faast_db/get_images.php";
//
//    private int index1 = 0;
//    private ViewFlipper mViewFlipper1;
//    com.android.volley.RequestQueue rq1;
//    List<String> sliderImg1;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_home_new);
//
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00ba30")));
//        getSupportActionBar().setTitle("Welcome " + FirstName);
//
//        Window window = HomeInternetStatus.this.getWindow();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            window.setStatusBarColor(HomeInternetStatus.this.getResources().getColor(R.color.my_statusbar_color));
//        }
//
//        mActivity = HomeInternetStatus.this;
//        mNotificationManager=(NotificationManager) this.getApplicationContext().getSystemService(this.NOTIFICATION_SERVICE);
//
////        ViewFlipper flipper=(ViewFlipper)findViewById(R.id.viewFlipper);
////        final Animation animFlipInNext = AnimationUtils.loadAnimation(this, R.anim.flipinnext);
////        final Animation animFlipOutNext = AnimationUtils.loadAnimation(this, R.anim.flipoutnext);
////
////        flipper.setInAnimation(animFlipInNext);
////        flipper.setOutAnimation(animFlipOutNext);
////        flipper.showNext();
////        flipper.startFlipping();
//
//        SharedPreferences myPrefs = this.getSharedPreferences("contacts", MODE_PRIVATE);
//        SharedPreferences myPrefs1 = this.getSharedPreferences("UserDetails", MODE_PRIVATE);
//
//        UserName = myPrefs.getString("Username", "");
//        SharedPreferences Links = getApplicationContext().getSharedPreferences("DatabaseLinks", MODE_PRIVATE);
//        homeUserDataURL = Links.getString("homeuserdata","");
//        dueAmountURL = Links.getString("homeduedata","");
//        rzChargeURL = Links.getString("rzcharge","");
//        updateInvoiceURL = Links.getString("updateinvoice","");
//        imageSliderURL = Links.getString("imagesliderurl","");
//        getNonVerifiedTicketsURL = Links.getString("getnonverifiedticket","");
//
////        rq = Volley.newRequestQueue(HomeInternetStatus.this);
////        sliderImg = new ArrayList<>();
////
////        viewPager = (ViewPager) findViewById(R.id.viewPager);
////        sendRequest();
//
////        Timer timer = new Timer();
////        timer.scheduleAtFixedRate(new MyTimerTask(), 3000,5000);
//
//
//        SharedPreferences ScreenSizes = getApplicationContext().getSharedPreferences("ScreenSize", MODE_PRIVATE);
//        Screensinch = ScreenSizes.getString("screens","");
//        ssizedouble = Double.parseDouble(Screensinch);
//
//        Intent i = getIntent();
//        message = i.getStringExtra("Message");
//
//        gauge1 = (CustomGauge) findViewById(R.id.gauge2);
//        graph_value = (TextView) findViewById(R.id.graph_value);
//        srvname_textview = (TextView) findViewById(R.id.current_plan_value);
//        due_amount_textview = (TextView) findViewById(R.id.total_payment_due);
//        due_date_textview = (TextView) findViewById(R.id.payment_due_date);
//        current_plan_lable = (TextView) findViewById(R.id.current_plan);
//        due_amount_lable = (TextView) findViewById(R.id.total_payment_lable);
//        due_date_lable = (TextView) findViewById(R.id.due_date_textview);
//
//        gv = (GridView) findViewById(R.id.gridView1);
//        gv.setAdapter(new CustomAdapter(HomeInternetStatus.this, menu_list, menu_images));
//
//        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
//        if (networkInfo != null && networkInfo.isConnected()) {
//            changeTextStatus(true);
//        } else {
//            changeTextStatus(false);
//        }
//
//        rq1 = Volley.newRequestQueue(HomeInternetStatus.this);
//        sliderImg1 = new ArrayList<>();
//
//        ImageView image = getNewImageView1();
////        image.setImageResource(R.mipmap.loader);
//        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
//
//        final Animation animFlipInNext = AnimationUtils.loadAnimation(this, R.anim.flipinnext);
//        final Animation animFlipOutNext = AnimationUtils.loadAnimation(this, R.anim.flipoutnext);
//
//        mViewFlipper1 = (ViewFlipper) findViewById(R.id.viewFlipper);
//        mViewFlipper1.setInAnimation(animFlipInNext);
//        mViewFlipper1.setOutAnimation(animFlipOutNext);
//
//        mViewFlipper1.addView(image);
//        sendRequest1();
//
//        Timer timer1 = new Timer();
//        timer1.scheduleAtFixedRate(new MyTimerTask1(), 3000,5000);
//
//        navigationView = (NavigationView) findViewById(R.id.nav_view);
//
//        navigationView.setNavigationItemSelectedListener(this);
//
//        header = navigationView.getHeaderView(0);
////        nav_username=(TextView) header.findViewById(R.id.nav_bar_username);
////        nav_username.setText(UserName);
//
//        nav_back_button =(ImageView) header.findViewById(R.id.nav_back_arrow);
//        nav_back_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//                drawer.closeDrawer(GravityCompat.START);
//            }
//        });
//
//
//        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();
//
//    }
//
//    public class MyTimerTask1 extends TimerTask {
//
//        @Override
//        public void run() {
//            HomeInternetStatus.this.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//
//                    ImageView imageView1 = getNewImageView1();
//                    imageView1.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                    // Where we will place the image
//                    Ion.with(imageView1)
////                            .placeholder(R.mipmap.loader)
//                            .load(getNextImage1());
//
//                    mViewFlipper1.addView(imageView1); // Adding the image to the flipper
//                    mViewFlipper1.showNext();
//                }
//            });
//        }
//    }
//
//
//    public void sendRequest1(){
//
//        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, imageSliderURL, null, new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response1) {
//                for(int i=0;i<response1.length();i++){
//                    SliderUtils slideUtils1 = new SliderUtils();
//
//                    try {
//                        JSONObject jsonObject = response1.getJSONObject(i);
//                        slideUtils1.setSliderImageUrl(jsonObject.getString("image_url"));
//                        sliderImg1.add(i,jsonObject.getString("image_url"));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//                Log.e("URL---", String.valueOf(response1));
//                Log.e("URL---", String.valueOf(sliderImg1));
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//            }
//        }
//        );
//        rq1.add(jsonArrayRequest);
//    }
//
//
//    protected ImageView getNewImageView1() {
//        ImageView image1 = new ImageView(getApplicationContext());
//        image1.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//        return image1;
//    }
//
//    protected String getNextImage1() {
////        if (index == sliderImg.size())
////            index = 0;
////        Log.e("Image====", sliderImg.get(index++).toString());
//        Log.e("ENtered", String.valueOf(index1));
//        Log.e("ENtered",sliderImg1.get(index1));
//        if(index1 == 0){
//            index1 = 1;
//            return String.valueOf(sliderImg1.get(1));
//        }
//        else if(index1 == 1){
//            index1 = 2;
//            return String.valueOf(sliderImg1.get(2));
//        }
//        else if(index1 == 2){
//            index1 = 3;
//            return String.valueOf(sliderImg1.get(3));
//        }
//        else{
//            index1 = 0;
//            return String.valueOf(sliderImg1.get(0));
//        }
//    }
//
//
//    public void changeTextStatus(boolean isConnected) {
//
//        // Change status according to boolean value
//        if (isConnected) {
//            new LoadProfile().execute();
//            System.out.println("Connected");
//        } else {
////            Toast.makeText(getApplicationContext(),"disConnected",Toast.LENGTH_SHORT).show();
//            System.out.println("Disconnected");
//
//            AlertDialog.Builder builder111 = new AlertDialog.Builder(HomeInternetStatus.this);
//            // Set the Alert Dialog Message
//            builder111.setMessage("Internet Connection Required")
//                    .setCancelable(false)
//                    .setPositiveButton("Retry",
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog,
//                                                    int id) {
//                                    // Restart the Activity
//                                    dialog.cancel();
////                                    mActivity.startActivity(mActivity.getIntent());
//                                    Intent intent = getIntent();
//                                    overridePendingTransition(0, 0);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                                    finish();
//                                    overridePendingTransition(0, 0);
//                                    startActivity(intent);
//
//                                }
//                            });
//            AlertDialog alert = builder111.create();
//            alert.show();
//        }
//    }
//
//    public class MyTimerTask extends TimerTask{
//
//        @Override
//        public void run() {
//            HomeInternetStatus.this.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if(viewPager.getCurrentItem() == 0){
//                        viewPager.setCurrentItem(1);
//                    }
//
//                    else if(viewPager.getCurrentItem() == 1){
//                        viewPager.setCurrentItem(2);
//                    }
//
//                    else if(viewPager.getCurrentItem() == 2){
//                        viewPager.setCurrentItem(3);
//                    }
//
//                    else{
//                        viewPager.setCurrentItem(0);
//                    }
//                }
//            });
//        }
//    }
//
//
//    public void sendRequest(){
//
//        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, requesrUrl, null, new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//                for(int i=0;i<response.length();i++){
//                    SliderUtils slideUtils = new SliderUtils();
//
//                    try {
//                        JSONObject jsonObject = response.getJSONObject(i);
//                        slideUtils.setSliderImageUrl(jsonObject.getString("image_url"));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    sliderImg.add(slideUtils);
//                }
//                Log.e("URL---", String.valueOf(response));
////                System.out.print("URL---"+String.valueOf(slideUtils));
//
//                viewPageAdapter = new ViewPageAdapter(sliderImg,HomeInternetStatus.this);
//                viewPager.setAdapter(viewPageAdapter);
//
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//            }
//        }
//        );
//        rq.add(jsonArrayRequest);
//    }
//
//
//    //-----------------------------VALIDATING FOR BACK BUTTON PRESSED----------------------------------
//
//    boolean doubleBackToExitPressedOnce = false;
//    @Override
//    public void onBackPressed() {
//
//        if(drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        }
//
//        else {
//            if (doubleBackToExitPressedOnce) {
//                finishAffinity();
//                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
//                return;
//            }
//
//            this.doubleBackToExitPressedOnce = true;
//            Toast.makeText(this, "Press once again to exit" +
//                    "", Toast.LENGTH_SHORT).show();
//
//            new Handler().postDelayed(new Runnable() {
//
//                @Override
//                public void run() {
//
//                    doubleBackToExitPressedOnce = false;
//                }
//            }, 2000);
//        }
//    }
//
//    //-------------Navigation Drawer----------------------
//
//    @SuppressWarnings("StatementWithEmptyBody")
//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//        if (id == R.id.home_menu) {
//            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//            drawer.closeDrawer(GravityCompat.START);
//        }else if (id == R.id.faast_prime_menu) {
//            Intent faastprime = new Intent(this,FaastPrime.class);
//            faastprime.putExtra("faastprime_member_status",faast_prime_member_status);
//            startActivity(faastprime);
//            overridePendingTransition(R.anim.enter, R.anim.exit);
//        }else if (id == R.id.payment_hisory_menu) {
//            Intent paymenthistory = new Intent(this,PaymentHistory.class);
//            startActivity(paymenthistory);
//            overridePendingTransition(R.anim.enter, R.anim.exit);
//        } else if (id == R.id.upgrade_plan_menu) {
//            Intent planChange = new Intent(this,PlanChange.class);
//            startActivity(planChange);
//            overridePendingTransition(R.anim.enter, R.anim.exit);
//        } else if (id == R.id.contact_us_menu) {
//            Intent paymenthistory = new Intent(this,ContactUs.class);
//            startActivity(paymenthistory);
//            overridePendingTransition(R.anim.enter, R.anim.exit);
//        } else if (id == R.id.version_menu) {
//            Intent version = new Intent(this,AppVersion.class);
//            startActivity(version);
//            overridePendingTransition(R.anim.enter, R.anim.exit);
//        }
//        else if (id == R.id.offers_menu) {
//            Intent offers = new Intent(this, Offers.class);
//            startActivity(offers);
//            overridePendingTransition(R.anim.enter, R.anim.exit);
//        }
//        else if (id == R.id.logout) {
//            Intent offers = new Intent(this, LogOut.class);
//            startActivity(offers);
//            overridePendingTransition(R.anim.enter, R.anim.exit);
//        }
////        else if (id == R.id.support_menu) {
////            Intent offers = new Intent(this, SupportTickets.class);
////            startActivity(offers);
////            overridePendingTransition(R.anim.enter, R.anim.exit);
////        }
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }
//    //-----------------------------BROADCAST RECEIVER----------------------------------
//
//    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
//
//        public void onReceive(Context context, Intent intent) {
//
//            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
//            String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
//            boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);
//
//            NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
//            NetworkInfo otherNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);
//
//            if (currentNetworkInfo.isConnected()) {
//
//            }
//            else {
//                AlertDialog.Builder builder = new AlertDialog.Builder(HomeInternetStatus.this);
//                // Set the Alert Dialog Message
//                builder.setMessage("Internet Connection Required")
//                        .setCancelable(false)
//                        .setPositiveButton("Retry",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog,
//                                                        int id) {
//                                        // Restart the Activity
//                                        dialog.cancel();
//                                        registerReceiver(mConnReceiver,
//                                                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
//
//                                    }
//                                });
//                AlertDialog alert = builder.create();
//                alert.show();
//            }
//        }
//    };
//
//    //-----------------------------DUE DETAILS ----------------------------------
//
//    class LoadProfile extends AsyncTask<String, String, String> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            pDialog1 = createProgressDialog(HomeInternetStatus.this);
//
//            pDialog1.show();
//        }
//
//        @Override
//        protected String doInBackground(String... args) {
//            // TODO Auto-generated method stub
//            String success;
//
//            try {
//                Log.d("try", "in the try");
//                List<NameValuePair> params = new ArrayList<NameValuePair>();
//
//                params.add(new BasicNameValuePair("username", UserName));
//                JSONObject json = jsonParser.makeHttpRequest(
//                        dueAmountURL, "POST", params);
//                Log.d("jsonObject", "new json Object");
//
//                matchFixture = json.getJSONArray(TAG_PRODUCTS);
//                JSONObject c1 = matchFixture.getJSONObject(0);
//                success = c1.getString(TAG_SUCCESS);
//
//                if (success.equals("1")) {
//
//                    for (int i = 0; i < matchFixture.length(); i++) {
//                        JSONObject c = matchFixture.getJSONObject(i);
//
//                        String due_date = c.getString(TAG_DUEDATE);
//                        String due_amount = c.getString(TAG_DUEAMOUNT);
//
//                        HashMap<String, String> map = new HashMap<String, String>();
//                        map.put(TAG_DUEDATE, due_date);
//                        map.put(TAG_DUEAMOUNT, due_amount);
//                        profileList.add(map);
//                    }
//                } else if (success.equals("0")) {
//
//                }
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        protected void onPostExecute(String result) {
////            pDialog.dismiss();
//            super.onPostExecute(result);
//            // dismiss the dialog once got all details
//
//            for (HashMap<String, String> map : profileList) {
//                dd = map.get(TAG_DUEDATE);
//                dm = map.get(TAG_DUEAMOUNT);
//
//                if(dd.equals("Immediate") || dd.equals("PAID"))
//                {
//                    due_date_textview.setText(dd);
//                }
//                else
//                {
//                    String duedate1 = getconvertdate1(dd);
//                    due_date_textview.setText(duedate1);
//                }
//                outstanding_amount = Double.valueOf(dm).doubleValue();
//                NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
//                String moneyString = formatter.format(outstanding_amount);
//                due_amount_textview.setText(moneyString);
//                System.out.println(dd);
//                System.out.println(dm);
//                new LoadUserDetails().execute();
//            }
//        }
//    }
//
//    //-----------------------------DATE FORMAT CONVERSION FUNCTION----------------------------------
//
//    public String getconvertdate1(String date)
//    {
//        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
//        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
//        DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
//        Date parsed = new Date();
//        try
//        {
//            parsed = inputFormat.parse(date);
//        }
//        catch (ParseException e)
//        {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        String outputText = outputFormat.format(parsed);
//        return outputText;
//    }
//
//    //-----------------------------CUSTOMER DETAILS ----------------------------------
//
//    class LoadUserDetails extends AsyncTask<String, String, String> {
//
//        boolean failure = false;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected String doInBackground(String... args) {
//            // TODO Auto-generated method stub
//            String success;
//
//            try {
//                Log.d("try", "in the try");
//                List<NameValuePair> params = new ArrayList<NameValuePair>();
//
//                params.add(new BasicNameValuePair("username", UserName));
//                JSONObject json = jsonParser1.makeHttpRequest(
//                        homeUserDataURL, "POST", params);
//                Log.d("jsonObject", "new json Object");
//
//                matchFixture1 = json.getJSONArray(TAG_PRODUCTS1);
//                JSONObject c1 = matchFixture1.getJSONObject(0);
//                success = c1.getString(TAG_SUCCESS1);
//                if (success.equals("1") ) {
//
//                    for (int i = 0; i < matchFixture1.length(); i++) {
//                        System.out.println(matchFixture1);
//
//                        JSONObject c = matchFixture1.getJSONObject(i);
//
//                        String firstname = c.getString(TAG_FIRSTNAME1);
//                        String lastname = c.getString(TAG_LASTNAME1);
//                        String srvname = c.getString(TAG_SRVNAME1);
//                        String used_data = c.getString(TAG_USEDDATA1);
//                        String remaining_data = c.getString(TAG_REMAININGDATA1);
//                        String total_data = c.getString(TAG_TOTALDATA1);
//                        String mobile = c.getString(TAG_MOBILE1);
//                        String email = c.getString(TAG_EMAIL1);
//                        String acc_status = c.getString(TAG_STATUS1);
//                        String flag_faast_prime = c.getString(TAG_FLAG_FAAST_PRIME);
//                        String faast_prime_member_status = c.getString(TAG_FAAST_PRIME_STATUS);
//
//                        HashMap<String, String> map = new HashMap<String, String>();
//
//                        // adding each child node to HashMap key => value
//                        map.put(TAG_FIRSTNAME1, firstname);
//                        map.put(TAG_LASTNAME1, lastname);
//                        map.put(TAG_SRVNAME1, srvname);
//                        map.put(TAG_USEDDATA1, used_data);
//                        map.put(TAG_REMAININGDATA1, remaining_data);
//                        map.put(TAG_TOTALDATA1, total_data);
//                        map.put(TAG_EMAIL1, email);
//                        map.put(TAG_MOBILE1, mobile);
//                        map.put(TAG_STATUS1, acc_status);
//                        map.put(TAG_FLAG_FAAST_PRIME, flag_faast_prime);
//                        map.put(TAG_FAAST_PRIME_STATUS, faast_prime_member_status);
//
//                        Log.d("Status",acc_status);
//
//                        // adding HashList to ArrayList
//                        profileList1.add(map);
//                    }
//                }
//                else if (success.equals("0")) {
//
//                }
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//            // dismiss the dialog once got all details
//            pDialog1.dismiss();
//
//            for (HashMap<String, String> map : profileList1) {
//                final String firstname = map.get(TAG_FIRSTNAME1);
//                final String lastname = map.get(TAG_LASTNAME1);
//                final String srvname = map.get(TAG_SRVNAME1);
//                final String used_data = map.get(TAG_USEDDATA1);
//                final String remaining_data = map.get(TAG_REMAININGDATA1);
//                final String total_data = map.get(TAG_TOTALDATA1);
//                final String mobile = map.get(TAG_MOBILE1);
//                final String email = map.get(TAG_EMAIL1);
//                final String flag_faast_prime = map.get(TAG_FLAG_FAAST_PRIME);
//                faast_prime_member_status = map.get(TAG_FAAST_PRIME_STATUS);
//                accountStatus = map.get(TAG_STATUS1);
//                //graph_value.setText(remaining_data + " GB \n left of \n" + total_data + "GB");
//
//                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//                navigationView.setNavigationItemSelectedListener(HomeInternetStatus.this);
//
//                View header = navigationView.getHeaderView(0);
//                nav_username = (TextView) header.findViewById(R.id.nav_bar_email);
//                nav_username.setText("    Hello! " + firstname);
//                if(!flag_faast_prime.equals("1"))
//                {
//                    MenuItem item = navigationView.getMenu().getItem(1);
//                    item.setVisible(false);
//                }
//
//                if(faast_prime_member_status.equals("1")){
//                    getSupportActionBar().setTitle("Welcome " + firstname + " (Prime Member)");
//                }
//                else{
//                    getSupportActionBar().setTitle("Welcome " + firstname );
//                }
//                Double ud2 = Double.parseDouble(used_data);
//                Double td2 = Double.parseDouble(total_data);
//                Integer i;
//                Double graphv;
//                if (td2 == 0.00 && ud2 != 0.00) {
//                    graphv = 779.00;
//                    graph_value.setText("Consumed Data\n" + used_data + "GB");
//                } else {
//                    graphv = (ud2 / td2) * 800;
//                    graph_value.setText(remaining_data + " GB \n left of \n" + total_data + "GB");
//                }
//
//                i = graphv.intValue();
//                System.out.println(i);
//                gauge1.setValue(i);
//                srvname_textview.setText(srvname);
//                System.out.println(srvname);
//
//                SharedPreferences myPrefs1 = getApplicationContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
//                SharedPreferences.Editor editor1 = myPrefs1.edit();
//                editor1.putString("firstname", firstname);
//                editor1.putString("lastname", lastname);
//                editor1.putString("srvname", srvname);
//                editor1.putString("used_data", used_data);
//                editor1.putString("remaining_data", remaining_data);
//                editor1.putString("total_data", total_data);
//                editor1.putString("mobile", mobile);
//                editor1.putString("faastPrimeMembershipStatus", faast_prime_member_status);
//                editor1.putString("email", email);
//
//                editor1.commit();
//
//                if (message!=null){
//                    BuilderNotification = new AlertDialog.Builder(HomeInternetStatus.this, R.style.MyDialogTheme);
//
//                    //Setting message manually and performing action on button click
//                    BuilderNotification.setMessage(message)
//                            .setCancelable(false)
//                            .setTitle("FAAST")
//                            .setIcon(R.mipmap.arrow_white)
//                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    dialog.cancel();
//                                    if(accountStatus.equals("0")){
//                                        AlertDialog.Builder AccountStatusAlertBox = new AlertDialog.Builder(HomeInternetStatus.this, R.style.MyDialogTheme);
//
//                                        //Setting message manually and performing action on button click
//                                        AccountStatusAlertBox.setMessage("Your account has been disabled due to non payment.")
//                                                .setCancelable(false)
//                                                .setTitle("FAAST")
//                                                .setIcon(R.mipmap.arrow_white)
//                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                                    public void onClick(DialogInterface dialog, int id) {
//                                                        dialog.cancel();
//                                                        GetVersionCode getVersionCode=new GetVersionCode();
//                                                        getVersionCode.execute();
//                                                    }
//                                                });
//                                        //Creating dialog box
//                                        AlertDialog AccountStatusAlert = AccountStatusAlertBox.create();
//                                        AccountStatusAlert.show();
//                                    }
//                                }
//                            });
//                    //Creating dialog box
//                    NotificationAlertBox = BuilderNotification.create();
//                    NotificationAlertBox.show();
//
//                }
//                else{
//                    if(accountStatus.equals("0")){
//                        AlertDialog.Builder AccountStatusAlertBox = new AlertDialog.Builder(HomeInternetStatus.this, R.style.MyDialogTheme);
//
//                        //Setting message manually and performing action on button click
//                        AccountStatusAlertBox.setMessage("Your account has been disabled due to non payment.")
//                                .setCancelable(false)
//                                .setTitle("FAAST")
//                                .setIcon(R.mipmap.arrow_white)
//                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int id) {
//                                        dialog.cancel();
//                                        GetVersionCode getVersionCode=new GetVersionCode();
//                                        getVersionCode.execute();
//                                    }
//                                });
//                        //Creating dialog box
//                        AlertDialog AccountStatusAlert = AccountStatusAlertBox.create();
//                        AccountStatusAlert.show();
//                    }
//                    else{
//                        GetVersionCode getVersionCode=new GetVersionCode();
//                        getVersionCode.execute();
//                    }
//                }
//            }
//        }
//    }
//
//    //-------------------showing alert for due amount-----------------------------------------
//
//    public void alertForDueAmount()
//    {
//        if(dm.equals("0.0"))
//        {
//
//        }
//        else
//        {
//            AlertDialog.Builder AccountStatusAlertBox = new AlertDialog.Builder(HomeInternetStatus.this, R.style.MyDialogTheme);
//
//            //Setting message manually and performing action on button click
//            AccountStatusAlertBox.setMessage("Unpaid bill found click to pay ")
//                    .setCancelable(false)
//                    .setTitle("FAAST")
//                    .setIcon(R.mipmap.arrow_white)
//                    .setNegativeButton("cancel",
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog,
//                                                    int id) {
//                                    dialog.cancel();
//
//                                }
//                            })
//                    .setPositiveButton("Pay", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            dialog.cancel();
//                            Total_amount = Double.parseDouble(dm);
//                            Toast.makeText(HomeInternetStatus.this,"Processing Payment, Please wait…",Toast.LENGTH_SHORT).show();
//                            startPayment(Total_amount);
//                        }
//                    });
//            //Creating dialog box
//            AlertDialog AccountStatusAlert = AccountStatusAlertBox.create();
//            AccountStatusAlert.show();
//        }
//    }
//
//    //-----------------------------CUSTOM ADAPTER FOR HOME MENUS GRIDS----------------------------------
//
//    public class CustomAdapter extends BaseAdapter {
//
//        String[] result;
//        Context context;
//        int [] imageId;
//        private LayoutInflater inflater=null;
//        public CustomAdapter(HomeInternetStatus mainActivity, String[] prgmNameList, int[] prgmImages) {
//            // TODO Auto-generated constructor stub
//            result = prgmNameList;
//            context = mainActivity;
//            imageId = prgmImages;
//            inflater = (LayoutInflater)context.
//                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        }
//
//        @Override
//        public int getCount() {
//            // TODO Auto-generated method stub
//            return result.length;
//        }
//
//        @Override
//        public Object getItem(int position) {
//            // TODO Auto-generated method stub
//            return position;
//        }
//
//        @Override
//        public long getItemId(int position) {
//            // TODO Auto-generated method stub
//            return position;
//        }
//
//        public class Holder
//        {
//            TextView tv;
//            ImageView img;
//        }
//
//        @Override
//        public View getView(final int position, View convertView, ViewGroup parent) {
//            // TODO Auto-generated method stub
//            CustomAdapter.Holder holder=new CustomAdapter.Holder();
//            View rowView;
//
//            rowView = inflater.inflate(R.layout.grids_home, null);
//            holder.tv = (TextView) rowView.findViewById(R.id.textView1);
//            holder.img = (ImageView) rowView.findViewById(R.id.imageView1);
//
//            holder.tv.setText(result[position]);
//            holder.img.setImageResource(imageId[position]);
//            rowView.setTag(holder);
//
//            rowView.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    // TODO Auto-generated method stub
//                    // Toast.makeText(context, "You Clicked "+result[position], Toast.LENGTH_LONG).show();
//                    if(position == 0)
//                    {
//                        if(dm.equals("0.0"))
//                        {
//                            Toast.makeText(getApplicationContext(),"No Outstanding Amount",Toast.LENGTH_SHORT).show();
//                        }
//                        else
//                        {
//                            Total_amount = Double.parseDouble(dm);
//                            Toast.makeText(HomeInternetStatus.this,"Processing Payment, Please wait…",Toast.LENGTH_SHORT).show();
//                            startPayment(Total_amount);
//                        }
//                    }
//                    else if(position == 1)
//                    {
//                        if(dm.equals("0.0"))
//                        {
//                            Toast.makeText(getApplicationContext(),"No Outstanding Amount",Toast.LENGTH_SHORT).show();
//                        }
//
//                        else {
//                            Intent intent = new Intent(HomeInternetStatus.this, Invoices.class);
//                            startActivity(intent);
//                            overridePendingTransition(R.anim.enter, R.anim.exit);
//                        }
//                    }
//                    else if(position == 2)
//                    {
//                        Intent intent = new Intent(HomeInternetStatus.this,UsageReportTabLayout.class);
//                        startActivity(intent);
//                        overridePendingTransition(R.anim.enter, R.anim.exit);
//                    }
//                    else if(position == 3)
//                    {
//
//                        Intent intent = new Intent(HomeInternetStatus.this, Topups.class);
//                        startActivity(intent);
//                        overridePendingTransition(R.anim.enter, R.anim.exit);
//                    }
//                    else if(position == 4)
//                    {
//                        Intent intent = new Intent(context,ChangePassword.class);
//                        startActivity(intent);
//                        overridePendingTransition(R.anim.enter, R.anim.exit);
//                    }
//                    else if(position == 5)
//                    {
//
//                        getNonVerifiedTickets(UserName);
//                        //Intent intent = new Intent(context,SupportTickets.class);
////                        startActivity(intent);
////                        overridePendingTransition(R.anim.enter, R.anim.exit);
//                    }
//                }
//            });
//            return rowView;
//        }
//    }
//
//    //----------------------Get non verified status---------------
//    private void getNonVerifiedTickets(final String username) {
//
//        class NonVerifiedTickets extends AsyncTask<String, Void, String> {
//
//            private Dialog loadingDialog;
//
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
////                loadingDialog = ProgressDialog.show(Login.this, "Please wait", "Loading...");
//                loadingDialog = createProgressDialog(HomeInternetStatus.this);
//                loadingDialog.show();
//            }
//
//            @Override
//            protected String doInBackground(String... params) {
//                String uname = params[0];
//
//                InputStream is = null;
//                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//                nameValuePairs.add(new BasicNameValuePair("username", uname));
//
//                String result = null;
//
//                try {
//                    HttpClient httpClient = new DefaultHttpClient();
////                    HttpPost httpPost = new HttpPost(
////                            "http://10.0.2.2/android_faast_db/login.php");
//                    HttpPost httpPost = new HttpPost(
//                            getNonVerifiedTicketsURL);
//                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//
//                    HttpResponse response = httpClient.execute(httpPost);
//
//                    HttpEntity entity = response.getEntity();
//
//                    is = entity.getContent();
//
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
//                    StringBuilder sb = new StringBuilder();
//
//                    String line = null;
//                    while ((line = reader.readLine()) != null) {
//                        sb.append(line + "\n");
//                        Log.d("Output=", line);
//                    }
//                    result = sb.toString();
//                } catch (ClientProtocolException e) {
//                    e.printStackTrace();
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                return result;
//            }
//
//            @Override
//            protected void onPostExecute(String result) {
//                loadingDialog.dismiss();
//                String s = result.trim();
//                Log.e("No of tickets",s);
//                if (s.equalsIgnoreCase("success")) {
//                    Intent i = new Intent(HomeInternetStatus.this, SupportTicketsTable.class);
//                    startActivity(i);
//                    overridePendingTransition(R.anim.enter, R.anim.exit);
//
//                } else {
//                    Intent i = new Intent(HomeInternetStatus.this, SupportTickets.class);
//                    startActivity(i);
//                    overridePendingTransition(R.anim.enter, R.anim.exit);
//                }
//            }
//        }
//
//        NonVerifiedTickets la = new NonVerifiedTickets();
//        la.execute(username);
//    }
//
//    //-----------------------------RAZOR PAYMENT ----------------------------------
//
//    public void startPayment(Double Amount){
//        /**
//         * Put your key id generated in Razorpay dashboard here
//         */
//        SharedPreferences myPrefs1 = this.getSharedPreferences("UserDetails",MODE_PRIVATE);
//        FirstNameP = myPrefs1.getString("firstname","");
//        mobileP = myPrefs1.getString("mobile","");
//        emailP = myPrefs1.getString("email","");
//        Checkout razorpayCheckout = new Checkout();
//        razorpayCheckout.setKeyID(your_key_id);
//
//        /**
//         * Image for checkout form can passed as reference to a drawable
//         */
//        razorpayCheckout.setImage(R.mipmap.arrow);
//
//        /**
//         * Reference to current activity
//         */
//
//        //Total_amount = Double.parseDouble(Amount);
//        Double d = new Double(Amount);
//        Long L = Math.round(d);
//        int i = Integer.valueOf(L.intValue());
//        Final_Amount = Integer.valueOf(L.intValue());
//        amt1 = Final_Amount*100;
//
//        Activity activity = this;
//
//        try{
//            JSONObject options = new JSONObject("{" +
//                    "description: '<purchase description>'," +
//                    "currency: 'INR'}"
//            );
//
//            /**
//             * Image for checkout form can also be set from a URL
//             * For this, pass URL inside JSONObject as following:
//             *
//             * options.put("image", "<link to image>");
//             */
//            Integer amt = Final_Amount*100;
//            options.put("amount", amt);
//            options.put("name", FirstNameP);
//            options.put("prefill", new JSONObject("{email: '"+emailP+"',contact: '"+mobileP+"',name: '"+FirstNameP+"'}"));
//            options.put("theme", new JSONObject("{color: '#00ba30'}"));
//
//            razorpayCheckout.open(activity, options);
//
//        } catch(Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * The name of the function has to be
//     *   onPaymentSuccess
//     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
//     */
//    public void onPaymentSuccess(String razorpayPaymentID){
//
//        try {
//
//            s=String.valueOf(amt1);
//            razorpayPaymentID1=razorpayPaymentID;
//            final GetData getdb = new GetData();
//
//            new Thread(new Runnable() {
//                public void run() {
//                    value= getdb.getData(your_key_id,key_secret,s,razorpayPaymentID1,rzChargeURL);
//
//                    System.out.println(value);
//                    i=new Integer(value);
//                    i1=i.intValue();
//
//                    runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            if(i1 == 1)
//                            {
//                                final UpdateInvoice ui = new UpdateInvoice();
//
//
//                                new Thread(new Runnable() {
//                                    public void run() {
//                                        updateinv= ui.updateInvoice(UserName,razorpayPaymentID1,updateInvoiceURL);
//
//                                        System.out.println("upadted invoice:"+updateinv);
//                                        uv=new Integer(value);
//                                        uv1=uv.intValue();
//
//                                        runOnUiThread(new Runnable() {
//
//                                            @Override
//                                            public void run() {
//                                                if(updateinv.equalsIgnoreCase("success"))
//                                                {
//                                                    Toast.makeText(getApplicationContext(), "Payment Successful", Toast.LENGTH_LONG).show();
//                                                    Intent i=new Intent(HomeInternetStatus.this,HomeInternetStatus.class);
//                                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                                    finishAffinity();
//                                                    startActivity(i);
//                                                }
//                                                else
//                                                {
//                                                    Toast.makeText(getApplicationContext(), "Payment Successful", Toast.LENGTH_LONG).show();
//                                                    Intent i=new Intent(getApplicationContext(),HomeInternetStatus.class);
//                                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                                    finishAffinity();
//                                                    startActivity(i);
//                                                }
//                                            }
//                                        });
//
//                                    }
//                                }).start();
//
//                            }
//                            else
//                            {
//                                Toast.makeText(getApplicationContext(), "Payment UnSuccessful", Toast.LENGTH_LONG).show();
//                                Intent i=new Intent(getApplicationContext(),HomeInternetStatus.class);
//                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                finishAffinity();
//                                startActivity(i);
//                            }
//                        }
//                    });
//
//                }
//            }).start();
//        }
//        catch (Exception e){
//            Log.e("com.merchant", e.getMessage(), e);
//        }
//    }
//    /**
//     * The name of the function has to be
//     *   onPaymentError
//     * Wrap your code in try catch, as shown, to ensure that this method runs correctl8q
//     */
//    public void onPaymentError(int code, String response) {
//        try {
//
////            Toast.makeText(this, "Payment failed: " + Integer.toString(code) + " " + response, Toast.LENGTH_SHORT).show();
//            Toast.makeText(this, "Payment Cancelled", Toast.LENGTH_SHORT).show();
//
//        } catch (Exception e) {
//            Log.e("com.merchant", e.getMessage(), e);
//        }
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        this.registerReceiver(this.mConnReceiver,
//                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
//        MyApplication.activityResumed();// On Resume notify the Application
//
//    }
//
//    public boolean isApplicationSentToBackground(final Context context) {
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
//        if (!tasks.isEmpty()) {
//            ComponentName topActivity = tasks.get(0).topActivity;
//            if (!topActivity.getPackageName().equals(context.getPackageName())) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        this.unregisterReceiver(mConnReceiver);
//        MyApplication.activityPaused();
//        if(isApplicationSentToBackground(this))
//        {
//            finishAffinity();
//        }
//    }
//
//    public static ProgressDialog createProgressDialog(Context mContext) {
//        ProgressDialog dialog = new ProgressDialog(mContext);
//        try {
//            dialog.show();
//        } catch (WindowManager.BadTokenException e) {
//
//        }
//        dialog.setCancelable(false);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        dialog.setContentView(R.layout.progress_dialogue);
//        // dialog.setMessage(Message);
//        return dialog;
//    }
//
//    /*Get version of the app from the playstores*/
//    public class GetVersionCode extends AsyncTask<Void, String, String> {
//        @Override
//        protected String doInBackground(Void... voids) {
//
//            String newVersion = null;
//            try {
//                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + HomeInternetStatus.this.getPackageName() + "&hl=it")
//                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
//                        .referrer("http://www.google.com")
//                        .get()
//                        .select("div[itemprop=softwareVersion]")
//                        .first()
//                        .ownText();
//                return newVersion;
//            } catch (Exception e) {
//                return newVersion;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(String onlineVersion) {
//            super.onPostExecute(onlineVersion);
//            String appVersion = BuildConfig.VERSION_NAME;
//            if (onlineVersion != null && !onlineVersion.isEmpty()) {
//
//                if (Float.valueOf(appVersion) < Float.valueOf(onlineVersion)) {
//                    //show dialog
//                    AlertDialog.Builder builderUpdate = new AlertDialog.Builder(HomeInternetStatus.this, R.style.MyDialogTheme);
//
//                    //Setting message manually and performing action on button click
//                    builderUpdate.setMessage("New version available in the playstore, please update the app.");
//                    builderUpdate.setCancelable(false);
//                    builderUpdate.setIcon(R.mipmap.arrow_white);
//                    builderUpdate.setTitle("FAAST");
//
//                    builderUpdate.setPositiveButton("Update", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            finishAffinity();
//                            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.faast.mobile.apps&hl=en");
//                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                            startActivity(intent);
//                        }
//                    });
//                    //Creating dialog box
//                    alertUpdate = builderUpdate.create();
//                    alertUpdate.show();
//
//                    alertUpdate.setOnKeyListener(new DialogInterface.OnKeyListener() {
//
//                        @Override
//                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//
//                            if(keyCode == KeyEvent.KEYCODE_BACK){
//                                finishAffinity();
//                            }
//                            return false;
//                        }
//                    });
//                }
//                else {
//                    alertForDueAmount();
//                }
//            }
//            System.out.println("update "+ "Current version " + appVersion + "playstore version " + onlineVersion);
//        }
//    }
//}
