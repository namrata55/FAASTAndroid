package com.faast.mobile.apps.faastwifi;

/**
 * Created by namrata.s on 17/03/2017.
 */


import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.ArrayList;
import java.util.List;

public class GetHotspotPlans {

    public String getplans(String URL) {
        try {

            HttpPost httppost;
            HttpClient httpclient;
            httpclient = new DefaultHttpClient();
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            httppost = new HttpPost(
                    URL); // change this to your URL.....

//            httppost = new HttpPost(
//                    "http://192.168.1.17/hotspot_android/all_plans.php"); // change this to your URL.....
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            final String response = httpclient.execute(httppost,responseHandler);
            // System.out.println(response.trim());

            return response.trim();

        } catch (Exception e) {
            System.out.println("ERROR : " + e.getMessage());
            return "error";
        }
    }
}
