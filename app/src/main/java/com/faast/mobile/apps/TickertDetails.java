package com.faast.mobile.apps;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

public class TickertDetails extends AppCompatActivity {

    String ticketId,ticketStatus,ticketSubject,ticketComment,ticketCreation;
    TextView ticketIdtextView,ticketStatustextView,ticketSubjecttextView,ticketCommenttextView,ticketCreationtextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tickert_details);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00ba30")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Ticket Details");
        Window window = TickertDetails.this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(TickertDetails.this.getResources().getColor(R.color.my_statusbar_color));
        }

        ticketIdtextView = (TextView) findViewById(R.id.ticket_id);
        ticketStatustextView = (TextView) findViewById(R.id.ticket_status);
        ticketSubjecttextView = (TextView) findViewById(R.id.ticket_subject);
        ticketCommenttextView = (TextView) findViewById(R.id.ticket_comment);
        ticketCreationtextView = (TextView) findViewById(R.id.ticket_create_date);

        Intent i = getIntent();
        ticketId = i.getStringExtra("ticket_id");
        ticketStatus = i.getStringExtra("ticket_status");
        ticketSubject = i.getStringExtra("ticket_subject");
        ticketComment = i.getStringExtra("ticket_comment");
        ticketCreation = i.getStringExtra("ticket_creation");
        ticketIdtextView.setText(ticketId);
        ticketStatustextView.setText(ticketStatus);
        ticketSubjecttextView.setText(ticketSubject);
        ticketCommenttextView.setText(ticketComment);
        ticketCreationtextView.setText(ticketCreation);

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
    protected void onSaveInstanceState(Bundle oldInstanceState) {
        super.onSaveInstanceState(oldInstanceState);
        oldInstanceState.clear();
    }
}
