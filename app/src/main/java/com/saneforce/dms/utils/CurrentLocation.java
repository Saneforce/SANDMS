package com.saneforce.dms.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

public class CurrentLocation extends Activity {

    private static final int REQUEST_LOCATION = 1;
    Button btnGetLocation;
    LocationManager locationManager;
    String latitude, longitude;
    Activity activity;
    Context _context;

    public CurrentLocation(Activity activity, Context _context) {
        this.activity = activity;
        this._context = _context;
    }

    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Enable GPS Location").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = null;
                try {
                    intent = new Intent(activity, Class.forName(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public String[] getLocation() {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        String[] str = new String[2];
        if (ActivityCompat.checkSelfPermission(
                activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                double lat = locationGPS.getLatitude();
                double longi = locationGPS.getLongitude();
                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);

                str[0] = latitude;
                str[1] = longitude;
                Log.v("Your_Location: ", "\n" + "Latitude: " + latitude + "\n" + "Longitude: " + longitude);
            } else {
                Toast.makeText(activity, "Unable to find location.", Toast.LENGTH_SHORT).show();
                OnGPS();
            }
        }
        Log.v("CHECKING_DATA_QQQ", String.valueOf(str));
        return str;
        //return latitude +" : "+ longitude;
    }

    private void GetValue(String lat, String lon) {

    }

}
