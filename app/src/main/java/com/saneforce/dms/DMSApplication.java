package com.saneforce.dms;


import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import com.google.firebase.FirebaseApp;

public class DMSApplication extends Application {
//implements Configuration.Provider
    private static final String TAG = DMSApplication.class.getSimpleName();
    static DMSApplication sharedInstance;

//    double latitude = 0.0;
//    double longitude = 0.0;
    public static Activity activeScreen;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);

        sharedInstance = this;

        setupActivityListener();
    }



    public static DMSApplication getApplication() {
        return sharedInstance;
    }

    public static Activity getActiveScreen() {
        return activeScreen;
    }

/*    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.DEBUG)
                .build();
    }*/


    private void setupActivityListener() {

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                activeScreen = activity;
            }

            @Override
            public void onActivityStarted(Activity activity) {


            }

            @Override
            public void onActivityResumed(Activity activity) {
                activeScreen = activity;

            }

            @Override
            public void onActivityPaused(Activity activity) {
                //  activeActivity = null;
            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                //unregisterReceiver(mNetworkReceiver);
            }
        });
    }


}
