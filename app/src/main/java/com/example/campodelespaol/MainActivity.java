package com.example.campodelespaol;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.campodelespaol.data.Model.Alert;
import com.example.campodelespaol.data.Model.LocationTracker;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity{

    private ImageView imageView;
    private TextView distanceView;

    private LocationTracker location;
    private double[] coordsCampo = { 41.347898, 2.075533 };//latitud,longitud CAMPO
    private double brng;
    private double distance;

    private SensorManager sensorManager;
    private Sensor sensor;
    private Sensor sensorAccelerometer;
    private Sensor sensorMagneticField;

    private float[] floatGravity = new float[3];
    private float[] floatGeoMagnetic = new float[3];

    private float[] floatOrientation = new float[3];
    private float[] floatRotationMatrix = new float[9];
    private Handler handler;
    private Runnable handlerTask;

    private static final DecimalFormat df = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView4);
        distanceView = findViewById(R.id.textDistance);

        // Permisos
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //Calcular direccion
            StartCompass();
            StartCalculating();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {


                DialogFragment alertFrag = new Alert(this);
                alertFrag.show(getSupportFragmentManager(), "Permisos de ubicación");
                // Mostrar diálogo explicativo
            }else{
                // Solicitar permiso
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    public void StartCompass(){

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);


        SensorEventListener sensorEventListenerAccelrometer = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                floatGravity = event.values;

                SensorManager.getRotationMatrix(floatRotationMatrix, null, floatGravity, floatGeoMagnetic);
                SensorManager.getOrientation(floatRotationMatrix, floatOrientation);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

        SensorEventListener sensorEventListenerMagneticField = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                floatGeoMagnetic = event.values;

                SensorManager.getRotationMatrix(floatRotationMatrix, null, floatGravity, floatGeoMagnetic);
                SensorManager.getOrientation(floatRotationMatrix, floatOrientation);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
        sensorManager.registerListener(sensorEventListenerAccelrometer, sensorAccelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(sensorEventListenerMagneticField, sensorMagneticField, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //If user presses allow
                    StartCompass();
                    StartCalculating();
                } else {
                    //If user presses deny
                }
                break;
            }
        }
    }

    private void StartCalculating() {
        //DialogFragment alertFrag = new Alert();
        //alertFrag.show(getSupportFragmentManager(), "missiles");

        location = new LocationTracker(MainActivity.this);
        //double[] coordsLocation = location.GetLocation();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Recalculate();
            }
        }, 0, 100);//wait 0 ms before doing the action and do it evry 1000ms (1second)

        handler = new Handler();
        handlerTask = new Runnable()
        {
            @Override
            public void run() {
                // do something
                GetDistance(location.GetLocation());
                handler.postDelayed(handlerTask, 2000);
            }
        };
        handlerTask.run();
    }

    private void Recalculate(){
        //Toast.makeText(this, "bearing: ", Toast.LENGTH_LONG).show();
        //Log.d("test", "test:");

        double[] locCoords = location.locat;

        double dLon = (coordsCampo[1] - locCoords[1]);
        double y = Math.sin(dLon) * Math.cos(coordsCampo[0]);
        double x = Math.cos(locCoords[0])*Math.sin(coordsCampo[0]) - Math.sin(locCoords[0])*Math.cos(coordsCampo[0])*Math.cos(dLon);
        brng = Math.toDegrees((Math.atan2(y, x)));
        brng = (360 - ((brng + 360) % 360));

        //imageView.setRotation((float) (-floatOrientation[0]*180/3.14159));
        double north = ((-floatOrientation[0]*180/3.14159f) + 360) % 360;
        imageView.setRotation((float) (north + ((float) brng)));
    }

    private void GetDistance(double[] coordsLocation){

        double lon1 = Math.toRadians(coordsLocation[1]);
        double lon2 = Math.toRadians(coordsCampo[1]);
        double lat1 = Math.toRadians(coordsLocation[0]);
        double lat2 = Math.toRadians(coordsCampo[0]);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2),2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371;
        distance = (c * r) ;//KM

        df.setRoundingMode(RoundingMode.DOWN);
        String mesure = " Kilometros";
        if(distance < 1){
            distance = distance * 1000;
            mesure = " Metros";
        }
        distanceView.setText("Distancia: " + df.format(distance) + mesure);
        Log.d("dist", "dist");
    }

}