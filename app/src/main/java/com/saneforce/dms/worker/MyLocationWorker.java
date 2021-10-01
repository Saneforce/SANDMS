package com.saneforce.dms.worker;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.BackoffPolicy;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
/*import androidx.work.BackoffPolicy;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkerParameters;
import androidx.work.BackoffPolicy;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;*/

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.saneforce.dms.R;
import com.saneforce.dms.listener.ApiInterface;
import com.saneforce.dms.sqlite.DBController;
import com.saneforce.dms.utils.ApiClient;
import com.saneforce.dms.utils.Common_Class;
import com.saneforce.dms.utils.Constant;
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

    private static int BACKOFF_DELAY_SECONDS = 2;
    boolean returnRetry = false;
    public static void createMyLocationWorker(Context context){
        Log.d(TAG, "createMyLocationWorker: "+ isWorkScheduled(TAG, context));
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

/*
        try {

            setForegroundAsync(createForegroundInfo("50"));
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        if(mLocationRequest == null){
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
            mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }

        if(dbController == null)
            dbController = new DBController(mContext);

        if(Common_Class.isGpsON(mContext)){

            try {

                if(shared_common_pref == null)
                    shared_common_pref = new Shared_Common_Pref(mContext);
                double lat = 0;
                try {
                    if(mLocation!=null)
                    lat = mLocation.getLatitude();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                double lng = 0;
                try {
                    if(mLocation!=null)
                    lng = mLocation.getLongitude();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d(TAG, "Location : " + mLocation);
                shared_common_pref.save(String.valueOf(Calendar.getInstance().getTimeInMillis()), ""+ lat+ ", "+ lng);
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                Date currentDate = dateFormat.parse(formattedDate);
                Date startDate = dateFormat.parse(DEFAULT_START_TIME);
                Date endDate = dateFormat.parse(DEFAULT_END_TIME);
// && currentDate.after(startDate) && currentDate.before(endDate)

                if (shared_common_pref!=null && !shared_common_pref.getvalue1("Login_details").equals("")) {

                    if(mFusedLocationClient==null){
                        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
                        mLocationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                super.onLocationResult(locationResult);
                            }
                        };
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

                                                if(dbController == null)
                                                    dbController = new DBController(mContext);

                                                if(dbController.isValidLocation(mLocation.getTime())) {
                                                    dbController.addLocation(mLocation, "0", address);
                                                    Common_Class.createNotification( mContext, "You are at " +mLocation.getLatitude()+","+ mLocation.getLongitude(),address);
                                                }else{
                                                    returnRetry = true;
                                                }

                                                if(Constant.isInternetAvailable(mContext))
                                                    updateLocationToServer();

                                                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                                            } else {
                                                returnRetry = true;
                                                Log.d(TAG, "onComplete: "+ task.getException());
                                            }
                                        }
                                    });
                        } catch (SecurityException unlikely) {
                            unlikely.printStackTrace();
                            returnRetry = true;
                            Log.e(TAG, "Lost location permission." + unlikely);
                        }
                    }


                    try {
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, null);
                    } catch (SecurityException unlikely) {
                        //Utils.setRequestingLocationUpdates(this, false);
                        unlikely.printStackTrace();
                        returnRetry = true;
                        Log.e(TAG, "Lost location permission. Could not request updates. " + unlikely);
                    }

                } else {
                    returnRetry = true;
                    Log.d(TAG, "Time up to get location. Your time is : " + DEFAULT_START_TIME + " to " + DEFAULT_END_TIME);
                }
            } catch (ParseException ignored) {
                ignored.printStackTrace();
                returnRetry = true;
            }

        }else {
            returnRetry = true;
            Common_Class.createNotification(mContext,mContext.getString(R.string.gps_require_info),"");
        }


        if(returnRetry)
            return Result.retry();
        else
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
        ArrayList<HashMap<String, String>> locationList = new ArrayList<>(dbController.getAllLocationData(true));
        if(locationList.size() ==0)
            return;

        JsonArray jsonArray = new JsonArray();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("SF_code", shared_common_pref.getvalue1(Shared_Common_Pref.Sf_Code));
        jsonObject.addProperty("SF_Name", shared_common_pref.getvalue1(Shared_Common_Pref.Sf_Name));
        jsonObject.addProperty("DvcID", Constant.getDeviceIdNew(mContext));



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
        Call<ResponseBody> call = request.updateLocation(Constant.toRequestBody(jsonArray), "save/trackloc",
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
            running = (state == WorkInfo.State.RUNNING | state == WorkInfo.State.ENQUEUED);
        }
        return running;

    }


    private static void startLocationTracker() {
        PeriodicWorkRequest periodicWork = new PeriodicWorkRequest.Builder(MyLocationWorker.class, 15, TimeUnit.MINUTES)
                .addTag(TAG)
                .setBackoffCriteria(
                        BackoffPolicy.EXPONENTIAL,
                        BACKOFF_DELAY_SECONDS,
                        TimeUnit.MINUTES
                )
                .build();
        WorkManager.getInstance().enqueueUniquePeriodicWork("Location", ExistingPeriodicWorkPolicy.REPLACE, periodicWork);

    }
    public static void stopLocationTracker() {
        WorkManager.getInstance().cancelAllWorkByTag(TAG);
    }

    @Override
    public boolean isRunInForeground() {
        return true;
    }
   /* private ForegroundInfo createForegroundInfo(@NonNull String progress) {
        // Build a notification...

        String id = "100";
        String title = "San DMS";
        String cancel = "Cancel";
        // This PendingIntent can be used to cancel the worker
        PendingIntent intent = WorkManager.getInstance(mContext)
                .createCancelPendingIntent(getId());
        *//*NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a Notification channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("sandms", "sandms", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);

        }*//*

        Notification notification = new NotificationCompat.Builder(mContext, id)
                .setContentTitle(title)
                .setTicker(title)
                .setContentText(progress)
                .setOngoing(true)
                // Add the cancel action to the notification which can
                // be used to cancel the worker
                .addAction(android.R.drawable.ic_delete, cancel, intent)
                .build();

        return new ForegroundInfo(111, notification,
                FOREGROUND_SERVICE_TYPE_LOCATION);
    }*/
}