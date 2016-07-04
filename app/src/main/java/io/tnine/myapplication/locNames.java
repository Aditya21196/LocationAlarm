package io.tnine.myapplication;

/**
 * Created by Acer on 7/4/2016.
 */
public class locNames {

    private String _locName;
    private int _id;
    private double _locLat;
    private double _locLng;

    public locNames(){}


    public void set_id(int _id) {
        this._id = _id;
    }

    public void set_locName(String _locName) {
        this._locName = _locName;
    }

    public void set_locLng(double _locLng) {
        this._locLng = _locLng;
    }

    public void set_locLat(double _locLat) {
        this._locLat = _locLat;
    }


    public int get_id() {
        return _id;
    }

    public double get_locLat() {
        return _locLat;
    }

    public double get_locLng() {
        return _locLng;
    }

    public String get_locName() {
        return _locName;
    }
}
