package io.tnine.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class selectionMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection_menu);
        TextView tv1 = (TextView)findViewById(R.id.settingsText1);
        TextView tv2 = (TextView)findViewById(R.id.settingsText2);
        TextView tv3 = (TextView)findViewById(R.id.settingsText3);
        tv1.setOnClickListener(new TextView.OnClickListener(){
            public void onClick (View v){
                Intent i = new Intent(selectionMenu.this, fragment_console.class);
                startActivity(i);
            }
        }
        );
        tv2.setOnClickListener(new TextView.OnClickListener(){
                                   public void onClick (View v){
                                       Intent i = new Intent(selectionMenu.this, fragment_console.class);
                                       startActivity(i);
                                   }
                               }
        );
        tv3.setOnClickListener(new TextView.OnClickListener(){
                                   public void onClick (View v){
                                       Intent i = new Intent(selectionMenu.this, fragment_console.class);
                                       startActivity(i);
                                   }
                               }
        );
    }
}
