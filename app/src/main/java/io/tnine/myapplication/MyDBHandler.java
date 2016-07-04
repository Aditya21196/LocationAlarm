package io.tnine.myapplication;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;


public class MyDBHandler extends SQLiteOpenHelper{


    private static final int DATABSE_VERSION = 1;
    private static final String DATABASE_NAME = "locNames.DB";
    private static final String TABLE_LOCATIONS = "Locations";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "locationname";
    private static final String COLUMN_LATTITUDE = "Lattitude";
    private static final String COLUMN_LONGITUDE = "Longitude";

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABSE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_LOCATIONS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT " +
                COLUMN_NAME + " TEXT " +
                COLUMN_LATTITUDE + " FLOAT " +
                COLUMN_LONGITUDE + " FLOAT " +
                ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
        onCreate(db);
    }

    //Add a new row to db
    public void addLocation(locNames location){
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, location.get_locName());
        values.put(COLUMN_LATTITUDE, location.get_locLat());
        values.put(COLUMN_LONGITUDE, location.get_id());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_LOCATIONS,null,values);
        db.close();
    }

    //Delete a product from database
    public void deleteLocation(int id){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_LOCATIONS + "WHERE " + COLUMN_ID + "=\"" + id + "\";" );
    }

    public LatLng getLocationLatLng() {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_LOCATIONS + " WHERE 1";

        //Cursor point to a place in results
        Cursor c = db.rawQuery(query,null);
        //Move to first row in results
        c.moveToFirst();

        double dlatOFdb = c.getColumnIndex(COLUMN_LATTITUDE);
        double dLngOFdb = c.getColumnIndex(COLUMN_LONGITUDE);
        LatLng dbLatLng = new LatLng(dlatOFdb,dLngOFdb);


        db.close();
        return dbLatLng;
    }


}
