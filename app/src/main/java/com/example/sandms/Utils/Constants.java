package com.example.sandms.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.JsonArray;

import org.json.JSONArray;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class Constants {
	public static final String PARAMETER_SEP = "&";
	public static final String PARAMETER_EQUALS = "=";
	public static final String TRANS_URL = "https://secure.ccavenue.com/transaction/initTrans";

	public static boolean isInternetAvailable(Context context){
		ConnectivityManager cm =
				(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		return activeNetwork != null &&
				activeNetwork.isConnectedOrConnecting();

	}

	// This method  converts String to RequestBody
	public static RequestBody toRequestBody (JSONArray value) {
		RequestBody body = RequestBody.create(MediaType.parse("text/plain"), value.toString());
		return body ;
	}
}