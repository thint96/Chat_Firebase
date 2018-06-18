package fsi.studymyselft.nguyenthanhthi.chatapp.other;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by thanhthi on 15/06/2018.
 */

public class LocationUpdating {

    private static final String TAG = "Location Updating class";

    private Context context;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location location;
    private double latitude, longitude;
    private Geocoder geocoder;
    private List<Address> addresses;

    public LocationUpdating(Context context) {
        this.context = context;
        addresses = new ArrayList<>();
        updateGpsLocation();
    }

    private void updateGpsLocation() {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        checkPermission();

        String locationProvider = LocationManager.NETWORK_PROVIDER;

        location = locationManager.getLastKnownLocation(locationProvider);
        if (location != null) {
            updateToAddress(location);
        }

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateToAddress(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
    }

    private void updateToAddress(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        if (0 == latitude && 0 == longitude) {
            Log.e(TAG, "updateToAddress(): can not get latitude and longitude");
        }

        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        }
        catch (IOException e) {
            Log.e(TAG, "updateToAddress(): Can not get addresses from Geocoder");
            e.printStackTrace();
        }
    }

    public void checkPermission() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.INTERNET)
                    != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.INTERNET},
                    500);
        }
    }

    public List<Address> getAddresses() {
        if (addresses == null || addresses.isEmpty()) {
            Log.e(TAG, "getAddresses(): addresses is null");
            return null;
        }
        return addresses;
    }

    public String getPositionInOneLine() {
        return getAddresses() == null ? "Position" : addresses.get(0).getAddressLine(0);
    }

}
