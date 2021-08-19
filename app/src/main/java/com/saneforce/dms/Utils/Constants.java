package com.saneforce.dms.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.snackbar.Snackbar;
import com.saneforce.dms.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
		return RequestBody.create(MediaType.parse("text/plain"), value.toString());
	}

	// This method  converts String to RequestBody
	public static RequestBody toRequestBody (JSONObject value) {
		return RequestBody.create(MediaType.parse("text/plain"), value.toString());
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

	public static String roundTwoDecimals(double d) {
		try {
			DecimalFormat twoDForm = new DecimalFormat("#.##");
			return twoDForm.format(d);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return String.valueOf(d);
		}
	}


	public static void hideKeyboard(Activity activity) {
		try {

			InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
			//Find the currently focused view, so we can grab the correct window token from it.
			View view = activity.getCurrentFocus();
			//If no view currently has focus, create a new one, just so we can grab a window token from it
			if (view == null) {
				view = new View(activity);
			}
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}catch (Exception e){
			e.printStackTrace();
		}

	}

	public static void showSnackbar(Context context, View view){

		Snackbar.make(view, "Please enable permission from settings",
				Snackbar.LENGTH_INDEFINITE)
				.setAction("OK", new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent();
						intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
						Uri uri = Uri.fromParts("package", context.getPackageName(), null);
						intent.setData(uri);
						context.startActivity(intent);
					}
				})
				.show();
	}

}