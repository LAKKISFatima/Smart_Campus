package com.example.smartcampus;

import android.location.Location;


public class MyLocation  {

    static boolean isInArea(Location l, double xMin, double xMax, double yMin, double yMax ){
        if ( l.getLongitude() <= xMax && l.getLongitude() >= xMin
                && l.getLatitude() <= yMax && l.getLatitude() >= yMin)
            return true;

        return false;
    }



}
