package com.faast.mobile.apps;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

public class UsageGraphFragment extends Fragment {

    String data = "";
    BarChart chart;
    ArrayList<BarEntry> BARENTRY;
    ArrayList<String> BarEntryLabels;
    BarDataSet Bardataset;
    BarData BARDATA;
    ArrayList<String> monthlist = new ArrayList<String>();

    ArrayList<String> datalist = new ArrayList<String>();
    String UserName;
    String usageReportURL;
    String current_month_year, chart_last_month;
    ArrayList<Users> users;
    ProgressDialog loadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.usage_graph_fragment, container, false);
        chart = (BarChart) v.findViewById(R.id.chart1);
        loadingDialog = createProgressDialog(getContext());
        loadingDialog.show();
        return v;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SharedPreferences myPrefs =getActivity().getSharedPreferences("contacts", Context.MODE_PRIVATE);

        UserName = myPrefs.getString("Username","");

        SharedPreferences Links =getActivity().getSharedPreferences("DatabaseLinks", Context.MODE_PRIVATE);

        usageReportURL = Links.getString("usagereport","");

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        if(month<10){
            current_month_year = year+"-0"+month;
        }
        else{
            current_month_year = year+"-"+month;
        }

        final GetUsageReport getdb = new GetUsageReport();
        new Thread(new Runnable() {
            public void run() {
                data = getdb.getDataFromDb(UserName,usageReportURL);
                System.out.println(data);

                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            ArrayList<Users> users = parseJSON(data);
                            addData(users);
                        }
                    });
                }

            }
        }).start();
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
                                        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mConnReceiver,
                                                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    };

    public ArrayList<Users> parseJSON(String result) {
        ArrayList<Users> users = new ArrayList<Users>();
        try {
            JSONArray jArray = new JSONArray(result);
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json_data = jArray.getJSONObject(i);
                Users user = new Users();
                user.setMonth(json_data.getString("month"));
                user.setData(json_data.getString("data"));
                users.add(user);
            }
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
        return users;
    }

    public void addData(ArrayList<Users> users) {

        for (Iterator i = users.iterator(); i.hasNext(); ) {
            Users p = (Users) i.next();
            monthlist.add(p.getMonth()); //add to arraylist
            chart_last_month = p.getMonth();
            datalist.add(p.getData()); //add to arraylist
        }
        System.out.println(monthlist);
        System.out.println(datalist);
        int i = monthlist.size();
        System.out.println(i);

        BARENTRY = new ArrayList<>();

        BarEntryLabels = new ArrayList<String>();

        AddValuesToBARENTRY();

        AddValuesToBarEntryLabels();

        Bardataset = new BarDataSet(BARENTRY, "MONTHS,DATA IN GB");

        BARDATA = new BarData(BarEntryLabels, Bardataset);
        Bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        Bardataset.setBarSpacePercent(50f);
        chart.setData(BARDATA);

        chart.animateY(3000);
        chart.setDescription("");
        chart.getXAxis().setTextSize(10f);
        Bardataset.setValueTextSize(10f);
        chart.getViewPortHandler().setMaximumScaleX(2f);
        chart.getAxisLeft().setStartAtZero(true);
        chart.getAxisRight().setStartAtZero(true);
        chart.setAutoScaleMinMaxEnabled(!chart.isAutoScaleMinMaxEnabled());
//        chart.notifyDataSetChanged();
        // chart.getXAxis().setDrawGridLines(true);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setDrawGridLines(false);
//
//        yAxis = chart.getAxisRight();
//        yAxis.setDrawGridLines(false);
        if(datalist.size() == 1)
        {
            Bardataset.setBarSpacePercent(90f);

        }
        else if(datalist.size() == 2)
        {
            Bardataset.setBarSpacePercent(70f);

        }
        else if(datalist.size() == 3)
        {
            Bardataset.setBarSpacePercent(50f);

        }
        else
        {
            Bardataset.setBarSpacePercent(30f);
        }
        loadingDialog.dismiss();

    }

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

    public void AddValuesToBARENTRY() {
        for(int i = 0;i < datalist.size();i++) {
            Float f = Float.parseFloat(datalist.get(i));
            BARENTRY.add(new BarEntry(f, i));
        }
    }
    public void AddValuesToBarEntryLabels() {
        for(int i = 0;i < monthlist.size();i++) {
            BarEntryLabels.add(monthlist.get(i));
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
    @Override
    public void onSaveInstanceState(Bundle oldInstanceState) {
        super.onSaveInstanceState(oldInstanceState);
        oldInstanceState.clear();
    }
}