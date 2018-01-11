package com.faast.mobile.apps.faastwifi;

import android.app.Activity;
import android.app.ActivityManager;
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

import com.faast.mobile.apps.BuildConfig;
import com.faast.mobile.apps.GetData;
import com.faast.mobile.apps.JSONParser;
import com.faast.mobile.apps.MyApplication;
import com.faast.mobile.apps.R;
import com.faast.mobile.apps.UpdateInvoice;
import com.razorpay.Checkout;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import pl.pawelkleczkowski.customgauge.CustomGauge;


public class HotspotHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private CustomGauge gauge1;
    static public String dd="";
    static public String dm="";
    GridView gv;
    public static String[] menu_list = { "Usage", "Topups", "Logout"};
    public static int[] menu_images = {R.mipmap.usage_report, R.mipmap.topups_green, R.mipmap.logout1};
    TextView srvname_textview, due_date_textview, graph_value;
    String FirstName, mobile, UserName;
    private Activity mActivity;
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "fixture";

    private static final String TAG_DUEAMOUNT = "due_amount";
    private static final String TAG_DUEDATE = "due_date";

    private ProgressDialog pDialog;
    JSONParser jsonParser= new JSONParser();
    ArrayList<HashMap<String, String>> profileList = new ArrayList<HashMap<String, String>>();
    JSONArray matchFixture = null;

    private static final String TAG_SUCCESS1= "success";
    private static final String TAG_PRODUCTS1= "fixture";
    private static final String TAG_FIRSTNAME1 = "firstname";
    private static final String TAG_LASTNAME1 = "lastname";
    private static final String TAG_SRVNAME1 = "srvname";
    private static final String TAG_REMAINING_DATA_DIGIT = "remaining_data_digit";
    private static final String TAG_REMAINING_DATA_GRAPH = "remaining_data_graph";
    private static final String TAG_TOTAL_DATA_DIGIT = "total_data_digit";
    private static final String TAG_TOTAL_DATA_GRAPH = "total_data_graph";
    private static final String TAG_USED_DATA_MB = "used_data_digit_mb";

    private static final String TAG_EMAIL1 = "email";
    private static final String TAG_MOBILE1= "mobile";
    private static final String TAG_STATUS1= "account_status";
    private static final String TAG_VALID_FROM= "valid_from";
    private static final String TAG_VALID_UPTO= "valid_upto";

    private ProgressDialog pDialog1;
    JSONParser jsonParser1 = new JSONParser();
    ArrayList<HashMap<String, String>> profileList1 = new ArrayList<HashMap<String, String>>();
    JSONArray matchFixture1 = null;
    String dueAmountURL;
    String homeUserDataURL;
    String updateInvoiceURL;
    String rzChargeURL;
    Double ssizedouble;

//    final String your_key_id = "rzp_live_yjf2wTSV05B9Fq";
//    final String key_secret = "hbjVhO5y7rbpapfk5cAGEtEJ";

    final String your_key_id = "rzp_test_uDpwVNvR5hvR9N";
    final String key_secret = "aYyJ1mipvXYX0RVfYKX2LCVH";
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

    //Notification Variables
    BroadcastReceiver mRegistrationBroadcastReceiver;
    String token;

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

    String accountStatus;
    boolean internetStatus;
    String value,updateinv;
    TextView curret_plan_textview,valid_from_textview,valid_upto_textview;

