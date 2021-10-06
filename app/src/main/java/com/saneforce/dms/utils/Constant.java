package com.saneforce.dms.utils;

import static android.content.Context.POWER_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonArray;
import com.saneforce.dms.DMSApplication;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class Constant {


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
	public static RequestBody toRequestBody (JsonArray value) {
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


	public static double roundTwoDecimals1(double d) {
		try {
			DecimalFormat twoDForm = new DecimalFormat("#.##");
			return Double.parseDouble(twoDForm.format(d));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return d;
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


	public static boolean checkPermissions(Context context) {
		return
				PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(context,
						android.Manifest.permission.ACCESS_FINE_LOCATION) &&
				PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(context,
						android.Manifest.permission.ACCESS_COARSE_LOCATION);
	}

	// This method  converts String to RequestBody
	public static RequestBody toRequestBody (String value) {
		return RequestBody.create(MediaType.parse("text/xml"), value);
	}

	public static String getDeviceIdNew(Context context){

		return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
	}


	public static void showSnackbar(Context context, View view){
		/*boolean hasBackgroundPermission;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
				hasBackgroundPermission = (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED);

		}else
			hasBackgroundPermission = true;

		if(hasBackgroundPermission){*/
			Snackbar.make(view, "Please enable permissions from settings",
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
					}).show();

		/*}else {
			showBackgroundPermissionDialog(context);
		}*/
	}



	public static void checkOptimizationDialog(Context context) {
		try {

			PowerManager pm = (PowerManager) context.getSystemService(POWER_SERVICE);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				if (pm != null && !pm.isIgnoringBatteryOptimizations(context.getPackageName())) {
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setTitle("Battery Optimization and Auto Start");
					builder.setMessage("Please Find San DMS in the list of applications and choose ‘Don’t optimize’ and turn on auto start ");
					builder.setPositiveButton("Allow",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();

									Intent intent = new Intent();
									intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
									context.startActivity(intent);
//                                    askIgnoreOptimization();
								}
							});

					builder.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							dialogInterface.dismiss();
						}
					});
					builder.show();

//					redirectToOptimizationSettings();
				}/* else {
					// already ignoring battery optimization code here next you want to do
				}*/
			} /*else {
				// already ignoring battery optimization code here next you want to do
			}*/
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	public static void showBackgroundPermissionDialog(Context context) {
		try {


				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("Permission");
				builder.setMessage("Please provide background location all the time");
				builder.setPositiveButton("Allow",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
								Intent intent = new Intent();
								intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
								Uri uri = Uri.fromParts("package", context.getPackageName(), null);
								intent.setData(uri);
								context.startActivity(intent);
							}
						});

				builder.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.dismiss();
					}
				});
				builder.show();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void askIgnoreOptimization() {

//		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
		@SuppressLint({"BatteryLife", "InlinedApi"})
		Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
		intent.setData(Uri.parse("package:" + DMSApplication.getActiveScreen().getPackageName()));
		DMSApplication.getActiveScreen().startActivityForResult(intent, 111);
		/*} else {
		}*/

	}
/*
    private static void redirectToOptimizationSettings() {
        if(DMSApplication.getActiveScreen()!=null){
            Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            DMSApplication.getActiveScreen().startActivity(intent);
        }

    }
*/


}