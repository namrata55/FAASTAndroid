package com.faast.mobile.apps;

import android.app.Activity;
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
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.razorpay.Checkout;

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
import java.text.DateFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class InvoiceForDeactivatedCancelledCustomers extends AppCompatActivity {

    String data = "";
    public TableLayout tl;
    TableRow tr;
    TextView InvNumTextView;
    Double TotalBill=0.0;
    Button inv_pay_button;
    String FirstNameP,emailP,mobileP;

    String firstName,mobile,email,UserName;
    Integer Final_Amount;
    String s;
    String value,updateinv,updatewallet;;
    String razorpayPaymentID1;

    String  RazorpayKeyId,RazorpayKeySecret;

    Integer i,i1,uv,uv1;
    Integer amt1;
    String invoiceURL;
    String rzChargeURL;
    String updateInvoiceURL,reactivationURL;
    String AccountStatus;

    LinearLayout tv;

    private static final String TAG_RESULT = "result";
    private static final String TAG_PRODUCTS = "output";
    private static final String TAG_WALLET_BALANCE = "balance";
    String wallet_balance;
    Double wallet_amount;
    Double outstanding_amount;
    CheckBox availaibleWalletBalanceCheckBox;
    TextView payBalanceTextView;
    String totalBillString;
    TextView walletBalanceTextView;
    String walletBalanceString;
    NumberFormat formatter2;
    String zero_in_rupee;
    String invnums;
    LinearLayout mainLayout;
    String updateWalletURL,walletAmountUrl;
    JSONParser jsonParser1 = new JSONParser();
    ArrayList<HashMap<String, String>> profileList1 = new ArrayList<HashMap<String, String>>();
    JSONArray matchFixture1 = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invoices);
        inv_pay_button = (Button)findViewById(R.id.invoice_pay);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00ba30")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Invoices");

        Window window = InvoiceForDeactivatedCancelledCustomers.this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(InvoiceForDeactivatedCancelledCustomers.this.getResources().getColor(R.color.my_statusbar_color));
        }

        Intent i = getIntent();
        AccountStatus = i.getStringExtra("account_status");
        firstName = i.getStringExtra("firstname");
        mobile = i.getStringExtra("mobile");
        email = i.getStringExtra("email");

        SharedPreferences Links = this.getSharedPreferences("DatabaseLinks", MODE_PRIVATE);
        invoiceURL = Links.getString("unpaidinvoices","");
        rzChargeURL = Links.getString("rzcharge","");
        updateInvoiceURL = Links.getString("updateinvoice","");
        reactivationURL = Links.getString("requestforreactivationurl","");
        walletAmountUrl = Links.getString("walletamount", "");
        updateWalletURL = Links.getString("updatewalletamount", "");

        SharedPreferences myPrefs = this.getSharedPreferences("contacts", MODE_PRIVATE);
        UserName = myPrefs.getString("Username", "");

        tl = (TableLayout) findViewById(R.id.maintable);

        final GetInvoices getdb = new GetInvoices();
        new Thread(new Runnable() {
            public void run() {
                data = getdb.getDataFromDb(UserName,invoiceURL);

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        ArrayList<Users> users = parseJSON(data);
                        addData(users);
                        System.out.println("Output:"+users);
                    }
                });

            }
        }).start();
