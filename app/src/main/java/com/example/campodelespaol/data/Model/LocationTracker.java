package com.example.campodelespaol.data.Model;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.util.List;

public class LocationTracker extends Service implements LocationListener {

    private final Context mContext;
    public double[] locat = new double[3];

    public LocationTracker(Context context){
        this.mContext = context;
        locat = GetLocation();
    }

    public double[] GetLocation(){
        LocationManager lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            boolean isGpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if(isGpsEnabled){
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);

                if (lm != null) {
                    Location location = lm
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if (location != null) {
                        Double latitude = location.getLatitude();
                        Double longitude = location.getLongitude();
                        Double altitude = location.getAltitude();
                        locat[0] = latitude;
                        locat[1] = longitude;
                        locat[2] = altitude;
                    }
                }
            }else{
                Toast.makeText(mContext, "GPS Desactivado...", Toast.LENGTH_SHORT).show();
            }
        }
        return locat;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        //Toast.makeText(mContext, "changed", Toast.LENGTH_SHORT).show();
        //locat = GetLocation();
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {

    }

    @Override
    public void onFlushComplete(int requestCode) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
