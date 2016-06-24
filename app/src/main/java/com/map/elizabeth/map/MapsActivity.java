package com.map.elizabeth.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;

    protected static final String TAG = "MainActivity";
    private SensorManager sensorManager;
    private Sensor accelerometer;

    private LocationListener mListener;
    //    private GoogleApiClient mLastLocation;
    private LocationRequest mLocationRequest;

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;

    protected String mLatitudeLabel;
    protected String mLongitudeLabel;
    protected TextView mLatitudeText;
    protected TextView mLongitudeText;

    //Changing location UI
    private TextView textView;

    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    protected Boolean mRequestingLocationUpdates;

    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
//    protected LocationSettingsRequest mLocationSettingsRequest;


    private LocationManager locationManager;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    private PolylineOptions rectOptions;
    private Polyline polyline;

    private int polynum = 0;
    private LatLng temp;

    //Stopwatch
    private Chronometer mChronometer;
    private Button button;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Try to connect GoogleAPI
        buildGoogleApiClient();

        //mGoogleApiClient.connect();

        //get location info
        mLatitudeLabel = "Latitude";
        mLongitudeLabel = "Longitude";
//        mLongitudeLabel = getResources().getString(R.string.longitude_label);
        mLatitudeText = (TextView) findViewById((R.id.latitude_text));
        mLongitudeText = (TextView) findViewById((R.id.longitude_text));

        mRequestingLocationUpdates = true;


        //Changing location UI
        textView = (TextView) findViewById(R.id.textView);


        button = (Button) findViewById(R.id.stop_reset_button);
        button.setText("Stop");
        button.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                stopReset(v);
            }
        });

//        // Create the LocationRequest object
//        mLocationRequest = LocationRequest.create()
//                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
//                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        //setup the accelerometer
       /* sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDafaultSensroe(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListenner(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
*/
        //Accessing Google API
//        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this /* FragmentActivity */,
//                        this /* OnConnectionFailedListener */)
//                .addApi(Drive.API)
//                .addScope(Drive.SCOPE_FILE)
//                .build();
//

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        //Show stopwatch
        mChronometer = (Chronometer) findViewById(R.id.chronometer);


    }


    protected void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        mGoogleApiClient.connect();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.map.elizabeth.map/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }


    @Override
    public void onResume() {
        super.onResume();
        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // location updates if the user has requested them.

        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }

    }


    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        createLocationRequest();
    }

    /**
     * 2016.6.17
     * Set Up a Location Request
     */
    protected void createLocationRequest() {

        // Create the LocationRequest object
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

//        mListener = new LocationListener() {
//
//            @Override
//            public void onLocationChanged(Location location) {
////                Toast.makeText(MapsActivity.this,
////                        "Location changed:" + location.getLatitude() + "," +
////                                location.getLatitude(), Toast.LENGTH_SHORT).show();
////                gotoLocation(location.getLatitude(), location.getLongitude(), 15);
//                textView.append("\n "+location.getLongitude() +" " +location.getLatitude());
//            }
//        };

        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitudeText.setText(String.format("%s: %f", mLatitudeLabel,
                    mLastLocation.getLatitude()));
            mLongitudeText.setText(String.format("%s: %f", mLongitudeLabel,
                    mLastLocation.getLongitude()));
//            textView.append("\n "+mLastLocation.getLongitude() +" " +location.getLatitude());
        } else {
            Toast.makeText(this, R.string.no_location_detected, Toast.LENGTH_LONG).show();
        }

        //startLocationUpdates
//        if (mRequestingLocationUpdates) {
//            startLocationUpdates();
//        }


