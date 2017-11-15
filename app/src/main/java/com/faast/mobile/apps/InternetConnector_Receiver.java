package com.faast.mobile.apps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class InternetConnector_Receiver extends BroadcastReceiver {
	public InternetConnector_Receiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		try {

			boolean isVisible = MyApplication.isActivityVisible();// Check if

			Log.i("Activity is Visible ", "Is activity visible : " + isVisible);

			if (isVisible == true) {
				ConnectivityManager connectivityManager = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo networkInfo = connectivityManager
						.getActiveNetworkInfo();

				if (networkInfo != null && networkInfo.isConnected()) {

					new HomeInternetStatus().changeTextStatus(true);
				} else {
					new HomeInternetStatus().changeTextStatus(false);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
