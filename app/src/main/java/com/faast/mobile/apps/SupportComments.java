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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SupportComments extends AppCompatActivity {
    ListView listView;
    String UserName, supportcommentsURL,support_id,support_ticket_id;
    private static CustomAdapter adapter;
    String data = "";
    ArrayList<DataModel> users = new ArrayList<>();
    ProgressDialog loadingDialog;
    ArrayList<DataModel> Users1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.support_comments);

        Window window = SupportComments.this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(SupportComments.this.getResources().getColor(R.color.my_statusbar_color));
        }

        Intent i = getIntent();
        support_id = i.getStringExtra("ticket_id");
        support_ticket_id = i.getStringExtra("support_ticket_id");

        SharedPreferences Links = getApplicationContext().getSharedPreferences("DatabaseLinks", MODE_PRIVATE);
        supportcommentsURL = Links.getString("getsupportcomments", "");

        SharedPreferences myPrefs = this.getSharedPreferences("contacts", MODE_PRIVATE);
        UserName = myPrefs.getString("Username", "");

        listView = (ListView) findViewById(R.id.support_comment_list);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00ba30")));
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final GetAllSupportComments getdb = new GetAllSupportComments();
        new Thread(new Runnable() {
            public void run() {
                System.out.println("Support id-----"+support_ticket_id);
                data = getdb.getComments(supportcommentsURL,UserName,support_ticket_id);
                System.out.println("Support comments::::::::::"+data);

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        Users1 = parseJSON(data);
                        if(Users1.size()==0){
                            TextView t = (TextView) findViewById(R.id.no_data);
                            t.setVisibility(View.VISIBLE);
                        }
                        else {
                            addData(Users1);
                        }
                        System.out.println("Output:"+Users1);
                    }
                });

            }
        }).start();
    }

    public void addData(ArrayList<DataModel> users) {
        adapter = new CustomAdapter(users, SupportComments.this);
        listView.setAdapter(adapter);
    }

    public ArrayList<DataModel> parseJSON(String result) {
        try {
            JSONArray jArray = new JSONArray(result);
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json_data = jArray.getJSONObject(i);

                users.add(new DataModel(json_data.getString("comment"), json_data.getString("commented_by"), json_data.getString("ticket_creation")));

            }
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
        return users;
    }

    public class DataModel {

        String comment;
        String commentedBy;
        String ticketCreation;


        public DataModel(String comment, String commentedBy, String ticketCreation) {
            this.comment = comment;
            this.commentedBy = commentedBy;
            this.ticketCreation = ticketCreation;
        }

        public String getComment() {
            return comment;
        }

        public String getCommentedBy() {
            return commentedBy;
        }

        public String getTicketCreation() {
            return ticketCreation;
        }

    }


    public class CustomAdapter extends ArrayAdapter<DataModel> {

        private ArrayList<DataModel> dataSet;
        Context mContext;

        // View lookup cache
        private class ViewHolder {
            TextView created_by;
            TextView comment;
            TextView ticket_creation;
        }

        public CustomAdapter(ArrayList<DataModel> data, Context context) {
            super(context, R.layout.support_comment_list_items, data);
            this.dataSet = data;
            this.mContext = context;
        }

        private int lastPosition = -1;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            DataModel dataModel = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            CustomAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

            final View result;

            if (convertView == null) {

                viewHolder = new CustomAdapter.ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.support_comment_list_items, parent, false);
                viewHolder.comment = (TextView) convertView.findViewById(R.id.comment);
                viewHolder.created_by = (TextView) convertView.findViewById(R.id.created_by);
                viewHolder.ticket_creation = (TextView) convertView.findViewById(R.id.created_date);

                result = convertView;

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (CustomAdapter.ViewHolder) convertView.getTag();
                result = convertView;
            }

            Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
            result.startAnimation(animation);
            lastPosition = position;

            viewHolder.created_by.setText(dataModel.getCommentedBy());
            viewHolder.comment.setText(dataModel.getComment());
            viewHolder.ticket_creation.setText(dataModel.getTicketCreation());

            return convertView;
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
                AlertDialog.Builder builder = new AlertDialog.Builder(SupportComments.this);
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
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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