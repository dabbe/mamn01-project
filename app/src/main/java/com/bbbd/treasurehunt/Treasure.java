package com.bbbd.treasurehunt;

import android.location.Location;

/**
 * Created by Daniel on 2015-04-25.
 */
public class Treasure {

    private float lon, lat;
    private String type;
    private String desc_text;
    private int points;

    public Location getLocation(){
        Location location = new Location("");
        location.setLatitude(lat);
        location.setLongitude(lon);
        return location;
    }
}
