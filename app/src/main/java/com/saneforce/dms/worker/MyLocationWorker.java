package com.saneforce.dms.worker;

import static android.content.Context.POWER_SERVICE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.saneforce.dms.DMSApplication;
import com.saneforce.dms.R;
import com.saneforce.dms.listener.ApiInterface;
import com.saneforce.dms.sqlite.DBController;
import com.saneforce.dms.utils.ApiClient;
import com.saneforce.dms.utils.Constants;
import com.saneforce.dms.utils.Shared_Common_Pref;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Vengat on 14-09-2021
 */


public class MyLocationWorker extends Worker {

	private static final String DEFAULT_START_TIME = "06:00";
	private static final String DEFAULT_END_TIME = "21:00";

	private static final String TAG = MyLocationWorker.class.getSimpleName();

	/**
	 * The desired interval for location updates. Inexact. Updates may be more or less frequent.
	 */
	private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

	/**
	 * The fastest rate for active location updates. Updates will never be more frequent
	 * than this value.
	 */
	private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
			UPDATE_INTERVAL_IN_MILLISECONDS / 2;
	/**
	 * The current location.
	 */
	private Location mLocation;
	LocationRequest mLocationRequest;
	/**
	 * Provides access to the Fused Location Provider API.
	 */
	private FusedLocationProviderClient mFusedLocationClient;

	private Context mContext;
	/**
	 * Callback for changes in location.
	 */
	private LocationCallback mLocationCallback;

	DBController dbController;
	Shared_Common_Pref shared_common_pref;

