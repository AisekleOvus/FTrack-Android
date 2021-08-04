package com.ftrack;

import android.annotation.SuppressLint;

import android.app.Activity;
import android.content.Context;
import android.location.GnssStatus;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;


import org.json.JSONObject;


import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;



public class FtrackLocationListener {
    private static FtrackLocationListener instance;
    private Context context;
    private String accuracy;
    private String latitude;
    private String longitude;
    private String provider;
    private int setellites;

    private LocationManager locationManager;
    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {

            showLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {

            checkEnabled();
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onProviderEnabled(String provider) {
            checkEnabled();
            showLocation(locationManager.getLastKnownLocation(provider));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                setellites = status;
            }
        }
    };


    private FtrackLocationListener(Context context) {
        locationManager = (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);
        this.context = context;
        this.setellites = 0;
    }

    public static FtrackLocationListener getInstance(Context context) {
        if (instance == null)
            instance = new FtrackLocationListener(context);
        return instance;
    }

    @SuppressLint("MissingPermission")
    public void LocationRequest() {

 /*       if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION},1);
            return;
        }*/
        checkEnabled();
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                120000,
                10,
                locationListener);
        locationManager.requestLocationUpdates(
              LocationManager.NETWORK_PROVIDER,
                120000,
                10,
                locationListener);

    }

    private void showLocation(Location location) {
        if (location == null)
            return;
/*        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {

            ((TextView)((Activity) context).findViewById(R.id.textViewGPSLat)).setText(String.valueOf(location.getLatitude()));
            ((TextView)((Activity) context).findViewById(R.id.textViewGPSLong)).setText(String.valueOf(location.getLongitude()));
        } else if (location.getProvider().equals(
                LocationManager.NETWORK_PROVIDER)) {
            ((TextView)((Activity) context).findViewById(R.id.textViewNTWRKLat)).setText(String.valueOf(location.getLatitude()));
            ((TextView)((Activity) context).findViewById(R.id.textViewNTWRKLong)).setText(String.valueOf(location.getLongitude()));
        }*/
        try {
            /*if (LocationManager.GPS_PROVIDER.equals(location.getProvider()) && setellites <= 2) {
                return;
            }*/
                sendCoordinates(location);


        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    private void checkEnabled() {
        ((TextView)((Activity) context).findViewById(R.id.textViewSat)).setText(
                "Спутники: "
                        + String.valueOf(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        );
        ((TextView)((Activity) context).findViewById(R.id.textViewNet)).setText(
                "Данные сети: "
                + String.valueOf(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        );
    }
    private void sendCoordinates(Location location) {
        accuracy = String.valueOf(location.getAccuracy());
        longitude = String.valueOf(location.getLongitude());
        latitude = String.valueOf(location.getLatitude());
        provider = location.getProvider();

        if(LocationManager.NETWORK_PROVIDER.equals(provider)) {
            ((TextView)((Activity) context).findViewById(R.id.textViewNETLongLat)).setText(longitude + ", "+ latitude);
            ((TextView)((Activity) context).findViewById(R.id.textViewNetAcu)).setText(
                    "Точность: " + accuracy);
        }
        if(LocationManager.GPS_PROVIDER.equals(provider)) {
            ((TextView)((Activity) context).findViewById(R.id.textViewGPSLongLat)).setText(longitude + ", "+ latitude);
            ((TextView)((Activity) context).findViewById(R.id.textViewGPSAcu)).setText(
                    "Точность: " + accuracy);
        }


        PostWork postWork = new PostWork();
        postWork.execute();

    }
    class PostWork extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void...voids) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("longitude",longitude);
                jsonObject.put("latitude",latitude );
                jsonObject.put("accuracy", accuracy);
                jsonObject.put("provider", provider);
                URL url = new URL( "http://torodoro.ru:8080/coordinates");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setDoOutput(true);
                conn.setInstanceFollowRedirects(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setUseCaches(false);
                OutputStreamWriter wr= new OutputStreamWriter(conn.getOutputStream());
                wr.write(jsonObject.toString());
                wr.flush();
                wr.close();
                System.out.println(conn.getResponseCode());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
