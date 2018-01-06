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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PlanChange extends AppCompatActivity {
    ArrayList<PlanChangeDataModel> dataModels;
    ListView listView;
    private static CustomAdapter adapter;
    String data = "";
    ArrayList<PlanChangeDataModel> Users1;
    ArrayList<PlanChangeDataModel> users = new ArrayList<>();
    TextView current_service;
    TextView current_service_price;
    TextView current_service_data;
    TextView current_service_speed;
    TextView current_service_post_plan;
    String currentPlanURL,allPlansURL,planActivationURL,UserName,FaastPrimeStatus;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "fixture";
    private static final String TAG_SERVICENAME = "service_name";
    private static final String TAG_SERVICEPRICE = "service_price";
    private static final String TAG_SERVICESPEED = "speed";
    private static final String TAG_SERVICEPOSTSPEED = "post_speed";
    private static final String TAG_SERVICEDATA = "total_data";
    private static final String TAG_SERVICEID = "srvid";
    private static final String TAG_SERVICEDESCR = "descr";

    public String current_plan_srvid;
    public String current_srvname,current_plan_srvdescr;

    JSONParser jsonParser1 = new JSONParser();
    ArrayList<HashMap<String, String>> profileList1 = new ArrayList<HashMap<String, String>>();
    JSONArray matchFixture1 = null;
    ProgressDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plan_change);
        loadingDialog = createProgressDialog(PlanChange.this);
        loadingDialog.show();
        Window window = PlanChange.this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(PlanChange.this.getResources().getColor(R.color.my_statusbar_color));
        }

        SharedPreferences Links = getApplicationContext().getSharedPreferences("DatabaseLinks", MODE_PRIVATE);
        currentPlanURL = Links.getString("currentplandetails","");
        allPlansURL = Links.getString("getallplans","");
        planActivationURL = Links.getString("planchangeactivation","");

        SharedPreferences myPrefs = this.getSharedPreferences("contacts", MODE_PRIVATE);
        UserName = myPrefs.getString("Username", "");

        SharedPreferences myPrefs1 = this.getSharedPreferences("UserDetails", MODE_PRIVATE);
        FaastPrimeStatus = myPrefs1.getString("faastPrimeMembershipStatus", "");

        Log.e("FaastPrimeStatus",FaastPrimeStatus);

        listView = (ListView)findViewById(R.id.list);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00ba30")));
        getSupportActionBar().setTitle("Plan Change");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        current_service = (TextView)findViewById(R.id.service_name);
        current_service_price = (TextView)findViewById(R.id.service_price);
        current_service_speed = (TextView)findViewById(R.id.service_speed_label);
        current_service_data = (TextView)findViewById(R.id.service_data);
        current_service_post_plan = (TextView)findViewById(R.id.current_plan_postspeed);

        new LoadCurrentPlan().execute();

    }

    public ArrayList<PlanChangeDataModel> parseJSON(String result) {
        try {
            JSONArray jArray = new JSONArray(result);
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json_data = jArray.getJSONObject(i);

                    users.add(new PlanChangeDataModel(json_data.getString("srvname"),json_data.getString("srvid"),json_data.getString("unitprice"),json_data.getString("trafficunitcomb"),json_data.getString("speed"), json_data.getString("postspeed"),json_data.getString("descr"),json_data.getString("total_monthly_data")));

            }
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
        return users;
    }


    public void addData(ArrayList<PlanChangeDataModel> users) {
        adapter = new CustomAdapter(users,PlanChange.this);

        listView.setAdapter(adapter);

    }

    //------------------Getting current plan details---------------

    class LoadCurrentPlan extends AsyncTask<String, String, String> {

        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pDialog1 = new ProgressDialog(PlanChange.this);
//            pDialog1.setMessage("Loading. Please wait...");
//            pDialog1.setIndeterminate(false);
//            pDialog1.setCancelable(true);
//            pDialog1.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            int success;

            try {
                Log.d("try", "in the try");
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                params.add(new BasicNameValuePair("username", UserName));
//                JSONObject json = jsonParser1.makeHttpRequest(
//                        "http://10.0.2.2/android_faast_db/current_plan.php", "POST", params);

                JSONObject json = jsonParser1.makeHttpRequest(
                        currentPlanURL, "POST", params);
                Log.d("jsonObject", "new json Object");
                // check your log for json response
//                Log.d("Load Profile", json.toString());
                matchFixture1 = json.getJSONArray(TAG_PRODUCTS);
                JSONObject c1 = matchFixture1.getJSONObject(0);
                success = c1.getInt(TAG_SUCCESS);
                // json success tag
                System.out.println(success);
                if (success == 1 ) {

                    for (int i = 0; i < matchFixture1.length(); i++) {
                        JSONObject c = matchFixture1.getJSONObject(i);
                        String srv_name = c.getString(TAG_SERVICENAME);
                        String srv_price = c.getString(TAG_SERVICEPRICE);
                        String srv_data = c.getString(TAG_SERVICEDATA);
                        String srv_speed = c.getString(TAG_SERVICESPEED);
                        String srv_id = c.getString(TAG_SERVICEID);
                        String srv_post_speed = c.getString(TAG_SERVICEPOSTSPEED);
                        String srv_descr = c.getString(TAG_SERVICEDESCR);

                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_SERVICENAME, srv_name);
                        map.put(TAG_SERVICEPRICE, srv_price);
                        map.put(TAG_SERVICEDATA, srv_data);
                        map.put(TAG_SERVICESPEED, srv_speed);
                        map.put(TAG_SERVICEID, srv_id);
                        map.put(TAG_SERVICEPOSTSPEED, srv_post_speed);
                        map.put(TAG_SERVICEDESCR, srv_descr);

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
            //pDialog1.dismiss();
            loadingDialog.dismiss();
            super.onPostExecute(result);
            // dismiss the dialog once got all details

            for (HashMap<String, String> map : profileList1) {
                current_srvname = map.get(TAG_SERVICENAME);
                final String srvprice = map.get(TAG_SERVICEPRICE);
                final String srvdata = map.get(TAG_SERVICEDATA);
                final String srvspeed = map.get(TAG_SERVICESPEED);
                final String srvpostspeed = map.get(TAG_SERVICEPOSTSPEED);
                current_plan_srvid = map.get(TAG_SERVICEID);
                current_plan_srvdescr = map.get(TAG_SERVICEDESCR);

                current_service.setText(current_srvname);

                current_service_data.setText(srvdata);

                Double amount = Double.parseDouble(srvprice);
                Format formatter = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                String moneyString = formatter.format(amount);
                current_service_price.setText(moneyString+"*/MO");

                current_service_speed.setText(srvspeed);

//                Double current_plan_speed = Double.parseDouble(srvspeed);
//                Double post_speed = current_plan_speed/10;
                current_service_post_plan.setText(srvpostspeed);

                final GetAllPlans getdb = new GetAllPlans();
                new Thread(new Runnable() {
                    public void run() {
                        System.out.print("cureent_plan"+current_srvname);
                        data = getdb.getplans(allPlansURL,current_srvname,current_plan_srvdescr,FaastPrimeStatus);
                        System.out.println(data);

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                Users1 = parseJSON(data);
                                addData(Users1);
                                System.out.println("Output:"+Users1);
                            }
                        });

                    }
                }).start();

            }
        }
    }

