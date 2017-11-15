package com.faast.mobile.apps.faastwifi;

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

import com.faast.mobile.apps.JSONParser;
import com.faast.mobile.apps.R;

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

public class HotspotTopups extends AppCompatActivity {
    ArrayList<HotspotTopupsModel> dataModels;
    ListView listView;
    private static HotspotTopups.CustomAdapter adapter;
    String data = "";
    ArrayList<HotspotTopupsModel> Users1;
    ArrayList<HotspotTopupsModel> users = new ArrayList<>();
    TextView current_service;
    TextView current_service_price;
    TextView current_service_data;
    TextView current_service_speed;
    TextView current_service_post_plan;
    String currentPlanURL,allPlansURL,planActivationURL,UserName;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "fixture";
    private static final String TAG_SERVICENAME = "service_name";
    private static final String TAG_SERVICEPRICE = "service_price";
    private static final String TAG_SERVICESPEED = "speed";
    private static final String TAG_SERVICEDATA = "total_data";
    private static final String TAG_SERVICEID = "srvid";

    public String current_plan_srvid;

    JSONParser jsonParser1 = new JSONParser();
    ArrayList<HashMap<String, String>> profileList1 = new ArrayList<HashMap<String, String>>();
    JSONArray matchFixture1 = null;
    ProgressDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hotspot_topups);

        Window window = HotspotTopups.this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(HotspotTopups.this.getResources().getColor(R.color.my_statusbar_color));
        }

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00ba30")));
        getSupportActionBar().setTitle("Topups");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences Links = getApplicationContext().getSharedPreferences("DatabaseLinks", MODE_PRIVATE);
        currentPlanURL = Links.getString("currentplandetails","");
        allPlansURL = Links.getString("hotspotgetallplans","");
        planActivationURL = Links.getString("planchangeactivation","");

        SharedPreferences myPrefs = this.getSharedPreferences("contacts", MODE_PRIVATE);
        UserName = myPrefs.getString("Username", "");

        listView = (ListView)findViewById(R.id.hotspot_topups_plan_list);


        final GetHotspotPlans getHotspotPlans = new GetHotspotPlans();
        new Thread(new Runnable() {
            public void run() {
                data = getHotspotPlans.getplans(allPlansURL);
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

    public ArrayList<HotspotTopupsModel> parseJSON(String result) {
        try {
            JSONArray jArray = new JSONArray(result);
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json_data = jArray.getJSONObject(i);

                users.add(new HotspotTopupsModel(json_data.getString("srvname"),json_data.getString("srvid"),json_data.getString("unitprice"),json_data.getString("trafficunitcomb"),json_data.getString("speed")));

            }
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
        return users;
    }


    public void addData(ArrayList<HotspotTopupsModel> users) {
        adapter = new CustomAdapter(users, HotspotTopups.this);

        listView.setAdapter(adapter);

    }

//--------------Custom Adapter for get all plan list------------

    public class CustomAdapter extends ArrayAdapter<HotspotTopupsModel> implements View.OnClickListener{

        public ArrayList<HotspotTopupsModel> dataSet;
        Context mContext;

        // View lookup cache
        public class ViewHolder {
            public TextView txtName;
            public TextView txtSpeedDetails;
            public TextView txtPrice;
            public TextView txtTotalData;
            Button info;
        }

        public CustomAdapter(ArrayList<HotspotTopupsModel> data, Context context) {
            super(context, R.layout.hotspot_topups_list_items, data);
            this.dataSet = data;
            this.mContext = context;
        }

        @Override
        public void onClick(View v) {

            int position = (Integer) v.getTag();
            Object object = getItem(position);
            final HotspotTopupsModel dataModel = (HotspotTopupsModel)object;

            switch (v.getId())
            {
                case R.id.hotspot_buy:
                    //Toast.makeText(mContext,dataModel.getName(),Toast.LENGTH_SHORT).show();

                    AlertDialog.Builder builder = new AlertDialog.Builder(HotspotTopups.this);
                    //Uncomment the below code to Set the message and title from the strings.xml file
                    //builder.setMessage(R.string.dialog_message) .setTitle(R.string.dialog_title);

                    //Setting message manually and performing action on button click
                    builder.setMessage("Are you sure?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                        //finish();
                                        recharge_activation(UserName, dataModel.getName(), dataModel.getId(), current_service.getText().toString(),current_plan_srvid);
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
            HotspotTopupsModel dataModel = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            HotspotTopups.CustomAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

            final View result;

            if (convertView == null) {

                viewHolder = new HotspotTopups.CustomAdapter.ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.hotspot_topups_list_items, parent, false);
                viewHolder.txtName = (TextView) convertView.findViewById(R.id.hotspot_topups_name);
                viewHolder.txtSpeedDetails = (TextView) convertView.findViewById(R.id.hotspot_topups_speed);
                viewHolder.txtPrice = (TextView) convertView.findViewById(R.id.hotspot_topups_price);
                viewHolder.txtTotalData = (TextView) convertView.findViewById(R.id.hotspot_topups_data);
                viewHolder.info = (Button) convertView.findViewById(R.id.hotspot_buy);
                result=convertView;

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (HotspotTopups.CustomAdapter.ViewHolder) convertView.getTag();
                result = convertView;
            }

            Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
            result.startAnimation(animation);
            lastPosition = position;

            viewHolder.txtName.setText(dataModel.getName());
            viewHolder.txtSpeedDetails.setText("upto "+dataModel.getSpeed()+" Mbps");
            Double amount=Double.parseDouble(dataModel.getPrice());
            Format formatter = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
            String moneyString = formatter.format(amount);

            viewHolder.txtPrice.setText(moneyString+"*/MO");
            viewHolder.txtTotalData.setText(dataModel.getTotalData()+" GB");
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

    private void recharge_activation(String username,String newsrvname,String newsrvid,String oldsrvname,String oldsrvid) {

        class LoginAsync extends AsyncTask<String, Void, String> {

            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                // loadingDialog = ProgressDialog.show(PlanChange.this, "Please wait", "Loading...");
                loadingDialog = createProgressDialog(HotspotTopups.this);
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(HotspotTopups.this,R.style.MyDialogTheme);
                    builder.setIcon(R.mipmap.arrow_white);
                    builder.setTitle("FAAST");
                    builder.setMessage("Kindly wait for the current billing cycle to complete.")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.show();

                }
                else if (s.equalsIgnoreCase("failuretwo"))  {
                    AlertDialog.Builder builder = new AlertDialog.Builder(HotspotTopups.this,R.style.MyDialogTheme);
                    builder.setIcon(R.mipmap.arrow_white);
                    builder.setTitle("FAAST");
                    builder.setMessage("A request for plan change is already pending.")
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(HotspotTopups.this,R.style.MyDialogTheme);
                    builder.setIcon(R.mipmap.arrow_white);
                    builder.setTitle("FAAST");
                    builder.setMessage("A request for plan change is already pending, kindly wait for the current billing cycle to complete.")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.show();
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
                AlertDialog.Builder builder = new AlertDialog.Builder(HotspotTopups.this);
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


    public class HotspotTopupsModel {

        String name;
        String id;
        String price;
        String total_data;
        String speed;
        String postspeed;


        public HotspotTopupsModel(String name, String id, String price, String total_data, String speed) {
            this.name = name;
            this.id = id;
            this.price = price;
            this.total_data = total_data;
            this.speed = speed;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public void setTotal_data(String total_data) {
            this.total_data = total_data;

        }

        public String getName() {
            return name;
        }

        public String getSpeed() {
            return speed;
        }

        public String getId() {
            return id;
        }

        public String getPrice() {
            return price;
        }

        public String getTotalData() {
            return total_data;
        }

    }

}