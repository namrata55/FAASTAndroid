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

public class Topups extends AppCompatActivity {

    ArrayList<DataModel> dataModels;
    ListView listView;
    public CustomAdapter adapter;
    String UserName;
    String topupsActivationURL , allTopupsURL;

    JSONParser jsonParser1 = new JSONParser();
    ArrayList<HashMap<String, String>> profileList1 = new ArrayList<HashMap<String, String>>();
    JSONArray matchFixture1 = null;
    ProgressDialog loadingDialog;
    ArrayList<DataModel> Users1;
    ArrayList<DataModel> users = new ArrayList<>();

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "fixture";
    private static final String TAG_TOPUPSNAME = "topups_name";
    private static final String TAG_TOPUPSPRICE = "topups_price";
    private static final String TAG_TOPUPSDATA = "topups_tax";
    private static final String TAG_TOPUPSFINALAMOUNT = "topups_final_amount";
    private static final String TAG_TOPUPSVATPERCENT = "topups_vatpercent";
    String data = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topups);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00ba30")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Topups");


        Window window = Topups.this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Topups.this.getResources().getColor(R.color.my_statusbar_color));
        }


        SharedPreferences myPrefs = this.getSharedPreferences("contacts", MODE_PRIVATE);
        UserName = myPrefs.getString("Username", "");

        SharedPreferences Links = getApplicationContext().getSharedPreferences("DatabaseLinks", MODE_PRIVATE);
        topupsActivationURL = Links.getString("topupsactivation","");
        allTopupsURL = Links.getString("gettopupsurl","");

        listView = (ListView) findViewById(R.id.list);

        final GetAllTopups getdb = new GetAllTopups();
        new Thread(new Runnable() {
            public void run() {
                data = getdb.getTopups(allTopupsURL);
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

    public void addData(ArrayList<DataModel> users) {
        adapter = new CustomAdapter(users,Topups.this);

        listView.setAdapter(adapter);

    }


    public ArrayList<DataModel> parseJSON(String result) {
        try {
            JSONArray jArray = new JSONArray(result);
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json_data = jArray.getJSONObject(i);

                users.add(new DataModel(json_data.getString("name"),json_data.getString("data_in_byte"),json_data.getString("price"),json_data.getString("tax"),json_data.getString("final_amount"), json_data.getString("vat_percent")));

            }
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
        return users;
    }

    public class DataModel {

        String topupsData;
        String topupsDataInBytes;
        String topupsPrice;
        String topupsVatPercent;
        String topupsFinalAmount;
        String topupsTax;

        public DataModel(String topupsData,String topupsDataInBytes, String topupsPrice,String topupsTax,String topupsFinalAmount , String topupsVatPercent) {
            this.topupsData = topupsData;
            this.topupsDataInBytes = topupsDataInBytes;
            this.topupsPrice = topupsPrice;
            this.topupsVatPercent = topupsVatPercent;
            this.topupsFinalAmount = topupsFinalAmount;
            this.topupsTax = topupsTax;

        }

        public String getTopupsData() {
            return topupsData;
        }

        public String getTopupsPrice() {
            return topupsPrice;
        }
        public String getTopupsDataInBytes() {
            return topupsDataInBytes;
        }
        public String getTopupsTax() {
            return topupsTax;
        }
        public String getTopupsFinalAmount() {
            return topupsFinalAmount;
        }
        public String getTopupsVatPercent() {
            return topupsVatPercent;
        }
    }


    public class CustomAdapter extends ArrayAdapter<DataModel> implements View.OnClickListener {

        private ArrayList<DataModel> dataSet;
        Context mContext;

        // View lookup cache
        private class ViewHolder {
            TextView topups_data;
            TextView topups_price;
            Button request_button;

        }

        public CustomAdapter(ArrayList<DataModel> data, Context context) {
            super(context, R.layout.topups_list_items_row, data);
            this.dataSet = data;
            this.mContext = context;
        }

        @Override
        public void onClick(View v) {

            int position = (Integer) v.getTag();
            Object object = getItem(position);
            final DataModel dataModel = (DataModel) object;

            switch (v.getId())
            {
                case R.id.topup_request_button:

                    AlertDialog.Builder builder = new AlertDialog.Builder(Topups.this);
                    //Uncomment the below code to Set the message and title from the strings.xml file
                    //builder.setMessage(R.string.dialog_message) .setTitle(R.string.dialog_title);

                    //Setting message manually and performing action on button click
                    builder.setMessage("Do you want to activate the topup?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    topup_activation(UserName,dataModel.getTopupsData(),dataModel.getTopupsPrice(),dataModel.getTopupsDataInBytes(),dataModel.getTopupsTax(),dataModel.getTopupsVatPercent());

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
            Topups.DataModel dataModel = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            ViewHolder viewHolder; // view lookup cache stored in tag

            final View result;

            if (convertView == null) {

                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.topups_list_items_row, parent, false);
                viewHolder.topups_data = (TextView) convertView.findViewById(R.id.topups_data);
                viewHolder.topups_price = (TextView) convertView.findViewById(R.id.topups_price);
                viewHolder.request_button = (Button) convertView.findViewById(R.id.topup_request_button);

                result = convertView;

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
                result = convertView;
            }

            Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
            result.startAnimation(animation);
            lastPosition = position;

            Double amount = Double.parseDouble(dataModel.getTopupsPrice());
            Format formatter = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
            String moneyString = formatter.format(amount);
            viewHolder.topups_price.setText(moneyString+"*");
            viewHolder.topups_data.setText(dataModel.getTopupsData());
            viewHolder.request_button.setOnClickListener(this);
            viewHolder.request_button.setTag(position);

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

    private void topup_activation(final String username, final String topups_data, final String topups_price, final String topups_data_in_bytes,final String topups_tax,final String topups_vat) {

        class LoginAsync extends AsyncTask<String, Void, String> {

            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                // loadingDialog = ProgressDialog.show(PlanChange.this, "Please wait", "Loading...");
                loadingDialog = createProgressDialog(Topups.this);
                loadingDialog.show();
            }

            @Override
            protected String doInBackground(String... params) {

                InputStream is = null;
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("username", username));
                nameValuePairs.add(new BasicNameValuePair("topupsdata", topups_data));
                nameValuePairs.add(new BasicNameValuePair("topupsprice", topups_price));
                nameValuePairs.add(new BasicNameValuePair("topupsdatainbytes", topups_data_in_bytes));
                nameValuePairs.add(new BasicNameValuePair("topupstax", topups_tax));
                nameValuePairs.add(new BasicNameValuePair("topupsvat", topups_vat));

                String result = null;

                try {
                    HttpClient httpClient = new DefaultHttpClient();
//                    HttpPost httpPost = new HttpPost(
//                            "http://10.0.2.2/android_faast_db/plan_activation.php");
//                    HttpPost httpPost = new HttpPost(
//                            "http://192.168.1.17/hotspot_android/plan_activation.php");
                    HttpPost httpPost = new HttpPost(topupsActivationURL);
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
                        Log.d("username=", username);
                        Log.d("data=", topups_data);
                        Log.d("price=", topups_price);
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
                System.out.println("Plan Changed="+result);
                String s = result.trim();
                if (s.equalsIgnoreCase("success")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Topups.this);
                    //Uncomment the below code to Set the message and title from the strings.xml file
                    //builder.setMessage(R.string.dialog_message) .setTitle(R.string.dialog_title);

                    //Setting message manually and performing action on button click
                    builder.setMessage("Your topups has been activated successfully")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent(Topups.this,HomeInternetStatus.class);
                                    startActivity(i);
                                }
                            });
                    //Creating dialog box
                    AlertDialog alert = builder.create();
                    //Setting the title manually
                    //alert.setTitle("AlertDialogExample");
                    alert.show();

                } else {
                    Toast.makeText(getApplicationContext(), "Sorry!Your request for top up is not registered...Please try Once agian", Toast.LENGTH_LONG).show();
                }
            }
        }

        LoginAsync la = new LoginAsync();
        la.execute(username,topups_data);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(Topups.this);
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

}
