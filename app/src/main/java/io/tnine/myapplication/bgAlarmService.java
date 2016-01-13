package io.tnine.myapplication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

public class bgAlarmService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public bgAlarmService() {

    }

    GoogleApiClient mGoogleApiClient;
    LatLng destloc = fragment_console.destloc;
    double updLat, updLng, destlat = destloc.latitude, destlng = destloc.longitude;
    double rad = fragment_console.rad;
    LocationRequest mLocationRequest;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {



        Runnable r = new Runnable() {
            @Override
            public void run() {
                if (mGoogleApiClient == null) {
                    mGoogleApiClient = new GoogleApiClient.Builder(bgAlarmService.this)
                            .addConnectionCallbacks(bgAlarmService.this)
                            .addOnConnectionFailedListener(bgAlarmService.this)
                            .addApi(LocationServices.API)
                            .build();
                }

                mGoogleApiClient.connect();
            }
        };

        Thread bgThread = new Thread(r);
        bgThread.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (fragment_console.mRequestingLocationUpdates) {
            createLocationRequest();
            startLocationUpdates();
        }
    }

    protected void startLocationUpdates() {
        try{
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }catch (SecurityException e){

        }

    }

    @Override
    public void onLocationChanged(Location location) {
        double distance;
        Location locationA = new Location("");
        locationA.setLatitude(destlat);
        locationA.setLongitude(destlng);
        Location locationB = new Location("");
        locationB.setLatitude(updLat);
        locationB.setLongitude(updLng);
        distance = locationA.distanceTo(locationB);
        if(distance<rad && fragment_console.alarmHostIsService){
            Vibrator v = fragment_console.vi;
            Ringtone r = fragment_console.r;
            long[] pattern = {0, 600, 1000};
            if (!r.isPlaying()) {
                r.play();
                v.vibrate(pattern, 0);
            }
            fragment_console.alarm = false;
            fragment_console.alarmHostIsService = false;
            Intent i = new Intent(this, fragment_console.class);
            startActivity(i);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public class MyBinder extends Binder {
        public bgAlarmService getService() {
            return bgAlarmService.this;
        }
    }




}
