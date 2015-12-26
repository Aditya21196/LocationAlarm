package io.tnine.myapplication;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;


public class fragment_console extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener {


    //declaration of app constituents
    private Button alarmbutton;
    private boolean result = true;

    //if result is true, destination fragment is seen
    //if result is false, alarm settings fragment is seen

    private GoogleApiClient mGoogleApiClient ;


    View frag1;
    View frag2;
    GoogleMap map;

    double destlat;
    double destlng;

    double latilast,longitlast;

    Marker marker;
    Marker locmarker;

    Location mLastLocation;
    LocationRequest mLocationRequest;
    Location mCurrentLocation;
    String mLastUpdateTime;
    String REQUESTING_LOCATION_UPDATES_KEY ;
    boolean mRequestingLocationUpdates = true;
    String LOCATION_KEY;
    LatLng locll;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_console);

        updateValuesFromBundle(savedInstanceState);



        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        final PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.


                try {
                    geoLocate(place.getAddress().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.

            }
        });


        //declarations
        alarmbutton = (Button) findViewById(R.id.setalarm_button);
        frag1 = findViewById(R.id.destination_fragment);
        frag2 = findViewById(R.id.alarmsetfragment);
//code to obtain google map named map
        try {
            if (map == null) {
                map = ((MapFragment) getFragmentManager()
                        .findFragmentById(R.id.map)).getMap();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //code to turn some instance variables invisible when program starts
        alarmbutton.setVisibility(View.GONE);
        frag2.setVisibility(View.GONE);

        //Initial On click listeners
        alarmbutton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        switchfrags();
                        switchalarmbutton();
                    }
                }
        );



    }


    //switch methods
    public void switchfrags() {

        if (result == true) {
            frag1.setVisibility(View.GONE);
            frag2.setVisibility(View.VISIBLE);
        } else {
            frag1.setVisibility(View.VISIBLE);
            frag2.setVisibility(View.GONE);
        }
        result = !result;
    }

    public void switchalarmbutton() {
        if (alarmbutton.getVisibility() == View.GONE) {
            alarmbutton.setVisibility(View.VISIBLE);
        } else {
            alarmbutton.setVisibility(View.GONE);
        }
    }


    public void geoLocate(String location) throws IOException {

        Geocoder gc = new Geocoder(this);
        List<Address> list = gc.getFromLocationName(location, 1);
        Address add = list.get(0);
        String Locality = add.getLocality();
        Toast.makeText(this, Locality, Toast.LENGTH_LONG).show();

        destlat = add.getLatitude();
        destlng = add.getLongitude();

        gotoLocation(destlat, destlng, 14);
        String locality = add.getLocality();
        if (marker != null) {
            marker.remove();
        }

        MarkerOptions options = new MarkerOptions()
                .title(locality)
                .position(new LatLng(destlat, destlng));

        marker = map.addMarker(options);
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnected(Bundle connectionHint) {
       try{
           mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                   mGoogleApiClient);
           if (mLastLocation != null) {

               latilast = mLastLocation.getLatitude();
               longitlast = mLastLocation.getLongitude();
               locll = new LatLng(latilast,longitlast);

               MarkerOptions locoptions = new MarkerOptions()
                       .title("You are here")
                       .position(locll);
               if (locmarker != null){
                   locmarker.remove();
               }
               locmarker = map.addMarker(locoptions);
               gotoLocation(latilast,longitlast,14);

           }
       }catch(SecurityException e){
           //do nothing
       }
        if(mRequestingLocationUpdates){
            createLocationRequest();
            startLocationUpdates();
        }
    }




    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void gotoLocation(double lat, double lng, int Zoom) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, Zoom);
        map.animateCamera(update);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient .disconnect();
        super.onStop();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {

        try{
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);

        }catch (SecurityException e){
            //code to ask for permission
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        locll = new LatLng(location.getLatitude(),location.getLongitude());

            MarkerOptions locoptions = new MarkerOptions()
                    .title("You are here")
                    .position(locll);
        if(locmarker != null){
            locmarker.remove();
        }
            locmarker = map.addMarker(locoptions);
            startLocationUpdates();

    }
    //stopping location updates
    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    //resuming
    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
                mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);;
        super.onSaveInstanceState(savedInstanceState);
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and
            // make sure that the Start Updates and Stop Updates buttons are
            // correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);
            }

            // Update the value of mCurrentLocation from the Bundle and update the
            // UI to show the correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that
                // mCurrentLocation is not null.
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

        }
    }
}




