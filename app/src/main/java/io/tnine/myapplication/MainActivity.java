package io.tnine.myapplication;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {


    //declaration of app constituents
    private Button alarmbutton;
    private boolean result = true;
    //if result is true, destination fragment is seen
    //if result is false, alarm settings fragment is seen

    public boolean dstatus = false;

    View frag1 = findViewById(R.id.destination_fragment);
    View frag2 = findViewById(R.id.alarmsetfragment);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //code to make splash image fullscreen
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        setContentView(R.layout.activity_main);

        //code to delay for 5 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.activity_map1);
            }
        }, 5000);

        alarmbutton = (Button) findViewById(R.id.setalarm_button);

        //code to turn some instance variables invisible when program starts
        alarmbutton.setVisibility(View.GONE);
        frag2.setVisibility(View.GONE);

        //Initial On click listeners
        alarmbutton.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        switchfrags();
                        switchalarmbutton();
                    }
                }
        );

    }

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

}






