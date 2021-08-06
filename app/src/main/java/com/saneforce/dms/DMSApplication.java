package com.saneforce.dms;


import android.app.Application;

import com.google.firebase.FirebaseApp;
public class DMSApplication extends Application {

    private static final String TAG = DMSApplication.class.getSimpleName();
    static DMSApplication sharedInstance;

//    double latitude = 0.0;
//    double longitude = 0.0;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);

        sharedInstance = this;
    }

    public static DMSApplication getApplication() {
        return sharedInstance;
    }


}
