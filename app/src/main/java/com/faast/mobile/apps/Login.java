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
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Login extends AppCompatActivity {

    private EditText editTextUserName;
    private EditText editTextPassword;
    TextView forgetPassword;
    SessionManager manager;
    Button l;
    String loginlink,dueAmountURL,reactivationURL,userDetailsURL;
    String username1;
    String password1;
    ImageView showpassword;
    ImageView hidepassword;
    String UserNameToSend;
    //private BroadcastReceiver mRegistrationBroadcastReceiver;
    String token;
    String text ;
    String pas;
    String devicegcmid2;
    String message;
    private boolean isShown = false;
    AlertDialog alertUpdate;
    String loginResponse;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "fixture";

    private static final String TAG_FIRSTNAME = "firstname";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_MOBILE = "mobile";

    String firstname,email,mobile;
    private ProgressDialog pDialog;
    JSONParser jsonParser= new JSONParser();
    ArrayList<HashMap<String, String>> profileList = new ArrayList<HashMap<String, String>>();
    JSONArray matchFixture = null;


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
                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        this.registerReceiver(this.mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        Window window = Login.this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Login.this.getResources().getColor(R.color.my_statusbar_color));
        }

        showpassword = (ImageView)findViewById(R.id.show_password);
        hidepassword = (ImageView)findViewById(R.id.hide_password);

        SharedPreferences URL = getApplicationContext().getSharedPreferences("DatabaseLinks", MODE_PRIVATE);
        loginlink = URL.getString("login","");
        dueAmountURL = URL.getString("homeduedata","");
        userDetailsURL = URL.getString("userdetails","");
        reactivationURL = URL.getString("requestforreactivationurl","");

        manager = new SessionManager();

        //Initializing our broadcast receiver

        editTextUserName = (EditText) findViewById(R.id.username);
        editTextPassword = (EditText) findViewById(R.id.password);
        l=(Button)findViewById(R.id.login);
        text = editTextUserName.getText().toString();
        pas = editTextPassword.getText().toString();


        forgetPassword = (TextView)findViewById(R.id.forget_password);
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login.this,ForgotPassword.class);
                finish();
                startActivity(i);
                overridePendingTransition(R.anim.enter, R.anim.exit);

            }
        });

        hidepassword.setOnClickListener(new View.OnClickListener() {
            Typeface typeface = editTextPassword.getTypeface();
            @Override
            public void onClick(View view) {
                showpassword.setVisibility(View.VISIBLE);
                hidepassword.setVisibility(View.INVISIBLE);
                editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD );
                editTextPassword.setTypeface(typeface);
                editTextPassword.setSelection(editTextPassword.length());
            }
        });

        showpassword.setOnClickListener(new View.OnClickListener() {
            Typeface typeface = editTextPassword.getTypeface();
            @Override
            public void onClick(View view) {
                hidepassword.setVisibility(View.VISIBLE);
                showpassword.setVisibility(View.INVISIBLE);
                editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD );
                editTextPassword.setTypeface(typeface);
                editTextPassword.setSelection(editTextPassword.length());
            }
        });

        Intent i = getIntent();
        message = i.getStringExtra("Message");
        if (message!=null){
            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this, R.style.MyDialogTheme);

            //Setting message manually and performing action on button click
            builder.setMessage(message)
                    .setCancelable(false)
                    .setIcon(R.mipmap.arrow_white)
                    .setTitle("FAAST")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            GetVersionCode getVersionCode=new GetVersionCode();
                            getVersionCode.execute();
                        }
                    });
            //Creating dialog box
            AlertDialog alert = builder.create();
            alert.show();
        }
        else
        {
            GetVersionCode getVersionCode=new GetVersionCode();
            getVersionCode.execute();
        }
    }

    void continueFAASTService(String username,String reactivation_request) {

        class CreateTicketSync extends AsyncTask<String, Void, String> {

            private Dialog loadingDialog;
            String reactivation_request;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = createProgressDialog(Login.this );
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
                        AlertDialog.Builder builder111 = new AlertDialog.Builder(Login.this);
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
                        AlertDialog.Builder builder111 = new AlertDialog.Builder(Login.this);
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

private BroadcastReceiver mRegistrationBroadcastReceiver = new BroadcastReceiver() {

        //When the broadcast received
        //We are sending the broadcast from GCMRegistrationIntentService

        @Override
        public void onReceive(Context context, Intent intent) {
            //If the broadcast has received with success
            //that means device is registered successfully
            if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)){
                //Getting the registration token from the intent
                token = intent.getStringExtra("token");
                //Displaying the token as toast
                Toast.makeText(getApplicationContext(), "Registration token:" + token, Toast.LENGTH_LONG).show();

                //if the intent is not with success then displaying error messages
            } else if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)){
                Toast.makeText(getApplicationContext(), "GCM registration error!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Error occurred", Toast.LENGTH_LONG).show();
            }
        }
    };

    public void invokeLogin(View view) {
        username1 = editTextUserName.getText().toString();
        password1 = editTextPassword.getText().toString();
        if (username1.equals("") && password1.equals("")) {
            Toast toast = Toast.makeText(getApplicationContext(),"Enter Username and Password", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        else if(username1.equals(""))
        {
            Toast toast = Toast.makeText(getApplicationContext(),"Enter Username", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        else if(password1.equals(""))
        {
            Toast toast = Toast.makeText(getApplicationContext(),"Enter Password", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        else {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            SharedPreferences deviceid = getApplicationContext().getSharedPreferences("DeviceToken",MODE_PRIVATE);
            devicegcmid2 = deviceid.getString("device_token","");
//            Toast.makeText(getApplicationContext(),devicegcmid2,Toast.LENGTH_SHORT).show();
            login(username1, password1, devicegcmid2);
        }
    }

    private void login(final String username,final String password,final String gcmid) {

        class LoginAsync extends AsyncTask<String, Void, String> {

            private Dialog loadingDialog;

                        @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = createProgressDialog(Login.this);
                loadingDialog.show();
            }

            @Override
            protected String doInBackground(String... params) {
                String uname = params[0];
                String pass = params[1];
                String id = params[2];
                System.out.println("GCMTOKEN:"+id);

                InputStream is = null;
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("username", uname));
                nameValuePairs.add(new BasicNameValuePair("password", pass));
                nameValuePairs.add(new BasicNameValuePair("gcmid", id));

                String result = null;

                try {
                    HttpClient httpClient = new DefaultHttpClient();
//                    HttpPost httpPost = new HttpPost(
//                            "http://10.0.2.2/android_faast_db/login.php");
                    HttpPost httpPost = new HttpPost(
                            loginlink);
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse response = httpClient.execute(httpPost);

                    HttpEntity entity = response.getEntity();

                    is = entity.getContent();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                        Log.d("Output=", line);
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
                loginResponse = result.trim();
                System.out.println(loginResponse);

                SharedPreferences myPrefs = getApplicationContext().getSharedPreferences("contacts", MODE_PRIVATE);
                SharedPreferences.Editor editor = myPrefs.edit();
                editor.putString("Username",username);
                editor.commit();

                SharedPreferences username_shared_preference = Login.this.getSharedPreferences("contacts", MODE_PRIVATE);
                UserNameToSend = username_shared_preference.getString("Username", "");

                if (loginResponse.equalsIgnoreCase("success")) {
                    manager.setPreferences(Login.this, "status", "1");

                    Intent i = new Intent(Login.this, HomeInternetStatus.class);
                    finish();
                    startActivity(i);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                } else if (loginResponse.equalsIgnoreCase("cancelled")){
                    editTextPassword.setText("");
                    editTextUserName.setText("");
                    editTextUserName.requestFocus();

                    AlertDialog.Builder AccountStatusAlertBox = new AlertDialog.Builder(Login.this, R.style.MyDialogTheme);
                    //Setting message manually and performing action on button click
                    AccountStatusAlertBox.setMessage("Your account has been cancelled, kindly tap OK to make the payment towards the outstanding balance.")
                            .setCancelable(false)
                            .setTitle("FAAST")
                            .setIcon(R.mipmap.arrow_white)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    new GetUserDetails().execute();
                                }
                            });
                    //Creating dialog box
                    AlertDialog AccountStatusAlert = AccountStatusAlertBox.create();
                    AccountStatusAlert.show();
                } else if (loginResponse.equalsIgnoreCase("deactivated")){
                    editTextPassword.setText("");
                    editTextUserName.setText("");
                    editTextUserName.requestFocus();

                    AlertDialog.Builder AccountStatusAlertBox = new AlertDialog.Builder(Login.this, R.style.MyDialogTheme);

                    //Setting message manually and performing action on button click
                    AccountStatusAlertBox.setMessage("Your account has been deactivated, kindly tap OK to make the payment towards the outstanding balance.")
                            .setCancelable(false)
                            .setTitle("FAAST")
                            .setIcon(R.mipmap.arrow_white)

                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    new GetUserDetails().execute();
                                }
                            });
                    //Creating dialog box
                    AlertDialog AccountStatusAlert = AccountStatusAlertBox.create();
                    AccountStatusAlert.show();
                }
                else if(loginResponse.equals("failure")){
                    AlertDialog.Builder AccountStatusAlertBox = new AlertDialog.Builder(Login.this, R.style.MyDialogTheme);

                    //Setting message manually and performing action on button click
                    AccountStatusAlertBox.setMessage("Login failure, kindly contact customer care for more information.")
                            .setCancelable(false)
                            .setTitle("FAAST")
                            .setIcon(R.mipmap.arrow_white)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    editTextPassword.setText("");
                                    editTextUserName.setText("");
                                    editTextUserName.requestFocus();
                                }
                            });
                    //Creating dialog box
                    AlertDialog AccountStatusAlert = AccountStatusAlertBox.create();
                    AccountStatusAlert.show();
                    editTextPassword.setText("");
                    editTextUserName.setText("");
                    editTextUserName.requestFocus();
                }
                else{
                    editTextPassword.setText("");
                    editTextUserName.setText("");
                    Toast toast = Toast.makeText(getApplicationContext(),"Invalid User Name or Password", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    editTextUserName.requestFocus();
                }
            }
        }

        LoginAsync la = new LoginAsync();
        la.execute(username, password,gcmid);
    }

    @Override
    public void onResume() {
        this.registerReceiver(this.mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        super.onResume();

        // this.registerReceiver(this.mConnReceiver1,
        //new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
       this.registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
       this.registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));
        editTextUserName.setText("");
        editTextPassword.setText("");
        editTextUserName.requestFocus();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.unregisterReceiver(mConnReceiver);
        //this.unregisterReceiver(mConnReceiver1);
        this.unregisterReceiver(mRegistrationBroadcastReceiver);

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

    public class GetVersionCode extends AsyncTask<Void, String, String> {
        @Override
        protected String doInBackground(Void... voids) {

            String newVersion = null;
            try {
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + Login.this.getPackageName() + "&hl=it")
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div[itemprop=softwareVersion]")
                        .first()
                        .ownText();
                return newVersion;
            } catch (Exception e) {
                return newVersion;
            }
        }

        @Override
        protected void onPostExecute(String onlineVersion) {
            super.onPostExecute(onlineVersion);
            String appVersion = BuildConfig.VERSION_NAME;

            if (onlineVersion != null && !onlineVersion.isEmpty()) {

                if (Float.valueOf(appVersion) < Float.valueOf(onlineVersion)) {
//                if (!appVersion.equals(onlineVersion)) {
                    //show dialog
                    AlertDialog.Builder builderUpdate = new AlertDialog.Builder(Login.this, R.style.MyDialogTheme);

                    //Setting message manually and performing action on button click
                    builderUpdate.setMessage("New version available in the playstore, please update the app.");
                    builderUpdate.setIcon(R.mipmap.arrow_white);
                    builderUpdate.setTitle("FAAST");
                    builderUpdate.setCancelable(false);
                    builderUpdate.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finishAffinity();
                            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.faast.mobile.apps&hl=en");
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }
                    });
                    //Creating dialog box
                    alertUpdate = builderUpdate.create();
                    alertUpdate.show();
                    isShown=true;
                    System.out.println("is showing"+isShown);

                     alertUpdate.setOnKeyListener(new DialogInterface.OnKeyListener() {

                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                            if(keyCode == KeyEvent.KEYCODE_BACK){
                                finishAffinity();
                            }
                            return false;
                        }
                    });
                }
                else{

                }
            }
            Log.d("update", "Current version " + appVersion + "playstore version " + onlineVersion);
            System.out.println("update "+ "Current version " + appVersion + "playstore version " + onlineVersion);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.enter, R.anim.exit);
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


    class GetUserDetails extends AsyncTask<String, String, String> {
        private Dialog pDialog1;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog1 = createProgressDialog(Login.this);

            pDialog1.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            int success;

            try {
                Log.d("try", "in the try");
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                params.add(new BasicNameValuePair("username", UserNameToSend));
                JSONObject json = jsonParser.makeHttpRequest(
                        userDetailsURL, "POST", params);
                Log.d("jsonObject", "new json Object");

                matchFixture = json.getJSONArray(TAG_PRODUCTS);
                JSONObject c1 = matchFixture.getJSONObject(0);
                success = c1.getInt(TAG_SUCCESS);

                if (success == 1) {

                    for (int i = 0; i < matchFixture.length(); i++) {
                        JSONObject c = matchFixture.getJSONObject(i);

                        String firstname = c.getString(TAG_FIRSTNAME);
                        String email = c.getString(TAG_EMAIL);
                        String mobile = c.getString(TAG_MOBILE);

                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(TAG_FIRSTNAME, firstname);
                        map.put(TAG_EMAIL, email);
                        map.put(TAG_MOBILE, mobile);
                        profileList.add(map);
                    }
                } else if (success == 0) {

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {
//            pDialog.dismiss();
            super.onPostExecute(result);
            System.out.print("OUTPUT=="+result);
            // dismiss the dialog once got all details

            for (HashMap<String, String> map : profileList) {
                firstname = map.get(TAG_FIRSTNAME);
                email = map.get(TAG_EMAIL);
                mobile = map.get(TAG_MOBILE);
            }
            System.out.println("OUTPUT=="+firstname);
            System.out.println("OUTPUT=="+email);
            System.out.println("OUTPUT=="+mobile);

            Intent i = new Intent(Login.this, InvoiceForDeactivatedCancelledCustomers.class);
            i.putExtra("account_status",loginResponse);
            i.putExtra("firstname",firstname);
            i.putExtra("mobile",mobile);
            i.putExtra("email",email);
            finish();
            startActivity(i);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle oldInstanceState) {
        super.onSaveInstanceState(oldInstanceState);
        oldInstanceState.clear();
    }
}


//package com.faast.mobile.apps;
//
//import android.app.ActivityManager;
//import android.app.Dialog;
//import android.app.ProgressDialog;
//import android.content.BroadcastReceiver;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.SharedPreferences;
//import android.graphics.Typeface;
//import android.graphics.drawable.ColorDrawable;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Build;
//import android.os.Bundle;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
//import android.text.InputType;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.message.BasicNameValuePair;
//import org.jsoup.Jsoup;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.UnsupportedEncodingException;
//import java.util.ArrayList;
//import java.util.List;
//
//public class Login extends AppCompatActivity {
//
//    private EditText editTextUserName;
//    private EditText editTextPassword;
//    TextView forgetPassword;
//    SessionManager manager;
//    Button l;
//    String loginlink;
//    String username1;
//    String password1;
//    ImageView showpassword;
//    ImageView hidepassword;
//
//    //private BroadcastReceiver mRegistrationBroadcastReceiver;
//    String token;
//    String text ;
//    String pas;
//    String devicegcmid2;
//    String message;
//    private boolean isShown = false;
//    AlertDialog alertUpdate;
//
//    //-----------------------------BROADCAST RECEIVER----------------------------------
//
//    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
//        public void onReceive(Context context, Intent intent) {
//            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
//            String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
//            boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);
//
//            NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
//            NetworkInfo otherNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);
//
//            if (currentNetworkInfo.isConnected()) {
//
//            }
//
//            else {
//                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
//                // Set the Alert Dialog Message
//                builder.setMessage("Internet Connection Required")
//                        .setCancelable(false)
//                        .setPositiveButton("Retry",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog,
//                                                        int id) {
//                                        // Restart the Activity
//
//                                        registerReceiver(mConnReceiver,
//                                                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
//
//
//                                    }
//                                });
//                AlertDialog alert = builder.create();
//                alert.show();
//            }
//
//        }
//    };
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
//        actionBar.hide();
//        this.registerReceiver(this.mConnReceiver,
//                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
//
//        Window window = Login.this.getWindow();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            window.setStatusBarColor(Login.this.getResources().getColor(R.color.my_statusbar_color));
//        }
//
//        showpassword = (ImageView)findViewById(R.id.show_password);
//        hidepassword = (ImageView)findViewById(R.id.hide_password);
//
//        SharedPreferences LoginLink = getApplicationContext().getSharedPreferences("DatabaseURL", MODE_PRIVATE);
//        loginlink = LoginLink.getString("login","");
//        manager = new SessionManager();
//
//        //Initializing our broadcast receiver
//
//        editTextUserName = (EditText) findViewById(R.id.username);
//        editTextPassword = (EditText) findViewById(R.id.password);
//        l=(Button)findViewById(R.id.login);
//        text = editTextUserName.getText().toString();
//        pas = editTextPassword.getText().toString();
//
//
//        forgetPassword = (TextView)findViewById(R.id.forget_password);
//        forgetPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(Login.this,ForgotPassword.class);
//                finish();
//                startActivity(i);
//                overridePendingTransition(R.anim.enter, R.anim.exit);
//
//            }
//        });
//
//        hidepassword.setOnClickListener(new View.OnClickListener() {
//            Typeface typeface = editTextPassword.getTypeface();
//            @Override
//            public void onClick(View view) {
//                showpassword.setVisibility(View.VISIBLE);
//                hidepassword.setVisibility(View.INVISIBLE);
//                editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT |
//                        InputType.TYPE_TEXT_VARIATION_PASSWORD );
//                editTextPassword.setTypeface(typeface);
//                editTextPassword.setSelection(editTextPassword.length());
//            }
//        });
//
//        showpassword.setOnClickListener(new View.OnClickListener() {
//            Typeface typeface = editTextPassword.getTypeface();
//            @Override
//            public void onClick(View view) {
//                hidepassword.setVisibility(View.VISIBLE);
//                showpassword.setVisibility(View.INVISIBLE);
//                editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT |
//                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD );
//                editTextPassword.setTypeface(typeface);
//                editTextPassword.setSelection(editTextPassword.length());
//            }
//        });
//
//
//        Intent i = getIntent();
//        message = i.getStringExtra("Message");
//        if (message!=null){
//            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this, R.style.MyDialogTheme);
//
//            //Setting message manually and performing action on button click
//            builder.setMessage(message)
//                    .setCancelable(false)
//                    .setIcon(R.mipmap.arrow_white)
//                    .setTitle("FAAST")
//                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            dialog.cancel();
//                            GetVersionCode getVersionCode=new GetVersionCode();
//                            getVersionCode.execute();
//
//                        }
//                    });
//            //Creating dialog box
//            AlertDialog alert = builder.create();
//            alert.show();
//
//        }
//        else
//        {
//            GetVersionCode getVersionCode=new GetVersionCode();
//            getVersionCode.execute();
//        }
//    }
//
//    private BroadcastReceiver mRegistrationBroadcastReceiver = new BroadcastReceiver() {
//
//        //When the broadcast received
//        //We are sending the broadcast from GCMRegistrationIntentService
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            //If the broadcast has received with success
//            //that means device is registered successfully
//            if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)){
//                //Getting the registration token from the intent
//                token = intent.getStringExtra("token");
//                //Displaying the token as toast
//                Toast.makeText(getApplicationContext(), "Registration token:" + token, Toast.LENGTH_LONG).show();
//
//                //if the intent is not with success then displaying error messages
//            } else if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)){
//                Toast.makeText(getApplicationContext(), "GCM registration error!", Toast.LENGTH_LONG).show();
//            } else {
//                Toast.makeText(getApplicationContext(), "Error occurred", Toast.LENGTH_LONG).show();
//            }
//        }
//    };
//
//
//    public void invokeLogin(View view) {
//        username1 = editTextUserName.getText().toString();
//        password1 = editTextPassword.getText().toString();
//        if (username1.equals("") && password1.equals("")) {
//            Toast toast = Toast.makeText(getApplicationContext(),"Enter Username and Password", Toast.LENGTH_SHORT);
//            toast.setGravity(Gravity.CENTER, 0, 0);
//            toast.show();
//        }
//        else if(username1.equals(""))
//        {
//            Toast toast = Toast.makeText(getApplicationContext(),"Enter Username", Toast.LENGTH_SHORT);
//            toast.setGravity(Gravity.CENTER, 0, 0);
//            toast.show();
//        }
//        else if(password1.equals(""))
//        {
//            Toast toast = Toast.makeText(getApplicationContext(),"Enter Password", Toast.LENGTH_SHORT);
//            toast.setGravity(Gravity.CENTER, 0, 0);
//            toast.show();
//        }
//        else {
//            SharedPreferences deviceid = getApplicationContext().getSharedPreferences("DeviceToken",MODE_PRIVATE);
//            devicegcmid2 = deviceid.getString("device_token","");
////            Toast.makeText(getApplicationContext(),devicegcmid2,Toast.LENGTH_SHORT).show();
//            login(username1, password1, devicegcmid2);
//        }
//    }
//
//    private void login(final String username,final String password,final String gcmid) {
//
//        class LoginAsync extends AsyncTask<String, Void, String> {
//
//            private Dialog loadingDialog;
//
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
////                loadingDialog = ProgressDialog.show(Login.this, "Please wait", "Loading...");
//                loadingDialog = createProgressDialog(Login.this);
//                loadingDialog.show();
//            }
//
//            @Override
//            protected String doInBackground(String... params) {
//                String uname = params[0];
//                String pass = params[1];
//                String id = params[2];
//                System.out.println("GCMTOKEN:"+id);
//
//                InputStream is = null;
//                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//                nameValuePairs.add(new BasicNameValuePair("username", uname));
//                nameValuePairs.add(new BasicNameValuePair("password", pass));
//                nameValuePairs.add(new BasicNameValuePair("gcmid", id));
//
//                String result = null;
//
//                try {
//                    HttpClient httpClient = new DefaultHttpClient();
////                    HttpPost httpPost = new HttpPost(
////                            "http://10.0.2.2/android_faast_db/login.php");
//                    HttpPost httpPost = new HttpPost(
//                            loginlink);
//                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//
//                    HttpResponse response = httpClient.execute(httpPost);
//
//                    HttpEntity entity = response.getEntity();
//
//                    is = entity.getContent();
//
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
//                    StringBuilder sb = new StringBuilder();
//
//                    String line = null;
//                    while ((line = reader.readLine()) != null) {
//                        sb.append(line + "\n");
//                        Log.d("Output=", line);
//                    }
//                    result = sb.toString();
//                } catch (ClientProtocolException e) {
//                    e.printStackTrace();
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                return result;
//            }
//
//            @Override
//            protected void onPostExecute(String result) {
//                loadingDialog.dismiss();
//                String s = result.trim();
//                System.out.println(s);
//                if (s.equalsIgnoreCase("success")) {
//                    manager.setPreferences(Login.this, "status", "1");
//
//                    SharedPreferences myPrefs = getApplicationContext().getSharedPreferences("contacts", MODE_PRIVATE);
//                    SharedPreferences.Editor editor = myPrefs.edit();
//                    editor.putString("Username", username);
//                    editor.commit();
//                    Intent i = new Intent(Login.this, HomeInternetStatus.class);
//                    finish();
//                    startActivity(i);
//                    overridePendingTransition(R.anim.enter, R.anim.exit);
//
//                } else {
//
//                    editTextPassword.setText("");
//                    editTextUserName.setText("");
//                    Toast toast = Toast.makeText(getApplicationContext(),"Invalid User Name or Password", Toast.LENGTH_SHORT);
//                    toast.setGravity(Gravity.CENTER, 0, 0);
//                    toast.show();
//                    editTextUserName.requestFocus();
//
//                }
//            }
//        }
//
//        LoginAsync la = new LoginAsync();
//        la.execute(username, password,gcmid);
//
//    }
//
//
//    @Override
//    public void onResume() {
//        this.registerReceiver(this.mConnReceiver,
//                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
//        super.onResume();
//
//        // this.registerReceiver(this.mConnReceiver1,
//        //new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
//        this.registerReceiver(mRegistrationBroadcastReceiver,
//                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
//        this.registerReceiver(mRegistrationBroadcastReceiver,
//                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));
//        editTextUserName.setText("");
//        editTextPassword.setText("");
//        editTextUserName.requestFocus();
//
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        this.unregisterReceiver(mConnReceiver);
//        //this.unregisterReceiver(mConnReceiver1);
//        this.unregisterReceiver(mRegistrationBroadcastReceiver);
//
//        if(isApplicationSentToBackground(this))
//        {
//            finishAffinity();
//        }
//
//    }
//
//    public static ProgressDialog createProgressDialog(Context mContext) {
//        ProgressDialog dialog = new ProgressDialog(mContext);
//        try {
//            dialog.show();
//        } catch (WindowManager.BadTokenException e) {
//
//        }
//        dialog.setCancelable(false);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        dialog.setContentView(R.layout.progress_dialogue);
//        // dialog.setMessage(Message);
//        return dialog;
//    }
//
//    public class GetVersionCode extends AsyncTask<Void, String, String> {
//        @Override
//        protected String doInBackground(Void... voids) {
//
//            String newVersion = null;
//            try {
//                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + Login.this.getPackageName() + "&hl=it")
//                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
//                        .referrer("http://www.google.com")
//                        .get()
//                        .select("div[itemprop=softwareVersion]")
//                        .first()
//                        .ownText();
//                return newVersion;
//            } catch (Exception e) {
//                return newVersion;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(String onlineVersion) {
//            super.onPostExecute(onlineVersion);
//            String appVersion = BuildConfig.VERSION_NAME;
//
//            if (onlineVersion != null && !onlineVersion.isEmpty()) {
//
//                if (Float.valueOf(appVersion) < Float.valueOf(onlineVersion)) {
////                if (!appVersion.equals(onlineVersion)) {
//                    //show dialog
//                    AlertDialog.Builder builderUpdate = new AlertDialog.Builder(Login.this, R.style.MyDialogTheme);
//
//                    //Setting message manually and performing action on button click
//                    builderUpdate.setMessage("New version available in the playstore, please update the app.");
//                    builderUpdate.setIcon(R.mipmap.arrow_white);
//                    builderUpdate.setTitle("FAAST");
//                    builderUpdate.setCancelable(false);
//                    builderUpdate.setPositiveButton("Update", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            finishAffinity();
//                            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.faast.mobile.apps&hl=en");
//                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                            startActivity(intent);
//
//                        }
//                    });
//                    //Creating dialog box
//                    alertUpdate = builderUpdate.create();
//                    alertUpdate.show();
//                    isShown=true;
//                    System.out.println("is showing"+isShown);
//
//                    alertUpdate.setOnKeyListener(new DialogInterface.OnKeyListener() {
//
//                        @Override
//                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//
//                            if(keyCode == KeyEvent.KEYCODE_BACK){
//                                finishAffinity();
//                            }
//                            return false;
//
//                        }
//                    });
//
//                }
//                else{
//
//                }
//            }
//            Log.d("update", "Current version " + appVersion + "playstore version " + onlineVersion);
//            System.out.println("update "+ "Current version " + appVersion + "playstore version " + onlineVersion);
//        }
//
//    }
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        finish();
//        overridePendingTransition(R.anim.enter, R.anim.exit);
//    }
//
//    public boolean isApplicationSentToBackground(final Context context) {
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
//        if (!tasks.isEmpty()) {
//            ComponentName topActivity = tasks.get(0).topActivity;
//            if (!topActivity.getPackageName().equals(context.getPackageName())) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//
//
//}
//
//
