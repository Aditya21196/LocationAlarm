package io.tnine.myapplication;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class fragment_console extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener {


    //declaration of app constituents
    private Button alarmbutton;
    private boolean result = true;


    //if result is true, destination fragment is seen
    //if result is false, alarm settings fragment is seen

    private GoogleApiClient mGoogleApiClient ;

    Timer timer;
    TimerTask timerTask;


    View frag1;
    View frag2;
    public GoogleMap map;

    double destlat;
    double destlng;

    double latilast,longitlast;
    double updLat, updLng;

    public Marker marker;
    public Marker locmarker = null;
    public Marker locmarker2;
    EditText current_distance;

    Location mLastLocation;
    LocationRequest mLocationRequest;
    Location mCurrentLocation;
    String REQUESTING_LOCATION_UPDATES_KEY ;
    boolean mRequestingLocationUpdates = true;
    String LOCATION_KEY;
    LatLng locll;
    LatLng destloc;

    boolean alarm = false;
    Ringtone r;
    Vibrator vi;
    boolean vibrating = false;

    boolean paused = false;
    boolean pausedOnce = false;
    final Handler handler = new Handler();

    public Circle circle;
    double rad = 500;

    boolean osVersionIs6orHigh = false;

    public static ArrayList<String> locNamesAL = new ArrayList<>();
    private String m_Text = "";
    public static int destinationCounter = 0;
    MyDBHandler dbHandler;
    View addDestToFavorites;
    View accessFavorites;
    View soundSettingsButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkFeatureAvailability();
        setContentView(R.layout.activity_fragment_console);


        paused = false;


        if (mGoogleApiClient == null) {
            // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(AppIndex.API).build();
        } else {
            startLocationUpdates();
        }

        final PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.


                try {
                    geoLocate(place.getAddress().toString());
                    startLocationUpdates();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Couldn't Find this location.Please search other nearby location.", Toast.LENGTH_LONG).show();
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
        final EditText radius = (EditText) findViewById(R.id.radiusValue);
        current_distance = (EditText) findViewById(R.id.currentDistance);
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        vi = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        addDestToFavorites = (View)findViewById(R.id.menu_item1);
        accessFavorites = (View)findViewById(R.id.menu_item2);
        soundSettingsButton = (View)findViewById(R.id.menu_item3);

        dbHandler = new MyDBHandler(fragment_console.this, null, null, 1);



        //code to obtain google map named map
        try {
            if (map == null) {
                map = ((MapFragment) getFragmentManager()
                        .findFragmentById(R.id.map)).getMap();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        map.setMyLocationEnabled(true);
        Toast.makeText(fragment_console.this, "Long Press on the map to set that place as your destination or use search box", Toast.LENGTH_LONG).show();

        Button OKButton = (Button) findViewById(R.id.OKButton);

        //code to turn some instance variables invisible when program starts
        alarmbutton.setVisibility(View.GONE);
        frag2.setVisibility(View.GONE);
        OKButton.setVisibility(View.GONE);
        current_distance.setVisibility(View.GONE);


        //Initial On click listeners
        alarmbutton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        vi.cancel();
                        if (r.isPlaying() == true) {
                            r.stop();

                            removeCircle();
                            alarmbutton.setVisibility(View.GONE);
                            alarmbutton.setText("SET ALARM");
                            marker.remove();
                            marker = null;
                            destinationCounter = 0;
                            current_distance.setVisibility(View.GONE);
                        } else {
                            if (alarmbutton.getText() != "RESET ALARM") {
                                switchfrags();
                                switchalarmbutton();
                                setCircle(rad, destloc);
                                destinationCounter = 1;
                            } else {
                                removeCircle();
                                alarm = false;
                                alarmbutton.setText("SET ALARM");
                                switchalarmbutton();
                                marker.remove();
                                marker = null;
                                current_distance.setVisibility(View.GONE);
                                destinationCounter = 0;
                            }
                        }
                    }
                }
        );

        radius.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (radius.getText().toString().length() != 0) {
                    String no = radius.getText().toString();
                    rad = Integer.parseInt(no);
                }

                setCircle(rad, destloc);
            }
        });


        OKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchfrags();
                Toast.makeText(fragment_console.this, "The alarm has been set", Toast.LENGTH_SHORT).show();
                alarmbutton.setText("RESET ALARM");
                switchalarmbutton();
                alarm = true;
                vi.cancel();
            }
        });

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                destlng = latLng.longitude;
                destlat = latLng.latitude;

                if (current_distance.getVisibility() == View.VISIBLE) {
                    current_distance.setVisibility(View.GONE);
                }

                if (frag1.getVisibility() == View.GONE) {
                    switchfrags();
                }

                destloc = new LatLng(destlat, destlng);

                if (marker != null) {
                    marker.remove();
                }
                if (circle != null) {
                    circle.remove();
                    circle = null;
                }

                MarkerOptions options = new MarkerOptions()
                        .title("your destination")
                        .position(destloc)
                        .position(destloc)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.dest_marker));
                ;

                marker = map.addMarker(options);
                onDestinationChanged();
            }
        });

        addDestToFavorites.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        if (destinationCounter == 1){
                            addToFavorites();
                        }else {
                            Toast.makeText(fragment_console.this,"Select a destination to add to favorites.",Toast.LENGTH_LONG).show();
                        }
                    }});

        accessFavorites.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        AlertDialog.Builder builderSingle = new AlertDialog.Builder(fragment_console.this);
                        builderSingle.setIcon(R.drawable.dest_marker);
                        builderSingle.setTitle("Select Your Destination:-");

                        ArrayList<String> namesAL = dbHandler.getArrayListOFnames();
                        int i;

                        final ListAdapter m_Adapter = new ArrayAdapter<String>(fragment_console.this,android.R.layout.simple_expandable_list_item_1, namesAL);

                        builderSingle.setAdapter(
                                m_Adapter,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                        builderSingle.show();
                    }
                }
        );





    }




    //switch methods
    public void switchfrags() {

        Button OKButton = (Button) findViewById(R.id.OKButton);
        current_distance = (EditText) findViewById(R.id.currentDistance);

        if (result == true) {
            frag1.setVisibility(View.GONE);
            frag2.setVisibility(View.VISIBLE);
            OKButton.setVisibility(View.VISIBLE);
            current_distance.setVisibility(View.VISIBLE);
        } else {
            frag1.setVisibility(View.VISIBLE);
            frag2.setVisibility(View.GONE);
            OKButton.setVisibility(View.VISIBLE);
            if (alarm){
                current_distance.setVisibility(View.VISIBLE);
            }
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
        String Local=add.getAddressLine(0);



        Toast.makeText(this,Local, Toast.LENGTH_LONG).show();

        destlat = add.getLatitude();
        destlng = add.getLongitude();

        if (current_distance.getVisibility() == View.VISIBLE){
            current_distance.setVisibility(View.GONE);
        }

        destloc = new LatLng(destlat,destlng);

        if (frag1.getVisibility() == View.GONE){
            switchfrags();
        }

        gotoLocation(destlat, destlng, 14);
        String locality = add.getLocality();
        if (marker != null) {
            marker.remove();
        }
        if (circle != null){
            circle.remove();
            circle = null;
        }

        MarkerOptions options = new MarkerOptions()
                .title("Your destination")
                .position(destloc)
                .position(destloc)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.dest_marker));

        marker = map.addMarker(options);
        onDestinationChanged();
    }

    public void minimizeApp() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
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
                        .position(locll)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.mapmarker));
                if (locmarker != null){
                    locmarker.remove();
                }
                locmarker = map.addMarker(locoptions);
                gotoLocation(latilast, longitlast, 14);
            }
        }catch(SecurityException e){
            osVersionIs6orHigh = true;
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
        paused = false;


        if (pausedOnce) {
            checkFeatureAvailability();
            startLocationUpdates();
        }


        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "fragment_console Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://io.tnine.myapplication/http/host/path")
        );
        AppIndex.AppIndexApi.start(mGoogleApiClient, viewAction);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "fragment_console Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://io.tnine.myapplication/http/host/path")
        );
        AppIndex.AppIndexApi.end(mGoogleApiClient, viewAction);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient.disconnect();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {


        createLocationRequest();

        try{
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);


        }catch (SecurityException e){
            //code to ask for permission
        }


    }

    @Override
    public void onLocationChanged(Location location) {
        updLat = location.getLatitude();
        updLng = location.getLongitude();
        locll = new LatLng(updLat,updLng);

        MarkerOptions locoptions = new MarkerOptions()
                .title("You are here")
                .position(locll)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mapmarker));

        if (locmarker == null){
            gotoLocation(updLat, updLng, 15);
        }

        if(locmarker != null){
            locmarker2 = locmarker;
            locmarker2 = map.addMarker(locoptions);
            locmarker.remove();
            locmarker = null;
        }
        locmarker = map.addMarker(locoptions);
        if(locmarker2 != null){
            locmarker2.remove();
            locmarker2 = null;
        }



        if (alarm){
            double distance;
            Location locationA = new Location("");
            locationA.setLatitude(destlat);
            locationA.setLongitude(destlng);
            Location locationB = new Location("");
            locationB.setLatitude(updLat);
            locationB.setLongitude(updLng);
            distance = locationA.distanceTo(locationB);
            current_distance.setText("Current distance: " + Math.round(distance) + " m");
            if(distance<rad){
                alertUser();
                Toast.makeText(this,"Destination is now within your range",Toast.LENGTH_LONG).show();
                alarm = false;
            }
        }

        if (frag2.getVisibility() == View.VISIBLE){
            double distance;
            Location locationA = new Location("");
            locationA.setLatitude(destlat);
            locationA.setLongitude(destlng);
            Location locationB = new Location("");
            locationB.setLatitude(updLat);
            locationB.setLongitude(updLng);
            distance = locationA.distanceTo(locationB);


            EditText current_distance = (EditText)findViewById(R.id.currentDistance);
            current_distance.setText("Current distance: " + Math.round(distance) + " m");
        }



    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
        if (alarm){
            startLocationUpdates();
            startTimer();
        }

        paused = true;
        pausedOnce = true;
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    //resuming
    @Override
    public void onResume() {
        super.onResume();
        paused = false;
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            //startLocationUpdates();
        }
    }





    public void setCircle(double radius, LatLng t) {
        CircleOptions cOptions = new CircleOptions()
                .center(t)
                .radius(radius)
                .strokeWidth(5)
                .strokeColor(0xFF3B5323)
                .fillColor(0x8078AB46);

        if (circle != null) {
            circle.remove();
            circle = null;
        }

        circle = map.addCircle(cOptions);
    }

    public void alertUser(){



        if (!r.isPlaying()){

            r.play();

            long[] pattern = {0, 600, 1000};
            vi.vibrate(pattern, 0);
            vibrating = true;

            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            AlertDialog builder = new AlertDialog.Builder(this).create();
            builder.setMessage("Hello Dear! You are within your range");


            /** Setting title for the alert dialog */
            builder.setTitle("Location alarm");
            builder.setIcon(R.drawable.dialogicon);
            builder.setButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    /** Exit application on click OK */

                    r.stop();
                    removeCircle();
                    vi.cancel();
                    alarmbutton.setText("SET ALARM");
                    marker.remove();
                    marker = null;
                    current_distance.setVisibility(View.GONE);

                }
            });
            builder.show();


            /** Creating the alert dialog window */

        }

        alarm = false;

    }

    public void removeCircle() {
        if (circle != null) {
            circle.remove();
            circle = null;
            alarm = false;
        }
    }

    public void onDestinationChanged(){
        removeCircle();
        if (r.isPlaying()){
            r.stop();
        }
        vi.cancel();
        alarmbutton.setText("SET ALARM");
        if (alarmbutton.getVisibility() == View.GONE){
            switchalarmbutton();
        }
        alarm = false;
        destinationCounter = 1;


    }




    public void checkFeatureAvailability(){
        LocationManager lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;


        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}





        if(!gps_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("Location Alarm needs location services to run for it to work. Do you want to switch on the Location services?");
            dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
        }
    }
    public void startTimer() {
        //set a new Timer
        timer = new Timer();
        //initialize the TimerTask's job
        initializeTimerTask();
        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 2000, 4000); //
    }
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        Runnable r = new Runnable() {
                            @Override
                            public void run() {
                                handler.post(new Runnable() {
                                    public void run() {
                                        if (alarm){
                                            double distance;
                                            Location locationA = new Location("");
                                            locationA.setLatitude(destlat);
                                            locationA.setLongitude(destlng);
                                            Location locationB = new Location("");
                                            locationB.setLatitude(updLat);
                                            locationB.setLongitude(updLng);
                                            distance = locationA.distanceTo(locationB);
                                            current_distance.setText("Current distance: " + Math.round(distance) + " m");
                                            if(distance<rad){
                                                alertUser();
                                                alarm = false;
                                            }
                                        }
                                    }
                                });
                            }
                        };
                        Thread bgAlarmThread = new Thread(r);
                        bgAlarmThread.start();
                    }
                });
            }
        };

    }
    public void addToFavorites(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add destination to Favorites");

        builder.setMessage("Enter Title for this Favorite Destination");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                store(m_Text);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void store(String s){
        locNames location = new locNames();
        location.set_locLat(destlat);
        location.set_locLng(destlng);
        location.set_locName(s.toString());
        //code to store locNames and other data in cache
        dbHandler.addLocation(location);
        Toast.makeText(fragment_console.this,"This location has been added to Favorites", Toast.LENGTH_LONG).show();
    }

    public void createDialogBoxOFlocNames(){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setIcon(R.drawable.dest_marker);
        builderSingle.setTitle("Select Your Destination:-");
        ArrayList<String> namesAL = dbHandler.getArrayListOFnames();
        int i;

        final ListAdapter m_Adapter = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1, namesAL);

        builderSingle.setAdapter(
                m_Adapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builderSingle.show();

    }


}




