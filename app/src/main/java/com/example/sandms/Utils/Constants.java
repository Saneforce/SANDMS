package com.example.sandms.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONObject;

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

	// This method  converts String to RequestBody
	public static RequestBody toRequestBody (JSONObject value) {
		RequestBody body = RequestBody.create(MediaType.parse("text/plain"), value.toString());
		return body ;
	}

	public static boolean isNetworkAvailable(Context context) {
		boolean isConnected = false;
		if(context!=null) {
			final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
			if(connectivityManager.getActiveNetworkInfo()!=null) {
				isConnected = connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
			}
		}else{
			isConnected = true;
		}

		try {
			Log.d("Constant", "isNetworkAvailable: isConnected => "+ isConnected );
//            if(!isConnected)
//                Constant.showToast(context, "No Internet Connection Available");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isConnected;
	}

}