//        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
//                mGoogleApiClient);
//        if (mLastLocation != null) {
//            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
//            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
//        }


    }


    /**
     * Uses a {@link LocationSettingsRequest.Builder} to build
     * a {@link LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
//        mLocationSettingsRequest = builder.build();
    }


    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }


  /*public void onSensorChanged(SensorEvent event){
        Sensor mySensor = event.sensor;

        if(mySensor.getType()== Sensor.TYPE_ACCELEROMETER){
            float x = event.valures[0];
            float y = event.valures[1];
            float z = event.valures[2];


        }
    }

    public void onAccracyChanged(Sensor sensor, int accuracy){

    }
*/

    public void startRecord(View view) {

//        createLocationRequest();
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitudeText.setText(String.format("%s: %f", mLatitudeLabel,
                    mLastLocation.getLatitude()));
            mLongitudeText.setText(String.format("%s: %f", mLongitudeLabel,
                    mLastLocation.getLongitude()));

        } else {
            Toast.makeText(this, R.string.no_location_detected, Toast.LENGTH_LONG).show();
        }

    }

    public void startResume(View view) {
        startLocationUpdates();
        mChronometer.start();
    }


    public void stopReset(View view) {
        if( button.getText().equals("Stop")) {
            mChronometer.stop();
            button.setText("Reset");
            button.getBackground().setColorFilter(0xFFD3D3D3, PorterDuff.Mode.MULTIPLY);
        } else if(button.getText().equals("Reset")) {
            mChronometer.setBase(SystemClock.elapsedRealtime());
            button.setText("Stop");
            button.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
        } else {

        }
    }

    @Override
    public void onLocationChanged(Location location) {

        if (location != null) {
            textView.append("\n"+"Lat, Lng : " + location.getLatitude() +", " + location.getLongitude());
            LatLng current = new LatLng(location.getLatitude(),location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(current).visible(true).draggable(true));

            //if there is on one LatLng, then set same value.
            if(polynum == 0) {
                temp = current;
                polynum = 1;
            }

            // Instantiates a new Polyline object and adds points to define a rectangle
            Polyline polyline = mMap.addPolyline(new PolylineOptions()
                    .add(current, temp)
                    .width(5)
                    .color(Color.BLUE));
            temp = current;

        } else {
            Toast.makeText(this, R.string.no_location_detected, Toast.LENGTH_LONG).show();
        }


//        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
//        updateUI();
//        Toast.makeText(this, getResources().getString(R.string.location_updated_message),
//                Toast.LENGTH_SHORT).show();
    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//        handleNewLocation(location);
//
//    }

    public void changeType(View view) {
        if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else if(mMap.getMapType() == GoogleMap.MAP_TYPE_SATELLITE) {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        } else{
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }


//public void showCurrent

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        // Add a marker in San Diego and move the camera
        LatLng sandiego = new LatLng(32.715738, -117.1610838);
        mMap.addMarker(new MarkerOptions().position(sandiego).title("Marker in San Diego"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sandiego));

        // Add a button for current my location
        mMap.setMyLocationEnabled(true);

    }


    public void getShowCurrentLocation(MenuItem item) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Location currentLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);
        if (currentLocation == null) {
            Toast.makeText(this, "Couldn't connet", Toast.LENGTH_SHORT).show();
        } else {
            LatLng latLng = new LatLng(
                    currentLocation.getLatitude(),
                    currentLocation.getLongitude()
            );
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 15);
            mMap.animateCamera(update);

        }
    }


    public void showCurrentLocation(MenuItem item) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location currentLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);
        if (currentLocation == null) {
            Toast.makeText(this, "Couldn't connet", Toast.LENGTH_SHORT).show();
        } else {
            LatLng latLng = new LatLng(
                    currentLocation.getLatitude(),
                    currentLocation.getLongitude()
            );
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 15);
            mMap.animateCamera(update);

        }
    }

    private void gotoLocation(double lat, double lng, float zoom) {
        LatLng latLng = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mMap.moveCamera(update);
    }


    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }
//        LocationRequest request = LocationRequest.create();
//        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        request.setInterval(5000);
//        request.setFasttestInterval(1000);
//        LocationServices.FusedLocationApi.requestLocationUpdates(
//            mGoogleApiClient, request, mListener
//        );


    protected void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.map.elizabeth.map/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }


    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
    }


//    public void getAndDrawSpeedLimits(){
//        SnapnedPoly
//        var snappedPolyline = new google.maps.Polyline({
//                path: snappedCoordinates,
//                strokeColor: 'black',
//                strokeWeight: 3
//        });
//
//        snappedPolyline.setMap(map);
//        polylines.push(snappedPolyline);
//
//    }

}