//--------------Custom Adapter for get all plan list------------

    public class CustomAdapter extends ArrayAdapter<PlanChangeDataModel> implements View.OnClickListener{

        public ArrayList<PlanChangeDataModel> dataSet;
        Context mContext;

        // View lookup cache
        public class ViewHolder {
            public TextView txtName;
            public TextView txtSpeedDetails;
            public TextView txtPrice;
            public TextView txtTotalData;
            public TextView txtSpeed;
            public TextView textPostSpeed;
            public TextView textTotalDataDaily;
            Button info;
        }

        public CustomAdapter(ArrayList<PlanChangeDataModel> data, Context context) {
            super(context, R.layout.plan_change_row_item, data);
            this.dataSet = data;
            this.mContext = context;

        }

        @Override
        public void onClick(View v) {

            int position = (Integer) v.getTag();
            Object object = getItem(position);
            final PlanChangeDataModel dataModel = (PlanChangeDataModel)object;

            switch (v.getId())
            {
                case R.id.item_info:
                    //Toast.makeText(mContext,dataModel.getName(),Toast.LENGTH_SHORT).show();

                    AlertDialog.Builder builder = new AlertDialog.Builder(PlanChange.this);
                    //Uncomment the below code to Set the message and title from the strings.xml file
                    //builder.setMessage(R.string.dialog_message) .setTitle(R.string.dialog_title);

                    //Setting message manually and performing action on button click
                    builder.setMessage("Are you sure?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if(current_plan_srvid.equals(dataModel.getId())){
                                        Toast toast = Toast.makeText(getApplicationContext(),"Selected plan is already active!", Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                    }
                                    else {
                                        //finish();
                                        plan_activation(UserName, dataModel.getName(), dataModel.getId(), current_service.getText().toString(),current_plan_srvid);
                                    }
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //  Action for 'NO' Button
                                    dialog.cancel();
                                }
                            });
                    //Creating dialog box
                    AlertDialog alert = builder.create();
                    //Setting the title manually
                    //alert.setTitle("AlertDialogExample");
                    alert.show();
                    break;
            }
        }

        private int lastPosition = -1;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            PlanChangeDataModel dataModel = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            ViewHolder viewHolder; // view lookup cache stored in tag

            final View result;

            if (convertView == null) {

                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.plan_change_row_item, parent, false);
                viewHolder.txtName = (TextView) convertView.findViewById(R.id.name);
                viewHolder.txtSpeedDetails = (TextView) convertView.findViewById(R.id.speed_details);
                viewHolder.txtPrice = (TextView) convertView.findViewById(R.id.price);
                viewHolder.txtTotalData = (TextView) convertView.findViewById(R.id.data);
                viewHolder.txtSpeed = (TextView) convertView.findViewById(R.id.speed);
                viewHolder.textPostSpeed = (TextView) convertView.findViewById(R.id.post_speed);
                viewHolder.textTotalDataDaily = (TextView) convertView.findViewById(R.id.total_data_daily);
                viewHolder.info = (Button) convertView.findViewById(R.id.item_info);

                result=convertView;

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
                result = convertView;
            }

            Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
            result.startAnimation(animation);
            lastPosition = position;
