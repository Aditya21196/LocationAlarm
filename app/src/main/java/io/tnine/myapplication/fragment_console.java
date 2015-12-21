package io.tnine.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class fragment_console extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    //declaration of app constituents
    private Button alarmbutton;
    private boolean result = true;

    //if result is true, destination fragment is seen
    //if result is false, alarm settings fragment is seen

    private GoogleApiClient mClient;

    public boolean dstatus = false;

    View frag1;
    View frag2;
    GoogleMap googleMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_console);

        mClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        //declarations
        alarmbutton = (Button) findViewById(R.id.setalarm_button);
        frag1 = findViewById(R.id.destination_fragment);
        frag2 = findViewById(R.id.alarmsetfragment);
//code to obtain google map named googleMap
        try{
if(googleMap==null) {
    googleMap = ((MapFragment) getFragmentManager()
            .findFragmentById(R.id.map)).getMap();
             }
        }
            catch(Exception e)
            {
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
    public void switchfrags(){

        if (result==true){
            frag1.setVisibility(View.GONE);
            frag2.setVisibility(View.VISIBLE);
        }else{
            frag1.setVisibility(View.VISIBLE);
            frag2.setVisibility(View.GONE);
        }
        result = !result;
    }
    public void switchalarmbutton(){
        if(alarmbutton.getVisibility()==View.GONE){
            alarmbutton.setVisibility(View.VISIBLE);
        }else{
            alarmbutton.setVisibility(View.GONE);
        }
    }
    public void dstatusSwitch(){
        dstatus=!dstatus;
        switchalarmbutton();
    }
    //places

    @Override
    public void onConnectionSuspended(int i) {

    }
    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void gotoLocation(double lat, double lng, boolean mark, String title){
        LatLng ll = new LatLng(lat,lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 15);
        googleMap.moveCamera(update);
        if (mark){
            MarkerOptions marker=(new MarkerOptions().position(ll).title(title));
            googleMap.addMarker(marker);
        }
    }


}
