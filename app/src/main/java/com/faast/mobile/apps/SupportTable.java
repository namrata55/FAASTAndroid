package com.faast.mobile.apps;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class SupportTable extends Fragment {
    public TableLayout support_table_header,support_table_content;
    TableRow tr,tr_header,tr_header_content;
    TextView ticket_id_header,subject_header1,status_header;
    String UserName,supportTableURL;
    String data = "";
    String ticket_ids[],ticket_status[],ticket_subjects[],tickets_comments[],tickets_creations[];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.support_table, container, false);
        support_table_content = (TableLayout) v.findViewById(R.id.maintable);
        support_table_header = (TableLayout) v.findViewById(R.id.header);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SharedPreferences Links =getActivity().getSharedPreferences("DatabaseLinks", Context.MODE_PRIVATE);

        supportTableURL = Links.getString("supportableurl","");

        SharedPreferences myPrefs = getActivity().getSharedPreferences("contacts", Context.MODE_PRIVATE);

        UserName = myPrefs.getString("Username","");

        final GetSupportTableDetails getdb = new GetSupportTableDetails();
        new Thread(new Runnable() {
            public void run() {
                data = getdb.gerSupportTableDetails(supportTableURL,UserName);

                System.out.println(data);

                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        ArrayList<Users> users = parseJSON(data);
                        if(users.size()==0){
                            TextView tickets_status_table_textview = (TextView) getView().findViewById(R.id.no_tickets);
                            tickets_status_table_textview.setVisibility(View.VISIBLE);

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

        ticket_ids = new String[users.size()+1];
        ticket_status = new String[users.size()+1];
        ticket_subjects = new String[users.size()+1];
        tickets_comments = new String[users.size()+1];
        tickets_creations = new String[users.size()+1];
        addHeader();
        int j;
        j = 1;
        for (Iterator i = users.iterator(); i.hasNext(); ) {
            /** Create a TableRow dynamically **/
            Users p = (Users) i.next();
            ticket_ids [j] = p.getTicketId();
            ticket_status [j] = p.getTicketStatus();
            ticket_subjects [j] = p.getTicketSubject();
            tickets_comments [j] = p.getTicketComment();
            tickets_creations [j] = p.getTicketCreation();

            tr_header_content = new TableRow(getContext());

            /** Creating a TextView to add to the row **/
            ticket_id_header = new TextView(getContext());
            ticket_id_header.setText(p.getTicketId());

            ticket_id_header.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            ticket_id_header.setBackground(getResources().getDrawable(
                    R.drawable.grey_border));
            // InvNumTextView.setPadding(3, 3, 3, 3);
            ticket_id_header.setGravity(Gravity.CENTER);
            LinearLayout Ll = new LinearLayout(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT);
            Ll.addView(ticket_id_header, params);
            tr_header_content.addView((View) Ll); // Adding textView to tablerow.

            subject_header1 = new TextView(getContext());
            subject_header1.setText(p.getTicketSubject());
            subject_header1.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));
            subject_header1.setBackground(getResources().getDrawable(
                    R.drawable.grey_border));
            subject_header1.setGravity(Gravity.CENTER);

            LinearLayout L2 = new LinearLayout(getContext());
            LinearLayout.LayoutParams  params2 = new LinearLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT);
            L2.addView(subject_header1, params2);
            tr_header_content.addView((View) L2); // Adding textview to tablerow.

            status_header = new TextView(getContext());
            status_header.setText(p.getTicketStatus());
            status_header.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));
            status_header.setBackground(getResources().getDrawable(
                    R.drawable.grey_border));
            status_header.setGravity(Gravity.CENTER);

            LinearLayout L3 = new LinearLayout(getContext());
            LinearLayout.LayoutParams  params3 = new LinearLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT);
            L3.addView(status_header, params3);
            tr_header_content.addView((View) L3); // Adding textview to tablerow.
            tr_header_content.setId(j);
            tr_header_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    TableRow t = (TableRow) v;
//                    TextView firstTextView = (TextView) t.getChildAt(0);
                    String clicked_ticket_id = ticket_ids[v.getId()];
                    String clicked_ticket_status = ticket_status[v.getId()];
                    String clicked_ticket_comment = tickets_comments[v.getId()];
                    String clicked_ticket_creation = tickets_creations[v.getId()];
                    String clicked_ticket_subject = ticket_subjects[v.getId()];

                    Intent i = new Intent(getContext(),TickertDetails.class);
                    i.putExtra("ticket_id",clicked_ticket_id);
                    i.putExtra("ticket_subject",clicked_ticket_subject);
                    i.putExtra("ticket_status",clicked_ticket_status);
                    i.putExtra("ticket_comment",clicked_ticket_comment);
                    i.putExtra("ticket_creation",clicked_ticket_creation);
                    startActivity(i);

                    Toast.makeText(getContext(),"Id==="+clicked_ticket_id,Toast.LENGTH_SHORT).show();
                }
            });
            // Add the TableRow to the TableLayout
            support_table_content.addView(tr_header_content, new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.FILL_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));
            j++;
        }

    }

    void addHeader(){
        /** Create a TableRow dynamically **/

        tr_header = new TableRow(getContext());

        ticket_id_header = new TextView(getContext());
        ticket_id_header.setText("Ticket Id");
        ticket_id_header.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        ticket_id_header.setBackgroundColor(Color.parseColor("#00b1ba"));
        ticket_id_header.setTextColor(Color.parseColor("#FFFFFF"));
        ticket_id_header.setGravity(Gravity.LEFT);

        LinearLayout L3 = new LinearLayout(getContext());
        LinearLayout.LayoutParams  params1 = new LinearLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
        L3.addView(ticket_id_header, params1);
        tr_header.addView((View) L3); // Adding textview to tablerow.

        subject_header1 = new TextView(getContext());
        subject_header1.setText("Subject");
        subject_header1.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        subject_header1.setBackgroundColor(Color.parseColor("#00b1ba"));
        subject_header1.setTextColor(Color.parseColor("#FFFFFF"));

        subject_header1.setGravity(Gravity.CENTER);

        LinearLayout Ll = new LinearLayout(getContext());
        LinearLayout.LayoutParams  params = new LinearLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
        Ll.addView(subject_header1, params);
        tr_header.addView((View) Ll); // Adding textview to tablerow.

        status_header = new TextView(getContext());
        status_header.setText("Status");
        status_header.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        status_header.setBackgroundColor(Color.parseColor("#00b1ba"));
        status_header.setTextColor(Color.parseColor("#FFFFFF"));

        status_header.setGravity(Gravity.RIGHT);

        LinearLayout L2 = new LinearLayout(getContext());
        LinearLayout.LayoutParams  params2 = new LinearLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
        L2.addView(status_header, params2);
        tr_header.addView((View) L2); // Adding textview to tablerow.

        // Add the TableRow to the TableLayout
        support_table_header.addView(tr_header, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

    }

}
