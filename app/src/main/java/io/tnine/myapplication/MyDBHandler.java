package io.tnine.myapplication;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


public class MyDBHandler extends SQLiteOpenHelper{


    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "locNames.DB";
    private static final String TABLE_LOCATIONS = "Locations";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "locationname";
    private static final String COLUMN_LATTITUDE = "Lattitude";
    private static final String COLUMN_LONGITUDE = "Longitude";


    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "CREATE TABLE Locations(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "locationname text," +
                "Lattitude float," +
                "Longitude float" +
                ") ";
        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS );
        onCreate(db);
    }

    //Add a new row to db
    public void addLocation(locNames location){
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, location.get_locName());
        values.put(COLUMN_LATTITUDE, location.get_locLat());
        values.put(COLUMN_LONGITUDE, location.get_locLng());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_LOCATIONS,null,values);
        db.close();
    }

    //Delete a product from database
    public void deleteLocation(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_LOCATIONS + "WHERE " + COLUMN_ID + "=\"" + id + "\";" );
    }

    public LatLng getLocationLatLng(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_LOCATIONS + " WHERE " + COLUMN_ID + " = "+ id ;

        //Cursor point to a place in results
        Cursor c;
        c = db.rawQuery(query,null);
        //Move to first row in results
        c.moveToFirst();

        double dlatOFdb = c.getColumnIndex(COLUMN_LATTITUDE);
        double dLngOFdb = c.getColumnIndex(COLUMN_LONGITUDE);
        LatLng dbLatLng = new LatLng(dlatOFdb,dLngOFdb);


        db.close();
        return dbLatLng;
    }

    public ArrayList<String> getArrayListOFnames (){
        ArrayList<String> outputAL = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_LOCATIONS + " WHERE 1";
        //create a cursor
        Cursor cursor;
        cursor =  db.rawQuery(query,null);
        //To move cursor to first result
        cursor.moveToFirst();

        while (!cursor.isAfterLast()){
            if(cursor.getString(cursor.getColumnIndex("locationname"))!=null){
                outputAL.add(cursor.getString(cursor.getColumnIndex("locationname")));
                cursor.moveToNext();
            }
        }
        db.close();
        cursor.close();
        return outputAL;
    };

    public ArrayList<Integer> getArrayListOFids (){
        ArrayList<Integer> outputAL = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT " + COLUMN_ID + " FROM " + TABLE_LOCATIONS + " WHERE 1";
        //creat a cursor
        Cursor c = db.rawQuery(query,null);
        //To move cursor to first result
        c.moveToFirst();

        while (!c.isAfterLast()){
            if(c.getString(c.getColumnIndex("locationname"))!=null){
                outputAL.add(c.getColumnIndex("locationname"));
            }
        }
        db.close();
        return outputAL;
    };


}