//        inv_pay_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Checkout.preload(getApplicationContext());
//
//                inv_pay_button.setEnabled(false);
//                inv_pay_button.setClickable(false);
//                inv_pay_button.setBackgroundColor(Color.parseColor("#7fd8dc"));
//
//                if(TotalBill > 0.0)
//                {
//                    Toast.makeText(InvoiceForDeactivatedCancelledCustomers.this,"Processing Payment, Please wait…",Toast.LENGTH_SHORT).show();
//                    startPayment(TotalBill);
//                }
//                else {
//
//                    Toast.makeText(InvoiceForDeactivatedCancelledCustomers.this,"Invalid Amount",Toast.LENGTH_LONG).show();
//                }
//            }
//        });

        inv_pay_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("invnums="+invnums);

                Checkout.preload(getApplicationContext());

                inv_pay_button.setEnabled(false);
                inv_pay_button.setClickable(false);
                inv_pay_button.setBackgroundColor(Color.parseColor("#7fd8dc"));

                if(mainLayout.getVisibility() == View.VISIBLE){
                    if (outstanding_amount > wallet_amount && ! availaibleWalletBalanceCheckBox.isChecked()){
                        //if bill amount is greater than wallet amount and user does not want to use wallet amount
                        startPayment(outstanding_amount);
                    }
                    else if (outstanding_amount > wallet_amount &&  availaibleWalletBalanceCheckBox.isChecked()) {
                        //if bill amount is greater than wallet amount and user wants to use wallet amount
                        Double amount_to_pay_by_razorpay = outstanding_amount - wallet_amount;
                        startPayment(amount_to_pay_by_razorpay);
                    }
                    else if (outstanding_amount < wallet_amount &&  !availaibleWalletBalanceCheckBox.isChecked()) {
                        //if bill amount is less than wallet amount and user  does not want to use wallet amount
                        startPayment(outstanding_amount);
                    }
                    else if (outstanding_amount < wallet_amount &&  availaibleWalletBalanceCheckBox.isChecked()) {
                        //if bill amount is less than wallet amount and user wants to use wallet amount

                        //deduct amount form wallet
                        final UpdateInvoice ui = new UpdateInvoice();

                        new Thread(new Runnable() {
                            public void run() {
                                updateinv = ui.updateInvoice(UserName, "wallet", updateInvoiceURL , "Wallet");

                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {

                                        if (updateinv.equals("success")) {

                                            final UpdateWallet uw = new UpdateWallet();

                                            new Thread(new Runnable() {
                                                public void run() {

                                                    System.out.println("updateWalletURL= "+updateWalletURL);
                                                    System.out.println("totalBillString= "+totalBillString);
                                                    System.out.println("walletBalanceString= "+walletBalanceString);
                                                    System.out.println("invnums= "+invnums);

                                                    Double walletBalance = wallet_amount - outstanding_amount;
                                                    updatewallet = uw.updateWallet(UserName, "throughwallet", updateWalletURL, "0.00",  String.valueOf(TotalBill), String.valueOf(walletBalance), invnums);

                                                    runOnUiThread(new Runnable() {

                                                        @Override
                                                        public void run() {

                                                            System.out.println("Update inv result= "+updateinv);

                                                            if (updatewallet.equals("success")) {
                                                                Toast.makeText(getApplicationContext(), "Payment Successful", Toast.LENGTH_LONG).show();
                                                                Intent i = new Intent(getApplicationContext(), HomeInternetStatus.class);
                                                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                finish();
                                                                finishAffinity();
                                                                startActivity(i);

                                                            } else {
                                                                Toast.makeText(getApplicationContext(), "Payment Unsuccessful", Toast.LENGTH_LONG).show();
                                                                Intent i = new Intent(getApplicationContext(), HomeInternetStatus.class);
                                                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                finish();
                                                                finishAffinity();
                                                                startActivity(i);
                                                            }
                                                        }
                                                    });
                                                }
                                            }).start();

                                        } else {
                                            Toast.makeText(getApplicationContext(), "Payment Unsuccessful", Toast.LENGTH_LONG).show();
                                            Intent i = new Intent(getApplicationContext(), HomeInternetStatus.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            finish();
                                            finishAffinity();
                                            startActivity(i);
                                        }
                                    }
                                });
                            }
                        }).start();
                    }
                    else {
                        Toast.makeText(InvoiceForDeactivatedCancelledCustomers.this, "Invalid Amount", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    if (TotalBill > 0.0) {
                        startPayment(TotalBill);
                    }
                    else{
                        Toast.makeText(InvoiceForDeactivatedCancelledCustomers.this, "Invalid Amount", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

        availaibleWalletBalanceCheckBox = (CheckBox) findViewById(R.id.use_availaible_wallet_balance);

        payBalanceTextView = (TextView) findViewById(R.id.pay_balance);
        walletBalanceTextView = (TextView) findViewById(R.id.wallet_Amount);

        formatter2 = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        zero_in_rupee = formatter2.format(0.00);

        availaibleWalletBalanceCheckBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (((CheckBox) v).isChecked()) {

                    if (outstanding_amount < wallet_amount) {
                        payBalanceTextView.setText(zero_in_rupee);
                        walletBalanceTextView.setText(totalBillString);
                    } else {
                        Double finalAmountToBePaid = outstanding_amount - wallet_amount;
                        NumberFormat formatter3 = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
                        String finalAmountToBePaidString = formatter3.format(finalAmountToBePaid);
                        payBalanceTextView.setText(finalAmountToBePaidString);
                        walletBalanceTextView.setText(walletBalanceString);
                    }
                } else {
                    walletBalanceTextView.setText(zero_in_rupee);
                    payBalanceTextView.setText(totalBillString);
                }
            }
        });

    }
    //-----------------------------PARSE JSON----------------------------------

    public ArrayList<Users> parseJSON(String result) {
        ArrayList<Users> users = new ArrayList<Users>();
        try {
            JSONArray jArray = new JSONArray(result);
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json_data = jArray.getJSONObject(i);
                Users user = new Users();
                user.setInvNum(json_data.getString("invnum"));
                user.setInvDate(json_data.getString("date"));
                user.setInvPrice(json_data.getString("total"));
                user.setInvDueDate(json_data.getString("paymentopt"));


                users.add(user);
            }
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
        return users;
    }

    void addHeader(){
        /** Create a TableRow dynamically **/

        tr = new TableRow(this);

        /** Creating a TextView to add to the row **/
        InvNumTextView = new TextView(this);
        InvNumTextView.setText("Inv Num");
        InvNumTextView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        InvNumTextView.setBackgroundColor(Color.parseColor("#00b1ba"));
        InvNumTextView.setTextColor(Color.parseColor("#FFFFFF"));
        InvNumTextView.setGravity(Gravity.CENTER);
        InvNumTextView.setPadding(7,7,7,7);
        InvNumTextView.setGravity(Gravity.CENTER);
        InvNumTextView.setWidth(30);
        LinearLayout Ll = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        Ll.addView(InvNumTextView,params);
        tr.addView((View)Ll); // Adding textView to tablerow.

        TextView id = new TextView(this);
        id.setText("Inv Date");
        id.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        id.setBackgroundColor(Color.parseColor("#00b1ba"));
        id.setTextColor(Color.parseColor("#FFFFFF"));

        id.setGravity(Gravity.CENTER);
        id.setWidth(30);

        id.setPadding(7,7,7,7);;
        id.setGravity(Gravity.CENTER);

        LinearLayout L3 = new LinearLayout(this);
        params = new LinearLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
        L3.addView(id,params);
        tr.addView((View)L3); // Adding textview to tablerow.

        TextView duedate = new TextView(this);
        duedate.setText("Due Date");
        duedate.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        duedate.setBackgroundColor(Color.parseColor("#00b1ba"));
        duedate.setTextColor(Color.parseColor("#FFFFFF"));

        duedate.setPadding(7,7,7,7);
        duedate.setGravity(Gravity.CENTER);

        Ll = new LinearLayout(this);
        params = new LinearLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
        Ll.addView(duedate,params);
        tr.addView((View)Ll); // Adding textview to tablerow.

        // Add the TableRow to the TableLayout

        /** Creating Qty Button **/
        TextView balance = new TextView(this);
        balance.setText("Amount");
        balance.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        balance.setBackgroundColor(Color.parseColor("#00b1ba"));
        balance.setTextColor(Color.parseColor("#FFFFFF"));

        balance.setPadding(7,7,7,7);
        balance.setGravity(Gravity.CENTER);

        Ll = new LinearLayout(this);
        params = new LinearLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
        Ll.addView(balance,params);
        tr.addView((View)Ll); // Adding textview to tablerow.

        // Add the TableRow to the TableLayout
        tl.addView(tr, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
    }

    void addFooter(Double totalbill){
        /** Create a TableRow dynamically **/
        tr = new TableRow(this);

        /** Creating a TextView to add to the row **/
        InvNumTextView = new TextView(this);
        InvNumTextView.setText("");
        InvNumTextView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        InvNumTextView.setPadding(5, 5, 5, 5);

        InvNumTextView.setGravity(Gravity.CENTER);
        InvNumTextView.setBackgroundColor(Color.parseColor("#00b1ba"));

        LinearLayout Ll = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT);

        Ll.addView(InvNumTextView,params);
        tr.addView((View)Ll); // Adding textView to tablerow.

        TextView id = new TextView(this);
        id.setText("");
        id.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        id.setBackgroundColor(Color.parseColor("#00b1ba"));
        id.setPadding(5, 5, 5, 5);

        id.setGravity(Gravity.CENTER);

        LinearLayout L3 = new LinearLayout(this);
        params = new LinearLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);

        L3.addView(id,params);
        tr.addView((View)L3); // Adding textview to tablerow.

        TextView duedate = new TextView(this);
        duedate.setText("Total Due");
        duedate.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        duedate.setBackgroundColor(Color.parseColor("#00b1ba"));
        duedate.setTextColor(Color.parseColor("#FFFFFF"));

        duedate.setPadding(5, 5, 5, 5);
        duedate.setGravity(Gravity.CENTER);
        Ll = new LinearLayout(this);
        params = new LinearLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
        Ll.addView(duedate,params);
        tr.addView((View)Ll); // Adding textview to tablerow.

        // Add the TableRow to the TableLayout

        /** Creating Qty Button **/
        Format formatter = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
        String moneyString = formatter.format(totalbill);

        TextView balance = new TextView(this);
        balance.setText(moneyString+"  ");
        balance.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        balance.setBackgroundColor(Color.parseColor("#00b1ba"));
        balance.setTextColor(Color.parseColor("#FFFFFF"));

        balance.setPadding(5, 5, 5, 5);
        balance.setGravity(Gravity.RIGHT);
        Ll = new LinearLayout(this);
        params = new LinearLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);

        Ll.addView(balance,params);
        tr.addView((View)Ll); // Adding textview to tablerow.

        // Add the TableRow to the TableLayout
        tl.addView(tr, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
    }

    public void addData(ArrayList<Users> users) {

        addHeader();

        for (Iterator i = users.iterator(); i.hasNext(); ) {

            Users p = (Users) i.next();

            /** Create a TableRow dynamically **/
            tr = new TableRow(this);

            /** Creating a TextView to add to the row **/
            InvNumTextView = new TextView(this);
            InvNumTextView.setText(p.getInvNum());

            InvNumTextView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            InvNumTextView.setBackground(getResources().getDrawable(
                    R.drawable.grey_border));
            // InvNumTextView.setPadding(3, 3, 3, 3);
            InvNumTextView.setGravity(Gravity.CENTER);
            LinearLayout Ll = new LinearLayout(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT);
            Ll.addView(InvNumTextView, params);
            tr.addView((View) Ll); // Adding textView to tablerow.

            /** Creating Qty Button **/
            String date2 = p.getInvDate();
            String textdate = getconvertdate1(date2);
            TextView invdate = new TextView(this);
            invdate.setText(textdate);
            invdate.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            invdate.setBackground(getResources().getDrawable(
                    R.drawable.grey_border));
            invdate.setGravity(Gravity.CENTER);
            Ll = new LinearLayout(this);
            params = new LinearLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT);
            Ll.addView(invdate, params);
            tr.addView((View) Ll); // Adding textview to tablerow.

            String duedate1 = p.getInvDueDate();
            String duedate = getconvertdate1(duedate1);
            TextView invduedate = new TextView(this);
            invduedate.setText(duedate);
            invduedate.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            invduedate.setBackground(getResources().getDrawable(
                    R.drawable.grey_border));
            invduedate.setGravity(Gravity.CENTER);
            Ll = new LinearLayout(this);
            params = new LinearLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT);
            Ll.addView(invduedate, params);
            tr.addView((View) Ll); // Adding textview to tablerow.

            String price = p.getInvPrice();
            Double price_double=Double.parseDouble(price);
            TotalBill = TotalBill + price_double;
            Format formatter = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
            String moneyString = formatter.format(price_double);

            TextView invamount = new TextView(this);
            invamount.setText(moneyString+"  ");
            invamount.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            invamount.setBackground(getResources().getDrawable(
                    R.drawable.grey_border));
            invamount.setGravity(Gravity.RIGHT);
            Ll = new LinearLayout(this);
            params = new LinearLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT);
            Ll.addView(invamount, params);
            tr.addView((View) Ll); // Adding textview to tablerow.

            // Add the TableRow to the TableLayout
            tl.addView(tr, new TableLayout.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
        }
        addFooter(TotalBill);
        GetWallet getWallet = new GetWallet();
        getWallet.execute();
    }


    class GetWallet extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog1;
        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog1 = createProgressDialog(InvoiceForDeactivatedCancelledCustomers.this);
            pDialog1.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            String success;

            try {
//                Log.d("try", "in the try");
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                params.add(new BasicNameValuePair("username", UserName));
                JSONObject json = jsonParser1.makeHttpRequest(
                        walletAmountUrl, "POST", params);
                Log.d("jsonObject", "new json Object");

                matchFixture1 = json.getJSONArray(TAG_PRODUCTS);
                JSONObject c1 = matchFixture1.getJSONObject(0);
                success = c1.getString(TAG_RESULT);
                if (success.equals("1")) {

                    for (int i = 0; i < matchFixture1.length(); i++) {
//                        System.out.println(matchFixture1);

                        JSONObject c = matchFixture1.getJSONObject(i);

                        String wallet_balance = c.getString(TAG_WALLET_BALANCE);

                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_WALLET_BALANCE, wallet_balance);

                        // adding HashList to ArrayList
                        profileList1.add(map);
                    }
                } else if (success.equals("0")) {
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
//            System.out.print(result);
            if (pDialog1 != null && pDialog1.isShowing()) {
                pDialog1.dismiss();
            }

            for (HashMap<String, String> map : profileList1) {
                wallet_balance = map.get(TAG_WALLET_BALANCE);
            }
//            Log.i("Wallet balance==", wallet_balance);

            if (wallet_balance.equals("0.00")) {
                mainLayout.setVisibility(mainLayout.INVISIBLE);
            } else {

                wallet_amount = Double.valueOf(wallet_balance).doubleValue();
                NumberFormat formatter1 = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
                walletBalanceString = formatter1.format(wallet_amount);

                String availaibleBalanceString = "(Availaible Balance " + walletBalanceString + ")";
                TextView availaibleWalletBalanceTextView = (TextView) findViewById(R.id.availaible_wallet_balance);
                availaibleWalletBalanceTextView.setText(availaibleBalanceString);

                outstanding_amount = Double.valueOf(TotalBill).doubleValue();
                NumberFormat formatter2 = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
                totalBillString = formatter2.format(outstanding_amount);

//                System.out.println("wallet Balance String" + walletBalanceString);
//                System.out.println("Total bill " + TotalBill);

                TextView amountToBePaidTextView = (TextView) findViewById(R.id.amount_to_be_paid);
                amountToBePaidTextView.setText(totalBillString);

                if (outstanding_amount < wallet_amount) {
                    walletBalanceTextView.setText(totalBillString);
                    payBalanceTextView.setText(zero_in_rupee);
                } else {
                    walletBalanceTextView.setText(walletBalanceString);
                    Double finalAmountToBePaid = outstanding_amount - wallet_amount;
                    NumberFormat formatter3 = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
                    String finalAmountToBePaidString = formatter3.format(finalAmountToBePaid);
                    payBalanceTextView.setText(finalAmountToBePaidString);
                }


            }

        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        if (menuItem.getItemId() == android.R.id.home) {
            Intent i = new Intent(InvoiceForDeactivatedCancelledCustomers.this,Login.class);
            startActivity(i);
            return false;
        }
        else
        {
            onBackPressed();
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);

    }

    //-----------------------------DATE CONVERSION----------------------------------

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
                AlertDialog.Builder builder = new AlertDialog.Builder(InvoiceForDeactivatedCancelledCustomers.this);
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

    //-----------------------------RAZOR PAYMENT ----------------------------------

    public void startPayment(Double Amount){
        /**
         * Put your key id generated in Razorpay dashboard here
         */
        SharedPreferences myPrefs = this.getSharedPreferences("RazorpayKeysDetails", MODE_PRIVATE);
        RazorpayKeyId = myPrefs.getString("razorpaykeyid", "");
        RazorpayKeySecret = myPrefs.getString("razorpaykeysecret", "");

        Checkout razorpayCheckout = new Checkout();
        razorpayCheckout.setKeyID(RazorpayKeyId);

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

            Integer amt = Final_Amount*100;
            options.put("amount", amt);
            options.put("name", firstName);
            options.put("prefill", new JSONObject("{email: '"+email+"',contact: '"+mobile+"',name: '"+firstName+"'}"));
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

            //            Thread.sleep(3000);
            // Toast.makeText(this, "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_LONG).show();
            s=String.valueOf(amt1);
            razorpayPaymentID1=razorpayPaymentID;
            final GetData getdb = new GetData();

            new Thread(new Runnable() {
                public void run() {
                    value= getdb.getData(RazorpayKeyId,RazorpayKeySecret,s,razorpayPaymentID1,rzChargeURL);

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

                                        System.out.println(updateinv);
                                        uv=new Integer(value);
                                        uv1=uv.intValue();

                                        runOnUiThread(new Runnable() {

                                            @Override
                                            public void run() {
                                                if(updateinv.equalsIgnoreCase("success"))
                                                {
                                                    tv = (LinearLayout) findViewById(R.id.invoiceLayout);
                                                    tv.setVisibility(View.GONE);

                                                    Toast.makeText(getApplicationContext(), "Payment Successful", Toast.LENGTH_LONG).show();

                                                    if(AccountStatus.equals("deactivated")) {

                                                        AlertDialog.Builder AccountStatusAlertBox = new AlertDialog.Builder(InvoiceForDeactivatedCancelledCustomers.this, R.style.MyDialogTheme);

                                                        //Setting message manually and performing action on button click
                                                        AccountStatusAlertBox.setMessage("Do you want to continue using FAAST Broadband service?")
                                                                .setCancelable(false)
                                                                .setTitle("FAAST")
                                                                .setIcon(R.mipmap.arrow_white)
                                                                .setNegativeButton("No",
                                                                        new DialogInterface.OnClickListener() {
                                                                            public void onClick(DialogInterface dialog,
                                                                                                int id) {
                                                                                dialog.cancel();
                                                                                continueFAASTService(UserName, "no");
                                                                            }
                                                                        })
                                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int id) {
                                                                        dialog.cancel();
                                                                        continueFAASTService(UserName, "yes");
                                                                    }
                                                                });
                                                        //Creating dialog box
                                                        AlertDialog AccountStatusAlert = AccountStatusAlertBox.create();
                                                        AccountStatusAlert.show();
                                                    }
                                                    else{
                                                        tv = (LinearLayout) findViewById(R.id.invoiceLayout);
                                                        tv.setVisibility(View.GONE);

                                                        Intent i=new Intent(getApplicationContext(),Login.class);
                                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        finish();
                                                        finishAffinity();
                                                        startActivity(i);
                                                    }
                                                }
                                                else
                                                {
                                                    tv = (LinearLayout) findViewById(R.id.invoiceLayout);
                                                    tv.setVisibility(View.GONE);

                                                    Toast.makeText(getApplicationContext(), "Payment Successful", Toast.LENGTH_LONG).show();

                                                    if(AccountStatus.equals("deactivated")) {

                                                        AlertDialog.Builder AccountStatusAlertBox = new AlertDialog.Builder(InvoiceForDeactivatedCancelledCustomers.this, R.style.MyDialogTheme);

                                                        //Setting message manually and performing action on button click
                                                        AccountStatusAlertBox.setMessage("Do you want to continue using FAAST Broadband service?")
                                                                .setCancelable(false)
                                                                .setTitle("FAAST")
                                                                .setIcon(R.mipmap.arrow_white)
                                                                .setNegativeButton("No",
                                                                        new DialogInterface.OnClickListener() {
                                                                            public void onClick(DialogInterface dialog,
                                                                                                int id) {
                                                                                dialog.cancel();
                                                                                continueFAASTService(UserName, "no");
                                                                            }
                                                                        })
                                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int id) {
                                                                        dialog.cancel();
                                                                        continueFAASTService(UserName, "yes");
                                                                    }
                                                                });
                                                        //Creating dialog box
                                                        AlertDialog AccountStatusAlert = AccountStatusAlertBox.create();
                                                        AccountStatusAlert.show();
                                                    }
                                                    else{
                                                        tv = (LinearLayout) findViewById(R.id.invoiceLayout);
                                                        tv.setVisibility(View.GONE);

                                                        Intent i=new Intent(getApplicationContext(),Login.class);
                                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        finish();
                                                        finishAffinity();
                                                        startActivity(i);
                                                    }
                                                }
                                            }
                                        });

                                    }
                                }).start();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "Payment UnSuccessful", Toast.LENGTH_LONG).show();
                                Intent i=new Intent(getApplicationContext(),Login.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                finish();
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
//            Intent i=new Intent(this,PaymentUnsuccessfull.class);
//            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            finishAffinity();
//            startActivity(i);

        } catch (Exception e) {
            Log.e("com.merchant", e.getMessage(), e);
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        this.registerReceiver(this.mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        inv_pay_button.setEnabled(true);
        inv_pay_button.setClickable(true);
        // inv_pay_button.setBackgroundColor(getDrawable(R.drawable.button_pressed));
        inv_pay_button.setBackgroundResource(R.drawable.button_pressed);

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

    void continueFAASTService(String username,String reactivation_request) {

        class CreateTicketSync extends AsyncTask<String, Void, String> {

            private Dialog loadingDialog;
            String reactivation_request;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = createProgressDialog(InvoiceForDeactivatedCancelledCustomers.this );
                loadingDialog.show();
            }

            @Override
            protected String doInBackground(String... params) {
                String uname = params[0];
                reactivation_request = params[1];

                InputStream is = null;
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("username", uname));
                nameValuePairs.add(new BasicNameValuePair("reactivation_request", reactivation_request));

                String result = null;

                try {
                    HttpClient httpClient = new DefaultHttpClient();
//                    HttpPost httpPost = new HttpPost(
//                            "http://192.168.1.17/hotspot_android/plan_activation.php");
                    HttpPost httpPost = new HttpPost(reactivationURL);
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
                String s = result.trim();

                if(s.equals("success")){

                    if(reactivation_request.equals("yes")){
                        AlertDialog.Builder builder111 = new AlertDialog.Builder(InvoiceForDeactivatedCancelledCustomers.this);
                        builder111.setMessage("Your account has been reactivated successfully.")
                                .setCancelable(false)
                                .setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {
                                                dialog.cancel();
                                                Intent i = new Intent(getApplicationContext(),Login.class);
                                                startActivity(i);
                                            }
                                        });
                        AlertDialog alert = builder111.create();
                        alert.show();
                    }
                    else{
                        AlertDialog.Builder builder111 = new AlertDialog.Builder(InvoiceForDeactivatedCancelledCustomers.this);
                        builder111.setMessage("Your cancellation request has been submitted successfully.")
                                .setCancelable(false)
                                .setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {
                                                dialog.cancel();
                                                Intent i = new Intent(getApplicationContext(),Login.class);
                                                startActivity(i);
                                            }
                                        });
                        AlertDialog alert = builder111.create();
                        alert.show();
                    }
                }
                else{
                    Toast toast = Toast.makeText(getApplicationContext(),"Your account is not activated, please try once again.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        }

        CreateTicketSync la = new CreateTicketSync();
        la.execute(username,reactivation_request);
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
    protected void onSaveInstanceState(Bundle oldInstanceState) {
        super.onSaveInstanceState(oldInstanceState);
        oldInstanceState.clear();
    }
}