//    public static final String BROADCAST = "com.faast.mobile.apps.android.action.mConnReceiver";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hotspot_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00ba30")));
        getSupportActionBar().setTitle("Welcome " + FirstName);

        Window window = HotspotHome.this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(HotspotHome.this.getResources().getColor(R.color.my_statusbar_color));
        }

        mActivity = HotspotHome.this;
        mNotificationManager=(NotificationManager) this.getApplicationContext().getSystemService(this.NOTIFICATION_SERVICE);

        ViewFlipper flipper=(ViewFlipper)findViewById(R.id.viewFlipper_home_hotspot);
        final Animation animFlipInNext = AnimationUtils.loadAnimation(this, R.anim.flipinnext);
        final Animation animFlipOutNext = AnimationUtils.loadAnimation(this, R.anim.flipoutnext);

        flipper.setInAnimation(animFlipInNext);
        flipper.setOutAnimation(animFlipOutNext);
        flipper.showNext();
        flipper.startFlipping();

        SharedPreferences HotspotUser = this.getSharedPreferences("hotspot_user", MODE_PRIVATE);
        UserName = HotspotUser.getString("macid","");

        System.out.println("Mac id: Username"+UserName);
        SharedPreferences Links = getApplicationContext().getSharedPreferences("DatabaseLinks", MODE_PRIVATE);
        homeUserDataURL = Links.getString("hotspot_home_page_details","");

        Intent i = getIntent();
        message = i.getStringExtra("Message");

        curret_plan_textview = (TextView) findViewById(R.id.current_plan_hotspot);
        gauge1 = (CustomGauge) findViewById(R.id.home_hotspot_gauge);
        graph_value = (TextView) findViewById(R.id.graph_value_home_hotspot);
        srvname_textview = (TextView) findViewById(R.id.current_plan_hotspot);
        valid_from_textview = (TextView) findViewById(R.id.valid_from);
        valid_upto_textview = (TextView) findViewById(R.id.valid_upto);


        gv = (GridView) findViewById(R.id.gridview_home_hotspot);
        gv.setAdapter(new CustomAdapter(HotspotHome.this, menu_list, menu_images));

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            changeTextStatus(true);
        } else {
            changeTextStatus(false);
        }

        navigationView = (NavigationView) findViewById(R.id.nav_view_home_hotspot);

        navigationView.setNavigationItemSelectedListener(this);

        header = navigationView.getHeaderView(0);
//        nav_username=(TextView) header.findViewById(R.id.nav_bar_username);
//        nav_username.setText(UserName);

        nav_back_button =(ImageView) header.findViewById(R.id.nav_back_arrow);
        nav_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_home_hotspot);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_home_hotspot);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

    }


    public void changeTextStatus(boolean isConnected) {

        // Change status according to boolean value
        if (isConnected) {
            new LoadUserDetails().execute();
            System.out.println("Connected");
        } else {
//            Toast.makeText(getApplicationContext(),"disConnected",Toast.LENGTH_SHORT).show();
            System.out.println("Disconnected");

            AlertDialog.Builder builder111 = new AlertDialog.Builder(HotspotHome.this);
            // Set the Alert Dialog Message
            builder111.setMessage("Internet Connection Required")
                    .setCancelable(true)
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
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_home_hotspot);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.recharge_history_hotspot) {
            Intent paymenthistory = new Intent(this,RechargeHistory.class);
            startActivity(paymenthistory);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        } else if (id == R.id.contact_us_menu_hotspot) {
            Intent paymenthistory = new Intent(this,com.faast.mobile.apps.ContactUs.class);
            startActivity(paymenthistory);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        } else if (id == R.id.version_menu_hotspot) {
            Intent version = new Intent(this,com.faast.mobile.apps.AppVersion.class);
            startActivity(version);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_home_hotspot);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //-----------------------------BROADCAST RECEIVER----------------------------------

    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
