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

public class UpdateWallet {

    public String updateWallet(String USERNAME,String paymentid,String URL,String credit,String debit,String balance,String invnum) {
        try {

            HttpPost httppost;
            HttpClient httpclient;
            httpclient = new DefaultHttpClient();
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("username", USERNAME));
            nameValuePairs.add(new BasicNameValuePair("paymentid", paymentid));
            nameValuePairs.add(new BasicNameValuePair("credit", credit));
            nameValuePairs.add(new BasicNameValuePair("debit", debit));
            nameValuePairs.add(new BasicNameValuePair("balance", balance));
            nameValuePairs.add(new BasicNameValuePair("invnum", invnum));
//
//            httppost = new HttpPost(
//                    "http://10.0.2.2/android_faast_db/updateinvoice.php"); // change this to your URL.....

            httppost = new HttpPost(URL);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            final String response = httpclient.execute(httppost,responseHandler);
            System.out.println("Output from wallet"+response.trim());

            return response.trim();

        } catch (Exception e) {
            System.out.println("ERROR : " + e.getMessage());
            return "error";
        }
    }
}