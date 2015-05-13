package arizona.edu.foodflipper;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Josh on 4/9/15.
 */
public class Score {

    private User user;
    private int score = -1;
    private String timestamp = "";
    private double latitude = 33.24;
    private double longitude = -110.9;

    // Used to insert new scores
    public Score(User user, int score) {

        this.score = score;
        this.user = user;
        setLocation();
    }

    //used to display scores
    public Score(User user, int score, String timestamp) {

        this.user = user;
        this.score = score;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // used to display scores
    public Score(User user, int score, String timestamp, double latitude, double longitude) {

        this.user = user;
        this.score = score;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String toString() {
        return user + " : " + score;
    }

    public int getUID() {
        return user.getUID();
    }

    public int getScore() {
        return score;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public double getLatitude(){ return latitude; };

    public double getLongitude(){ return longitude; };

    // Set latitude and longitude to the best value we can at the moment
    private void setLocation() {

        //Try to get most recent location
        Context context = null;
        context = context.getApplicationContext();
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Geocoder coder = new Geocoder(context);
        Location location;

        //Use best option
        if(gpsEnabled) {
            location = new Location(LocationManager.GPS_PROVIDER);
        }else if(networkEnabled){
            location = new Location(LocationManager.GPS_PROVIDER);
        }else{

            // No options available, user User's default location (address)
            try {
                List<Address> geocodeResults = coder.getFromLocationName(user.getAddress(), 1);
                Iterator<Address> locations = geocodeResults.iterator();
                while (locations.hasNext()) {
                    Address addr = locations.next();
                    this.latitude = addr.getLatitude();
                    this.longitude = addr.getLongitude();
                }
                return;
            } catch (IOException e) {
                return;
            }
        }

        //If you've made it this far we can use locations's lat and lng
        this.latitude = location.getLatitude();
        this.latitude = location.getLatitude();
        return;
    }//end setLocation

}