//            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.CONNECTIVITY_ACTION, false);
//            String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
//            boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);
//
//            NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
//            NetworkInfo otherNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);
//
//            if (noConnectivity) {
//            }
            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
            boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);

            NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            NetworkInfo otherNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

            if (currentNetworkInfo.isConnected()) {

            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(HotspotHome.this);
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

    //-----------------------------CUSTOMER DETAILS ----------------------------------

    class LoadUserDetails extends AsyncTask<String, String, String> {

        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog1 = createProgressDialog(HotspotHome.this);
            pDialog1.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            int success;

            try {
                Log.d("try", "in the try");
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                params.add(new BasicNameValuePair("username", UserName));
                JSONObject json = jsonParser1.makeHttpRequest(
                        homeUserDataURL, "POST", params);
                Log.d("jsonObject", "new json Object");
                // check your log for json response
//                Log.d("Load Profile", json.toString());
                matchFixture1 = json.getJSONArray(TAG_PRODUCTS1);
                JSONObject c1 = matchFixture1.getJSONObject(0);
                success = c1.getInt(TAG_SUCCESS1);
                // json success tag

                System.out.println(success);
                if (success == 1 ) {

                    // successfully received user details
                    // get first product object from JSON Array

                    for (int i = 0; i < matchFixture1.length(); i++) {
                        System.out.println(matchFixture1);

                        JSONObject c = matchFixture1.getJSONObject(i);

                        String firstname = c.getString(TAG_FIRSTNAME1);
                        String lastname = c.getString(TAG_LASTNAME1);
                        String srvname = c.getString(TAG_SRVNAME1);
                        String remaining_data_digit = c.getString(TAG_REMAINING_DATA_DIGIT);
                        String total_data_digit = c.getString(TAG_TOTAL_DATA_DIGIT);
                        String total_data_graph = c.getString(TAG_TOTAL_DATA_GRAPH);
                        String remaining_data_graph = c.getString(TAG_REMAINING_DATA_GRAPH);
                        String mobile = c.getString(TAG_MOBILE1);
                        String email = c.getString(TAG_EMAIL1);
                        String acc_status = c.getString(TAG_STATUS1);
                        String valid_from = c.getString(TAG_VALID_FROM);
                        String valid_upto = c.getString(TAG_VALID_UPTO);
                        String used_data = c.getString(TAG_USED_DATA_MB);

                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_FIRSTNAME1, firstname);
                        map.put(TAG_LASTNAME1, lastname);
                        map.put(TAG_SRVNAME1, srvname);
                        map.put(TAG_REMAINING_DATA_DIGIT, remaining_data_digit);
                        map.put(TAG_TOTAL_DATA_DIGIT, total_data_digit);
                        map.put(TAG_TOTAL_DATA_GRAPH, total_data_graph);
                        map.put(TAG_REMAINING_DATA_GRAPH, remaining_data_graph);
                        map.put(TAG_EMAIL1, email);
                        map.put(TAG_MOBILE1, mobile);
                        map.put(TAG_STATUS1, acc_status);
                        map.put(TAG_VALID_FROM, valid_from);
                        map.put(TAG_VALID_UPTO, valid_upto);
                        map.put(TAG_USED_DATA_MB, used_data);

                        Log.d("Status",acc_status);

                        // adding HashList to ArrayList
                        profileList1.add(map);
                    }
                }
                else if (success == 0) {
                    //Log.d("Failure!", json.getString(TAG_MESSAGE));
                    //return json.getString(TAG_MESSAGE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // dismiss the dialog once got all details
            pDialog1.dismiss();

            for (HashMap<String, String> map : profileList1) {
                final String firstname = map.get(TAG_FIRSTNAME1);
                final String lastname = map.get(TAG_LASTNAME1);
                final String srvname = map.get(TAG_SRVNAME1);
                final String remaining_data_digit1 = map.get(TAG_REMAINING_DATA_DIGIT);
                final String total_data_digit1 = map.get(TAG_TOTAL_DATA_DIGIT);
                final String total_data_graph1 = map.get(TAG_TOTAL_DATA_GRAPH);
                final String remaining_data_graph1 = map.get(TAG_REMAINING_DATA_GRAPH);
                final String used_data_mb1 = map.get(TAG_USED_DATA_MB);
                final String mobile = map.get(TAG_MOBILE1);
                final String email = map.get(TAG_EMAIL1);
                final String validfrom = map.get(TAG_VALID_FROM);
                final String validupto = map.get(TAG_VALID_UPTO);
                accountStatus = map.get(TAG_STATUS1);
                //graph_value.setText(remaining_data + " GB \n left of \n" + total_data + "GB");

                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_home_hotspot);
                navigationView.setNavigationItemSelectedListener(HotspotHome.this);

                View header = navigationView.getHeaderView(0);
                nav_username = (TextView) header.findViewById(R.id.nav_bar_home_hotspot_username);
                nav_username.setText("    Hello! " + firstname);
                String valid_from_date = getconvertdate1(validfrom);
                String valid_upto_date = getconvertdate1(validupto);

                valid_from_textview.setText(valid_from_date);
                valid_upto_textview.setText(valid_upto_date);

                getSupportActionBar().setTitle("Welcome " + firstname);
                Double rd2 = Double.parseDouble(remaining_data_digit1);
                Double td2 = Double.parseDouble(total_data_digit1);
                Double ud2 = Double.parseDouble(used_data_mb1);

                Integer i;
                Double graphv;
                if (td2 == 0.00 && ud2 != 0.00) {
                    graphv = 779.00;
                    graph_value.setText("Consumed Data\n" + td2 + "GB");
                } else {
                    graphv = (ud2 / td2) * 800;
                    graph_value.setText(remaining_data_graph1+"\n left of \n" + total_data_graph1 + "");
                }
                i = graphv.intValue();
                System.out.println(i);
                gauge1.setValue(i);
                curret_plan_textview.setText(srvname);

                SharedPreferences myPrefs1 = getApplicationContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
                SharedPreferences.Editor editor1 = myPrefs1.edit();
                editor1.putString("firstname", firstname);
                editor1.putString("lastname", lastname);
                editor1.putString("srvname", srvname);
                editor1.putString("mobile", mobile);
                editor1.putString("email", email);

                editor1.commit();


                if (message!=null){
                    BuilderNotification = new AlertDialog.Builder(HotspotHome.this, R.style.MyDialogTheme);

                    //Setting message manually and performing action on button click
                    BuilderNotification.setMessage(message)
                            .setCancelable(false)
                            .setTitle("FAAST")
                            .setIcon(R.mipmap.arrow_white)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    if(accountStatus.equals("0")){
                                        AlertDialog.Builder AccountStatusAlertBox = new AlertDialog.Builder(HotspotHome.this, R.style.MyDialogTheme);

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
                        AlertDialog.Builder AccountStatusAlertBox = new AlertDialog.Builder(HotspotHome.this, R.style.MyDialogTheme);

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
                        HotspotHome.GetVersionCode getVersionCode=new HotspotHome.GetVersionCode();
                        getVersionCode.execute();
                    }
                }
            }
        }
    }

    //-----------------------------CUSTOM ADAPTER FOR HOME GRIDS----------------------------------

    public class CustomAdapter extends BaseAdapter {

        String[] result;
        Context context;
        int [] imageId;
        private LayoutInflater inflater=null;
        public CustomAdapter(HotspotHome mainActivity, String[] prgmNameList, int[] prgmImages) {
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
                        Intent intent = new Intent(HotspotHome.this,HotspotUsageReport.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                    else if(position == 1)
                    {
                        Intent intent = new Intent(HotspotHome.this,HotspotTopups.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                    else if(position == 2)
                    {

                    }
                }
            });
            return rowView;
        }

    }

    //-----------------------------RAZOR PAYMENT ----------------------------------

    public void startPayment(Double Amount){
        /**
         * Put your key id generated in Razorpay dashboard here
         */
        SharedPreferences myPrefs1 = this.getSharedPreferences("UserDetails",MODE_PRIVATE);
        FirstNameP = myPrefs1.getString("firstname","");
        mobileP = myPrefs1.getString("mobile","");
        emailP = myPrefs1.getString("email","");
        Checkout razorpayCheckout = new Checkout();
        razorpayCheckout.setKeyID(your_key_id);

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
                    value= getdb.getData(your_key_id,key_secret,s,razorpayPaymentID1,rzChargeURL);

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
                                        updateinv= ui.updateInvoice(UserName,razorpayPaymentID1,updateInvoiceURL,"Online");

                                        System.out.println("upadted invoice:"+updateinv);
                                        uv=new Integer(value);
                                        uv1=uv.intValue();

                                        runOnUiThread(new Runnable() {

                                            @Override
                                            public void run() {
                                                if(updateinv.equalsIgnoreCase("success"))
                                                {
                                                    Toast.makeText(getApplicationContext(), "Payment Successful", Toast.LENGTH_LONG).show();
                                                    Intent i=new Intent(HotspotHome.this, HotspotHome.class);
                                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    finishAffinity();
                                                    startActivity(i);
                                                }
                                                else
                                                {
                                                    Toast.makeText(getApplicationContext(), "Payment Successful", Toast.LENGTH_LONG).show();
                                                    Intent i=new Intent(getApplicationContext(), HotspotHome.class);
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
                                Intent i=new Intent(getApplicationContext(), HotspotHome.class);
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
//

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
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + HotspotHome.this.getPackageName() + "&hl=it")
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
            if (Float.valueOf(appVersion) < Float.valueOf(onlineVersion)) {
                if (!appVersion.equals(onlineVersion)) {
                    //show dialog
                    AlertDialog.Builder builderUpdate = new AlertDialog.Builder(HotspotHome.this, R.style.MyDialogTheme);

                    //Setting message manually and performing action on button click
                    builderUpdate.setMessage("New version available in the playstore, please update the app.");
                    builderUpdate.setCancelable(true);
                    builderUpdate.setIcon(R.mipmap.arrow_white);
                    builderUpdate.setTitle("FAAST");

                    builderUpdate.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
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
                                finishAffinity();
                            }
                            return false;

                        }
                    });
                }
                else {

                }
            }
            System.out.println("update "+ "Current version " + appVersion + "playstore version " + onlineVersion);
        }

    }



}
