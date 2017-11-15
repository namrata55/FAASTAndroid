package com.faast.mobile.apps;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class GetData {

    public String getData(String your_key_id, String key_secret, String amount, String payment_id,String URL) {

        try {

            HttpPost httppost;
            HttpClient httpclient;
            httpclient = new DefaultHttpClient();
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("key_id", your_key_id));
            nameValuePairs.add(new BasicNameValuePair(" key_secret", key_secret));
            nameValuePairs.add(new BasicNameValuePair("amount", amount));
            nameValuePairs.add(new BasicNameValuePair("razorpay_payment_id", payment_id));

//            httppost = new HttpPost(
//                    "http://10.0.2.2/razorpay_payment/charge.php");

             httppost = new HttpPost(
                         URL);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            final String response = httpclient.execute(httppost,responseHandler);
            System.out.println(response.trim());

            return response.trim();

        }

        catch (Exception e) {
            System.out.println("ERROR : " + e.getMessage());
            return "0";
        }

    }
}