	private BroadcastReceiver yourReceiver;
	private static final String ACTION_GPS = "android.location.PROVIDERS_CHANGED";
	boolean mShownSettings;
	public static void createMyLocationWorker(Context context){
//		if(!isWorkScheduled(TAG, context)){
			startLocationTracker();
//		}
	}
	public MyLocationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
		super(context, workerParams);
		this.mContext = context;
		dbController = new DBController(mContext);
		shared_common_pref = new Shared_Common_Pref(mContext);
	}

	@NonNull
	@Override
	public Result doWork() {
		Log.d(TAG, "doWork: Done");

		Log.d(TAG, "onStartJob: STARTING JOB..");

		DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

		final Calendar c = Calendar.getInstance();
		Date date = c.getTime();
		String formattedDate = dateFormat.format(date);

		try {
			Date currentDate = dateFormat.parse(formattedDate);
			Date startDate = dateFormat.parse(DEFAULT_START_TIME);
			Date endDate = dateFormat.parse(DEFAULT_END_TIME);
// && currentDate.after(startDate) && currentDate.before(endDate)
			if (shared_common_pref!=null && !shared_common_pref.getvalue1("Login_details").equals("")) {
				mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
				mLocationCallback = new LocationCallback() {
					@Override
					public void onLocationResult(LocationResult locationResult) {
						super.onLocationResult(locationResult);
					}
				};

				mLocationRequest = new LocationRequest();
				mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
				mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
				mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

				try {
					mFusedLocationClient
							.getLastLocation()
							.addOnCompleteListener(new OnCompleteListener<Location>() {
								@Override
								public void onComplete(@NonNull Task<Location> task) {
									if (task.isSuccessful() && task.getResult() != null) {
										mLocation = task.getResult();



										String address = null;
										try {
											address = getCompleteAddressString(mLocation.getLatitude(), mLocation.getLongitude());
										} catch (Exception e) {
											e.printStackTrace();
										}

										if(dbController == null )
											dbController = new DBController(mContext);

										dbController.addLocation(mLocation, "0", address);

										if(Constants.isInternetAvailable(mContext))
											updateLocationToServer();

										try {
											Log.d(TAG, "Location : " + mLocation);
											shared_common_pref.save(String.valueOf(Calendar.getInstance().getTimeInMillis()), ""+ mLocation.getLatitude()+ ", "+ mLocation.getLongitude());
										} catch (Exception e) {
											e.printStackTrace();
										}

										// Create the NotificationChannel, but only on API 26+ because
										// the NotificationChannel class is new and not in the support library
										if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
											CharSequence name = mContext.getString(R.string.app_name);
											String description = mContext.getString(R.string.app_name);
											int importance = NotificationManager.IMPORTANCE_DEFAULT;
											NotificationChannel channel = new NotificationChannel(mContext.getString(R.string.app_name), name, importance);
											channel.setDescription(description);
											// Register the channel with the system; you can't change the importance
											// or other notification behaviors after this
											NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
											notificationManager.createNotificationChannel(channel);
										}

										NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, mContext.getString(R.string.app_name))
												.setSmallIcon(android.R.drawable.ic_menu_mylocation)
												.setContentTitle("New Location Update")
												.setContentText("You are at " +mLocation.getLatitude()+","+ mLocation.getLongitude())
												.setPriority(NotificationCompat.PRIORITY_DEFAULT)
												.setStyle(new NotificationCompat.BigTextStyle().bigText("You are at Location " + address));

										NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);

										// notificationId is a unique int for each notification that you must define
										notificationManager.notify(1001, builder.build());

										mFusedLocationClient.removeLocationUpdates(mLocationCallback);
									} else {
										Log.d(TAG, "onComplete: "+ task.getException());
									}
								}
							});
				} catch (SecurityException unlikely) {
					unlikely.printStackTrace();
					Log.e(TAG, "Lost location permission." + unlikely);
				}

				try {
					mFusedLocationClient.requestLocationUpdates(mLocationRequest, null);
				} catch (SecurityException unlikely) {
					//Utils.setRequestingLocationUpdates(this, false);
					unlikely.printStackTrace();
					Log.e(TAG, "Lost location permission. Could not request updates. " + unlikely);
				}
				try {
					registerReceiverGPS();
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					checkOptimization();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				Log.d(TAG, "Time up to get location. Your time is : " + DEFAULT_START_TIME + " to " + DEFAULT_END_TIME);
			}
		} catch (ParseException ignored) {
			ignored.printStackTrace();
		}

		return Result.success();
	}

	private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
		String strAdd = "";
		Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
		try {
			List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
			if (addresses != null) {
				Address returnedAddress = addresses.get(0);
				StringBuilder strReturnedAddress = new StringBuilder();

				for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
					strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
				}
				strAdd = strReturnedAddress.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strAdd;
	}

	public void updateLocationToServer() {
		if(shared_common_pref == null)
			shared_common_pref = new Shared_Common_Pref(mContext);
		JsonArray jsonArray = new JsonArray();
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("SF_code", shared_common_pref.getvalue1(Shared_Common_Pref.Sf_Code));
		jsonObject.addProperty("SF_Name", shared_common_pref.getvalue1(Shared_Common_Pref.Sf_Name));
		jsonObject.addProperty("DvcID", Constants.getDeviceIdNew(mContext));

		ArrayList<HashMap<String, String>> locationList = new ArrayList<>(dbController.getAllLocationData());

		JsonArray jsonArray1 = new JsonArray();
		for(HashMap<String, String> location : locationList){
			try {
				JsonObject jsonObject1 = new JsonObject();
				jsonObject1.addProperty("Latitude", String.valueOf(location.get(DBController.Latitude)));
				jsonObject1.addProperty("Longitude", String.valueOf(location.get(DBController.Longitude)));
				jsonObject1.addProperty("Time", String.valueOf(location.get(DBController.CurrentTime)));
				try {
					jsonObject1.addProperty("Address", !location.get(DBController.Address).equals("") ? location.get(DBController.Address) : getCompleteAddressString(Double.parseDouble(location.get(DBController.Latitude)),Double.parseDouble(location.get(DBController.Longitude))));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
				jsonObject1.addProperty("Accuracy",String.valueOf(location.get(DBController.Accuracy)));
				jsonObject1.addProperty("Speed", String.valueOf(location.get(DBController.Speed)));
				jsonObject1.addProperty("Bearing", String.valueOf(location.get(DBController.Bearing)));

				jsonArray1.add(jsonObject1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		jsonObject.add("TLocations", jsonArray1);
		JsonObject jsonObject2 = new JsonObject();
		jsonObject2.add("TrackLoction", jsonObject);
		jsonArray.add(jsonObject2);

		Log.d(TAG, "updateLocationToServer: jsonArray "+ jsonArray);
		ApiInterface request = ApiClient.getClient().create(ApiInterface.class);
		Call<ResponseBody> call = request.updateLocation(Constants.toRequestBody(jsonArray), "save/trackloc",
				shared_common_pref.getvalue1(Shared_Common_Pref.Div_Code),
				shared_common_pref.getvalue1(Shared_Common_Pref.Sf_Code),
				1,
				"");
//		shared_common_pref.getIntvalue(Shared_Common_Pref.State_Code)
//		Constant.DESIG

		call.enqueue(new Callback<ResponseBody>() {
			@Override
			public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
				try {
					String responseBody = response.body().string();
					Log.d(TAG, "onResponse: updateLocationToServer " + responseBody);

					for(HashMap<String, String> location : locationList){
						try {
							dbController.updateLocation(String.valueOf(location.get(DBController.ID)));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			@Override
			public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
				Log.d(TAG, "updateLocationToServer "+ Objects.requireNonNull(t.getMessage()));
			}
		});
	}

	private void registerReceiverGPS() {
		if (yourReceiver == null) {
			final IntentFilter theFilter = new IntentFilter();
			theFilter.addAction(ACTION_GPS);
			Log.d(TAG,"GPS Called Register");
			mShownSettings=false;
			yourReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					ContentResolver contentResolver = context.getContentResolver();

					int mode = Settings.Secure.getInt(
							contentResolver, Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);

					if(mode == Settings.Secure.LOCATION_MODE_OFF)
						CheckGPSSettings(context);
                   /* if (intent != null) {
                        String s = intent.getAction();
                        if (s != null) {
                            if (s.equals(ACTION_GPS)) {
                                initFusedGPS();
                             //   chkEnaGPS();
                            }
                        }
                    }*/
				}
			};
			Context context = getApplicationContext();
			context.registerReceiver(yourReceiver, theFilter);
		}
	}
	public void CheckGPSSettings(Context context)
	{

		ContentResolver contentResolver = context.getContentResolver();
		// Find out what the settings say about which providers are enabled
		//  String locationMode = "Settings.Secure.LOCATION_MODE_OFF";
		int mode = Settings.Secure.getInt(
				contentResolver, Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);
		if (mode == Settings.Secure.LOCATION_MODE_OFF ) {
			if( !mShownSettings) {
				Log.d(TAG, "GPS OFF");
				ShowLocationWarn();
			}
		}else
		{
			Log.d(TAG,"GPS ON");mShownSettings=false;
		}
	}
	public void ShowLocationWarn(){
		mShownSettings=true;
		LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
				.addLocationRequest(mLocationRequest);
		builder.setAlwaysShow(true);
		SettingsClient settingsClient = LocationServices.getSettingsClient(mContext);
		settingsClient.checkLocationSettings(builder.build())
				.addOnSuccessListener(DMSApplication.getActiveScreen(), new OnSuccessListener<LocationSettingsResponse>() {
					@SuppressLint("MissingPermission")
					@Override
					public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
//  GPS is already enable, callback GPS status through listener
                        /*if (onGpsListener != null) {
                            onGpsListener.gpsStatus(true);
                        }*/
					}
				})
				.addOnFailureListener((Activity) DMSApplication.getActiveScreen(), new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						int statusCode = ((ApiException) e).getStatusCode();
						switch (statusCode) {
							case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
								try {
									// Show the dialog by calling startResolutionForResult(), and check the
									// result in onActivityResult().
									Log.i(TAG, "PendingIntent INSAP.");
									ResolvableApiException rae = (ResolvableApiException) e;
									rae.startResolutionForResult(DMSApplication.getActiveScreen(), 1000);
								} catch (IntentSender.SendIntentException sie) {
									Log.i(TAG, "PendingIntent unable to execute request.");
								}
								break;
							case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
								String errorMessage = "Location settings are inadequate, and cannot be " +
										"fixed here. Fix in Settings.";
								Log.e(TAG, errorMessage);
								Toast.makeText(mContext, errorMessage, Toast.LENGTH_LONG).show();
						}
					}
				});


	}

	private static boolean isWorkScheduled(String tag, Context context) {

		WorkManager instance = WorkManager.getInstance(context);
		ListenableFuture<List<WorkInfo>> statuses = instance.getWorkInfosByTag(tag);

		boolean running = false;
		List<WorkInfo> workInfoList = Collections.emptyList(); // Singleton, no performance penalty

		try {
			workInfoList = statuses.get();
		} catch (ExecutionException e) {
			Log.d(TAG, "ExecutionException in isWorkScheduled: " + e);
		} catch (InterruptedException e) {
			Log.d(TAG, "InterruptedException in isWorkScheduled: " + e);
		}

		for (WorkInfo workInfo : workInfoList) {
			WorkInfo.State state = workInfo.getState();
			running = running || (state == WorkInfo.State.RUNNING | state == WorkInfo.State.ENQUEUED);
		}
		return running;

	}


	private static void startLocationTracker() {
		PeriodicWorkRequest periodicWork = new PeriodicWorkRequest.Builder(MyLocationWorker.class, 15, TimeUnit.MINUTES)
				.addTag(TAG)
				.build();
		WorkManager.getInstance().enqueueUniquePeriodicWork("Location", ExistingPeriodicWorkPolicy.REPLACE, periodicWork);

	}
	public static void stopLocationTracker() {
		WorkManager.getInstance().cancelAllWorkByTag(TAG);

	}

	public static void checkOptimization() {
		try {

			PowerManager pm = (PowerManager) DMSApplication.getActiveScreen().getSystemService(POWER_SERVICE);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				if (pm != null && !pm.isIgnoringBatteryOptimizations(DMSApplication.getActiveScreen().getPackageName())) {

					askIgnoreOptimization();
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

	private static void askIgnoreOptimization() {

//		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
			@SuppressLint({"BatteryLife", "InlinedApi"})
			Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
			intent.setData(Uri.parse("package:" + DMSApplication.getActiveScreen().getPackageName()));
			DMSApplication.getActiveScreen().startActivityForResult(intent, 111);
		/*} else {
		}*/
	}
	private static void redirectToOptimizationSettings() {

	String name = DMSApplication.getActiveScreen().getString(R.string.app_name);
					Toast.makeText(DMSApplication.getActiveScreen(), "Battery optimization -> All apps -> "+name+" -> Don't optimize", Toast.LENGTH_LONG).show();

					Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
					DMSApplication.getActiveScreen().startActivity(intent);
	}
}