package io.tnine.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

class CustomAdapter extends ArrayAdapter<String> {


    public CustomAdapter(Context context, ArrayList<String> foods) {
        super(context, R.layout.cutomrow, foods );
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater m_inflater = LayoutInflater.from(getContext());
        View customView = m_inflater.inflate(R.layout.cutomrow,parent, false);

        String singleFoodItem = getItem(position);
        TextView m_TextView = (TextView)customView.findViewById(R.id.customRowText);
        m_TextView.setText(singleFoodItem);

        Button m_Button = (Button)customView.findViewById(R.id.cross_button);


        m_Button.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //code to remove this element from database - to do after learning how to locate the chosen destination
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });
        return customView;
    }
}
