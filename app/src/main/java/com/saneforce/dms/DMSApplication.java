package com.saneforce.dms;


import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.work.Configuration;

import com.google.firebase.FirebaseApp;
import com.saneforce.dms.activity.DashBoardActivity;
import com.saneforce.dms.cromappwhitelist.AppWhitelist;
import com.saneforce.dms.utils.Common_Class;

public class DMSApplication extends Application  implements LifecycleObserver, Configuration.Provider  {

    private static final String TAG = DMSApplication.class.getSimpleName();
    static DMSApplication sharedInstance;

    
    public static Activity activeScreen;
    public boolean isAppInForeground = false;
    Common_Class common_class ;
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);

        sharedInstance = this;
        setupActivityListener();
        common_class= new Common_Class(sharedInstance);
        try {
            common_class.registerReceiverGPS(sharedInstance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
//        AppWhitelist.settingForAutoStart(this);
//        AppWhitelist.settingForBatterySaver(this);
//        AppWhitelist.settingForMemoryAcceleration(this);
//        AppWhitelist.settingForNotification(this);

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

            }

        });

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        //App in background
        isAppInForeground = false;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {
        // App in foreground
        isAppInForeground = true;
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        unregisterReceiver(common_class.yourReceiver);
    }

    @NonNull
    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.DEBUG)
                .build();
    }
}
