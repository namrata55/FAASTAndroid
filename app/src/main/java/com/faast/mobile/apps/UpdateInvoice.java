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

public class UpdateInvoice {

    public String updateInvoice(String USERNAME,String paymentid,String URL) {
        try {

            HttpPost httppost;
            HttpClient httpclient;
            httpclient = new DefaultHttpClient();
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("username", USERNAME));
            nameValuePairs.add(new BasicNameValuePair("paymentid", paymentid));
//
//            httppost = new HttpPost(
//                    "http://10.0.2.2/android_faast_db/updateinvoice.php"); // change this to your URL.....

            httppost = new HttpPost(URL);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            final String response = httpclient.execute(httppost,responseHandler);
            System.out.println(response.trim());

            return response.trim();

        } catch (Exception e) {
            System.out.println("ERROR : " + e.getMessage());
            return "error";
        }
    }
}