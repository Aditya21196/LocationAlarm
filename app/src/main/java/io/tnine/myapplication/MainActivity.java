package io.tnine.myapplication;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;

public class MainActivity extends Activity {
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









