package com.faast.mobile.apps;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

public class GetAllTopups {

    public String getTopups(String URL) {
        try {

            HttpPost httppost;
            HttpClient httpclient;
            httpclient = new DefaultHttpClient();

            httppost = new HttpPost(
                    URL); // change this to your URL.....

//            httppost = new HttpPost(
//                    "http://192.168.1.17/hotspot_android/all_plans.php"); // change this to your URL.....
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost,responseHandler);
//             System.out.println(response.trim());

            return response.trim();

        } catch (Exception e) {
            System.out.println("ERROR : " + e.getMessage());
            return "error";
        }
    }
}