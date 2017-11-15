package com.faast.mobile.apps;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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

public class SupportTicketsTable extends AppCompatActivity {
    ListView listView;

    String UserName,supportTableURL;
    String data = "";
    private static CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.support_tickets_table);

        Window window = SupportTicketsTable.this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(SupportTicketsTable.this.getResources().getColor(R.color.my_statusbar_color));
        }

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00ba30")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Ticket List");

        listView = (ListView) findViewById(R.id.support_ticket_list);

        SharedPreferences Links =this.getSharedPreferences("DatabaseLinks", Context.MODE_PRIVATE);

        supportTableURL = Links.getString("supportableurl","");

        SharedPreferences myPrefs = this.getSharedPreferences("contacts", Context.MODE_PRIVATE);

        UserName = myPrefs.getString("Username","");

        final GetSupportTableDetails getdb = new GetSupportTableDetails();
        new Thread(new Runnable() {
            public void run() {
                data = getdb.gerSupportTableDetails(supportTableURL,UserName);

                System.out.println(data);

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        ArrayList<Users> users = parseJSON(data);
                        if(users.size()==0){
                            TextView tickets_status_table_textview = (TextView) findViewById(R.id.no_tickets_textview);
                            tickets_status_table_textview.setVisibility(View.VISIBLE);
                            listView.setVisibility(View.INVISIBLE);
                        }
                        else {
                            addData(users);
                        }
                        System.out.println("Output:"+users);
                    }
                });
            }
        }).start();
    }

    public ArrayList<Users> parseJSON(String result) {
        ArrayList<Users> users = new ArrayList<Users>();

        try {
            JSONArray jArray = new JSONArray(result);
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json_data = jArray.getJSONObject(i);
                Users user = new Users();
                user.setTicketId(json_data.getString("ticket_id"));
                user.setSupportTicketId(json_data.getString("id"));
                user.setTicketSubject(json_data.getString("ticket_subject"));
                user.setTicketStatus(json_data.getString("ticket_status"));
                user.setTicketCommemt(json_data.getString("ticket_comment"));
                user.setTicketCreation(json_data.getString("ticket_creation"));

                users.add(user);
            }

        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
        return users;
    }
    public void addData(ArrayList<Users> users) {
        adapter = new CustomAdapter(users,SupportTicketsTable.this);
        listView.setAdapter(adapter);
    }

    public class CustomAdapter extends ArrayAdapter<Users> implements View.OnClickListener{

        public ArrayList<Users> dataSet;
        Context mContext;

        // View lookup cache
        public class ViewHolder {
            public TextView ticketIdTextview;
            public TextView ticketSubjectTextview;
            public TextView info;
            public TextView ticketStatusTextview;
            public TextView ticketCreateDateTextview;
        }

        public CustomAdapter(ArrayList<Users> data, Context context) {
            super(context, R.layout.support_tickets_list_items, data);
            this.dataSet = data;
            this.mContext = context;
        }

        @Override
        public void onClick(View v) {

            int position = (Integer) v.getTag();
            Object object = getItem(position);
            final Users dataModel = (Users) object;

            switch (v.getId())
            {
                case R.id.click1:

//                    Toast toast = Toast.makeText(getContext(),"Clicked"+dataModel.getSupportTicketId(), Toast.LENGTH_SHORT);
//                    toast.setGravity(Gravity.CENTER, 0, 0);
//                    toast.show();
                    Intent i = new Intent(getContext(),SupportComments.class);
                    i.putExtra("ticket_id",dataModel.getTicketId());
                    i.putExtra("support_ticket_id",dataModel.getSupportTicketId());
                    i.putExtra("ticket_subject",dataModel.getTicketSubject());
                    i.putExtra("ticket_status",dataModel.getTicketStatus());
                    i.putExtra("ticket_comment",dataModel.getTicketComment());
                    i.putExtra("ticket_creation",dataModel.getTicketCreation());
                    startActivity(i);
            }
        }

        private int lastPosition = -1;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Users dataModel = getItem(position);

            CustomAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

            final View result;

            if (convertView == null) {

                viewHolder = new CustomAdapter.ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.support_tickets_list_items, parent, false);
                viewHolder.ticketIdTextview = (TextView) convertView.findViewById(R.id.ticket_id);
                viewHolder.ticketStatusTextview = (TextView) convertView.findViewById(R.id.status);
                viewHolder.ticketSubjectTextview = (TextView) convertView.findViewById(R.id.subject1);
                viewHolder.ticketCreateDateTextview = (TextView) convertView.findViewById(R.id.create_date);
                viewHolder.info = (TextView) convertView.findViewById(R.id.click1);

                result=convertView;

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (CustomAdapter.ViewHolder) convertView.getTag();
                result = convertView;
            }

            Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
            result.startAnimation(animation);
            lastPosition = position;

            viewHolder.ticketIdTextview.setText("");
            viewHolder.ticketSubjectTextview.setText("");
            viewHolder.ticketStatusTextview.setText("");
            viewHolder.ticketCreateDateTextview.setText("");

            SpannableString id_span=  new SpannableString("Ticket Id : ");
            id_span.setSpan(new StyleSpan(Typeface.BOLD), 0, id_span.length(), 0);
            viewHolder.ticketIdTextview.append(id_span);
            viewHolder.ticketIdTextview.append(dataModel.getTicketId());

            SpannableString status_span=  new SpannableString("Status : ");
            status_span.setSpan(new StyleSpan(Typeface.BOLD), 0, status_span.length(), 0);
            viewHolder.ticketStatusTextview.append(status_span);
            viewHolder.ticketStatusTextview.append(dataModel.getTicketStatus());

            SpannableString subject_span =  new SpannableString("Subject : ");
            subject_span.setSpan(new StyleSpan(Typeface.BOLD), 0, subject_span.length(), 0);
            viewHolder.ticketSubjectTextview.append(subject_span);
            viewHolder.ticketSubjectTextview.append(dataModel.getTicketSubject());

            SpannableString create_date_span =  new SpannableString("Create date : ");
            create_date_span.setSpan(new StyleSpan(Typeface.BOLD), 0, create_date_span.length(), 0);
            viewHolder.ticketCreateDateTextview.append(create_date_span);
            viewHolder.ticketCreateDateTextview.append(dataModel.getTicketCreation());
            System.out.print("date-----"+dataModel.getTicketCreation());
            SpannableString content = new SpannableString("View");
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            viewHolder.info.setText(content);
            viewHolder.info.setOnClickListener(this);
            viewHolder.info.setTag(position);

            return convertView;
        }
    }

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
                AlertDialog.Builder builder = new AlertDialog.Builder(SupportTicketsTable.this);
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