//            Double speedvalue = Double.parseDouble(dataModel.getSpeed());
//            Double postspeed = speedvalue/10;

            viewHolder.txtName.setText(dataModel.getName());
            viewHolder.txtSpeedDetails.setText("upto "+dataModel.getSpeed()+" Mbps");
            Double amount=Double.parseDouble(dataModel.getPrice());
            Format formatter = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
            String moneyString = formatter.format(amount);

            viewHolder.txtPrice.setText(moneyString+"*/MO");
            viewHolder.txtTotalData.setText(dataModel.getTotalData()+" GB");
            viewHolder.txtSpeed.setText(dataModel.getTotalData());
            viewHolder.textPostSpeed.setText(dataModel.getPostspeed());
            viewHolder.textTotalDataDaily.setText(dataModel.getTotalDataDaily());
            viewHolder.info.setOnClickListener(this);
            viewHolder.info.setTag(position);
            // Return the completed view to render on screen
            if (position % 2 == 0) {
                convertView.setBackgroundColor(Color.parseColor("#3ad2d1"));
            } else {
                convertView.setBackgroundColor(Color.parseColor("#e97d68"));
            }

            return convertView;
        }
    }

//-----------Plan activation Function---------------------

    private void plan_activation(String username,String newsrvname,String newsrvid,String oldsrvname,String oldsrvid) {

        class LoginAsync extends AsyncTask<String, Void, String> {

            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
               // loadingDialog = ProgressDialog.show(PlanChange.this, "Please wait", "Loading...");
                loadingDialog = createProgressDialog(PlanChange.this);
                loadingDialog.show();
            }

            @Override
            protected String doInBackground(String... params) {
                String uname = params[0];
                String newplanname = params[1];
                String newplanid = params[2];
                String oldplanname = params[3];


                InputStream is = null;
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("username", uname));
                nameValuePairs.add(new BasicNameValuePair("newsrvname", newplanname));
                nameValuePairs.add(new BasicNameValuePair("newsrvid", newplanid));
                nameValuePairs.add(new BasicNameValuePair("oldsrvname", oldplanname));
                String result = null;

                try {
                    HttpClient httpClient = new DefaultHttpClient();
//                    HttpPost httpPost = new HttpPost(
//                            "http://10.0.2.2/android_faast_db/plan_activation.php");
//                    HttpPost httpPost = new HttpPost(
//                            "http://192.168.1.17/hotspot_android/plan_activation.php");
                    HttpPost httpPost = new HttpPost(planActivationURL);
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
                System.out.println("Plan Changed="+result.trim());
                String s = result.trim();
                if (s.equalsIgnoreCase("success")) {
                    Toast toast = Toast.makeText(getApplicationContext(),"Your request for plan change has been registered.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                else if (s.equalsIgnoreCase("failureone"))  {


                    AlertDialog.Builder builder1 = new AlertDialog.Builder(PlanChange.this);
                    //Uncomment the below code to Set the message and title from the strings.xml file
                    //builder.setMessage(R.string.dialog_message) .setTitle(R.string.dialog_title);

                    //Setting message manually and performing action on button click
                    builder1.setMessage("Kindly wait for the current billing cycle to complete.")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                   dialog.dismiss();
                                }
                            });
                    //Creating dialog box
                    AlertDialog alert1 = builder1.create();
                    //Setting the title manually
                    //alert.setTitle("AlertDialogExample");
                    alert1.show();

//                    AlertDialog.Builder builder = new AlertDialog.Builder(PlanChange.this,R.style.MyDialogTheme);
//                    builder.setIcon(R.mipmap.arrow_white);
//                    builder.setTitle("FAAST");
//                    builder.setMessage("Kindly wait for the current billing cycle to complete.")
//                            .setCancelable(false)
//                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    dialog.cancel();
//                                }
//                            });
//
//                    AlertDialog alert = builder.create();
//                    alert.show();

                }
                else if (s.equalsIgnoreCase("failuretwo"))  {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PlanChange.this,R.style.MyDialogTheme);
                    builder.setIcon(R.mipmap.arrow_white);
                    builder.setTitle("FAAST");
                    builder.setMessage("Kindly wait for minimum 2 Billing cycles before requesting for a plan change.")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.show();

                }

                else{
//                    AlertDialog.Builder builder = new AlertDialog.Builder(PlanChange.this,R.style.MyDialogTheme);
//                    builder.setIcon(R.mipmap.arrow_white);
//                    builder.setTitle("FAAST");
//                    builder.setMessage("kindly wait for the current billing cycle to complete.")
//                            .setCancelable(false)
//                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    dialog.cancel();
//                                }
//                            });
//
//                    AlertDialog alert = builder.create();
//                    alert.show();


                    AlertDialog.Builder builder1 = new AlertDialog.Builder(PlanChange.this);
                    //Uncomment the below code to Set the message and title from the strings.xml file
                    //builder.setMessage(R.string.dialog_message) .setTitle(R.string.dialog_title);

                    //Setting message manually and performing action on button click
                    builder1.setMessage("Kindly wait for the current billing cycle to complete.")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    //Creating dialog box
                    AlertDialog alert1 = builder1.create();
                    //Setting the title manually
                    //alert.setTitle("AlertDialogExample");
                    alert1.show();
                }
            }
        }

        LoginAsync la = new LoginAsync();
        la.execute(username,newsrvname,newsrvid,oldsrvname,oldsrvid);
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


            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(PlanChange.this);
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
        // this.registerReceiver(this.mConnReceiver1,
        //new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        this.unregisterReceiver(mConnReceiver);
        //this.unregisterReceiver(mConnReceiver1);
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