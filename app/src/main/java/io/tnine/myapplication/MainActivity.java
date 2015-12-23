package io.tnine.myapplication;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class MainActivity extends Activity {
    private static final int GPS_ERRORDIALOG_REQUEST = 9001 ;
    private static int TIME_OUT = 5000; //Time to launch the another activity






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(MainActivity.this, fragment_console.class);
                startActivity(i);
                finish();
            }


        }, TIME_OUT);


    }
}









