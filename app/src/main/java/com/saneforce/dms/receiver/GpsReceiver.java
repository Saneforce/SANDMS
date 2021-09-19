package com.saneforce.dms.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.saneforce.dms.DMSApplication;
import com.saneforce.dms.R;
import com.saneforce.dms.sqlite.DBController;
import com.saneforce.dms.utils.Common_Class;
import com.saneforce.dms.utils.TimeUtils;

public class GpsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().matches("android.location.GPS_ENABLED_CHANGE"))
        {
            if(!Common_Class.isGpsON(context)) {
                if(DMSApplication.getApplication().isAppInForeground)
                    new Common_Class(context).ShowLocationWarn(context);
                Common_Class.createNotification( context,context.getString(R.string.gps_require_info),"");

                try {
                    Location location = new Location("");
                    location.setLatitude(-1);
                    location.setLongitude(-1);
                    location.setAccuracy(-1);
                    location.setBearing(-1);
                    location.setSpeed(-1);
                    location.setTime(TimeUtils.getTimeStamp(TimeUtils.getCurrentTime(TimeUtils.FORMAT3), TimeUtils.FORMAT3));

                    new DBController(context).addLocation(location, "0", "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
