package com.faast.mobile.apps;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class DetailedUsageFragment extends Fragment {
    String detailedusagereport,UserName;
    String data = "";
    public TableLayout tl,table_header,table_footer;
    TableRow tr,tr_header,tr_footer;
    TextView dateheader,sessionTimeHeader,consumedDataHeader;
    String total_usage;
    ProgressDialog loadingDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v= inflater.inflate(R.layout.detailed_usage_fragment, container, false);
        tl = (TableLayout) v.findViewById(R.id.maintable);
        table_header = (TableLayout) v.findViewById(R.id.header);
        table_footer = (TableLayout) v.findViewById(R.id.footer);
        tl = (TableLayout)v.findViewById(R.id.maintable);
        table_header = (TableLayout) v.findViewById(R.id.header);
        table_footer = (TableLayout)v.findViewById(R.id.footer);
        return v;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SharedPreferences Links =getActivity().getSharedPreferences("DatabaseLinks", Context.MODE_PRIVATE);

        detailedusagereport = Links.getString("detailedusage","");
        SharedPreferences myPrefs = getActivity().getSharedPreferences("contacts", Context.MODE_PRIVATE);

        UserName = myPrefs.getString("Username","");

        final GetDetailedUsageReport getdb = new GetDetailedUsageReport();
        new Thread(new Runnable() {
            public void run() {
                data = getdb.getdetailedusagereport(detailedusagereport, UserName);

                System.out.println(data);

                // here you check the value of getActivity() and break up if needed
                if (getActivity() != null)
                {
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            ArrayList<Users> users = parseJSON(data);
                            if (users.size() == 0) {
                                TextView t = (TextView) getView().findViewById(R.id.no_usage_textview);
                                t.setVisibility(View.VISIBLE);
                            } else {
                                addData(users);
                            }
                            System.out.println("Output:" + users);
                        }
                    });
            }

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
                user.setUsagestartdate(json_data.getString("start_date"));
                user.setUsagestopdate(json_data.getString("end_time"));
                user.setConsumeddata(json_data.getString("consumed_data"));
                user.setUsagesessiontime(json_data.getString("session_time"));
                user.setUsagegrandtotal(json_data.getString("grand_total_usage"));
                users.add(user);
            }

        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
        return users;
    }


    public void addData(ArrayList<Users> users) {

        addHeader();

        for (Iterator i = users.iterator(); i.hasNext(); ) {

            Users p = (Users) i.next();
            /** Create a TableRow dynamically **/
            tr = new TableRow(getContext());

            /** Creating a TextView to add to the row **/
            dateheader = new TextView(getContext());
            dateheader.setText(p.getUsagestartdate());

            dateheader.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            dateheader.setBackground(getResources().getDrawable(
                    R.drawable.grey_border));
            // InvNumTextView.setPadding(3, 3, 3, 3);
            dateheader.setGravity(Gravity.CENTER);
            LinearLayout Ll = new LinearLayout(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT);
            Ll.addView(dateheader, params);
            tr.addView((View) Ll); // Adding textView to tablerow.

            /** Creating Qty Button **/

            if(p.getUsagestopdate().equals("Online")) {

                SpannableStringBuilder builder = new SpannableStringBuilder();
                builder.append(p.getUsagesessiontime()).append(" ");
                builder.setSpan(new ImageSpan(getContext(), R.mipmap.green_dot16),
                        builder.length() - 1, builder.length(), 0);

                sessionTimeHeader = new TextView(getContext());
                sessionTimeHeader.setText(builder);
//                sessionTimeHeader.setCompoundDrawablesWithIntrinsicBounds(
//                        R.drawable.green_dot16, 0, 0, 0);

                sessionTimeHeader.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                sessionTimeHeader.setBackground(getResources().getDrawable(
                        R.drawable.grey_border));
                sessionTimeHeader.setGravity(Gravity.CENTER);
                Ll = new LinearLayout(getContext());
                params = new LinearLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT);
                Ll.addView(sessionTimeHeader, params);
                tr.addView((View) Ll); // Adding textview to tablerow.
            }
            else{
                sessionTimeHeader = new TextView(getContext());
                sessionTimeHeader.setText(p.getUsagesessiontime());
                sessionTimeHeader.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                sessionTimeHeader.setBackground(getResources().getDrawable(
                        R.drawable.grey_border));
                sessionTimeHeader.setGravity(Gravity.CENTER);
                Ll = new LinearLayout(getContext());
                params = new LinearLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT);
                Ll.addView(sessionTimeHeader, params);
                tr.addView((View) Ll); // Adding textview to tablerow.
            }

            consumedDataHeader = new TextView(getContext());
            consumedDataHeader.setText(p.getConsumeddata());
            consumedDataHeader.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            consumedDataHeader.setBackground(getResources().getDrawable(
                    R.drawable.grey_border));
            consumedDataHeader.setGravity(Gravity.RIGHT);
            Ll = new LinearLayout(getContext());
            params = new LinearLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT);
            Ll.addView(consumedDataHeader, params);
            tr.addView((View) Ll); // Adding textview to tablerow.

            // Add the TableRow to the TableLayout
            tl.addView(tr, new TableLayout.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            total_usage = p.getUsagegrandtotal();
        }
        addFooter(total_usage);

    }

    private String getColoredSpanned(String text, String color) {
        String input = "<font color=" + color + ">" + text + "</font>";
        return input;
    }

    void addHeader(){
        /** Create a TableRow dynamically **/

        tr_header = new TableRow(getContext());

        /** Creating a TextView to add to the row **/
        dateheader = new TextView(getContext());
        dateheader.setText("   Date");
        dateheader.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        dateheader.setBackgroundColor(Color.parseColor("#00b1ba"));
        dateheader.setTextColor(Color.parseColor("#FFFFFF"));
        dateheader.setGravity(Gravity.CENTER);
        dateheader.setPadding(7, 7, 7, 7);
        LinearLayout Ll = new LinearLayout(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        Ll.addView(dateheader, params);
        tr_header.addView((View) Ll); // Adding textView to tablerow.

        sessionTimeHeader = new TextView(getContext());
        sessionTimeHeader.setText("            Duration");
        sessionTimeHeader.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        sessionTimeHeader.setBackgroundColor(Color.parseColor("#00b1ba"));
        sessionTimeHeader.setTextColor(Color.parseColor("#FFFFFF"));

        sessionTimeHeader.setGravity(Gravity.CENTER);

        sessionTimeHeader.setPadding(7, 7, 7, 7);
        LinearLayout L3 = new LinearLayout(getContext());
        params = new LinearLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
        L3.addView(sessionTimeHeader, params);
        tr_header.addView((View) L3); // Adding textview to tablerow.

        consumedDataHeader = new TextView(getContext());
        consumedDataHeader.setText("Consumed");
        consumedDataHeader.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        consumedDataHeader.setBackgroundColor(Color.parseColor("#00b1ba"));
        consumedDataHeader.setTextColor(Color.parseColor("#FFFFFF"));

        consumedDataHeader.setPadding(7, 7, 7, 7);
        consumedDataHeader.setGravity(Gravity.RIGHT);

        Ll = new LinearLayout(getContext());
        params = new LinearLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
        Ll.addView(consumedDataHeader, params);
        tr_header.addView((View) Ll); // Adding textview to tablerow.

        // Add the TableRow to the TableLayout
        table_header.addView(tr_header, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

    }

    void addFooter(String totaldatausage){
        /** Create a TableRow dynamically **/
        tr_footer = new TableRow(getContext());

        /** Creating a TextView to add to the row **/
        dateheader = new TextView(getContext());
        dateheader.setText("");
        dateheader.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        dateheader.setPadding(5, 5, 5, 5);

        dateheader.setGravity(Gravity.CENTER);
        dateheader.setBackgroundColor(Color.parseColor("#00b1ba"));

        LinearLayout Ll = new LinearLayout(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT);

        Ll.addView(dateheader,params);
        tr_footer.addView((View)Ll); // Adding textView to tablerow.

        sessionTimeHeader = new TextView(getContext());
        sessionTimeHeader.setText("Total");
        sessionTimeHeader.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        sessionTimeHeader.setBackgroundColor(Color.parseColor("#00b1ba"));
        sessionTimeHeader.setTextColor(Color.parseColor("#FFFFFF"));
        sessionTimeHeader.setPadding(5, 5, 5, 5);

        sessionTimeHeader.setGravity(Gravity.RIGHT);

        LinearLayout L3 = new LinearLayout(getContext());
        params = new LinearLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);

        L3.addView(sessionTimeHeader,params);
        tr_footer.addView((View)L3); // Adding textview to tablerow.

        // Add the TableRow to the TableLayout

        consumedDataHeader = new TextView(getContext());
        consumedDataHeader.setText(totaldatausage);
        consumedDataHeader.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        consumedDataHeader.setBackgroundColor(Color.parseColor("#00b1ba"));
        consumedDataHeader.setTextColor(Color.parseColor("#FFFFFF"));

        consumedDataHeader.setPadding(5, 5, 5, 5);
        consumedDataHeader.setGravity(Gravity.RIGHT);
        Ll = new LinearLayout(getContext());
        params = new LinearLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);

        Ll.addView(consumedDataHeader,params);
        tr_footer.addView((View)Ll); // Adding textview to tablerow.

        // Add the TableRow to the TableLayout
        table_footer.addView(tr_footer, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
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


//    @Override
//    public void onPause() {
//        super.onPause();
//        getActivity().unregisterReceiver(mConnReceiver);
//        if(isApplicationSentToBackground(getContext()))
//        {
//            getActivity().finish();
//        }
//    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        getActivity().unregisterReceiver(mConnReceiver);
    }


    @Override
    public void onResume() {
        super.onResume();
        getContext().registerReceiver(this.mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
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
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                // Set the Alert Dialog Message
                builder.setMessage("Internet Connection Required")
                        .setCancelable(false)
                        .setPositiveButton("Retry",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        // Restart the Activity
//                                        registerReceiver(mConnReceiver,
//                                                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
                                        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mConnReceiver,
                                                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    };

}