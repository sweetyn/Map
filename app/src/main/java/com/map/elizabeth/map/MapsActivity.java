package com.map.elizabeth.map;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionApi;
import com.google.android.gms.location.DetectedActivity;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


import java.text.DecimalFormat;
import java.util.ArrayList;

//
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener {
//public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
//        ConnectionCallbacks,
//        OnConnectionFailedListener,
//        LocationListener,
//        ActionBar.TabListener{

    private GoogleMap mMap;

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
    protected TextView mDistanceText;

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
    private Button stopButton;
    private Button startButton;

    //Distance
    double totalDistance;
    float result[] = new float[]{0, 0, 0, 0, 0};

    double currentLat;
    double currentLng;
    double tempLat;
    double tempLng;

    int a;
    int c;

    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

//    private durationChronometer dChronometer;


//    durationChronometer dChronometer = new durationChronometer(Context);

//    durationChronometer dChron = new durationChronometer(this);

    android.app.ActionBar actionBar;

    protected static final String TAG="activity";

    protected ActivityDetectionBroadcastReceiver mBroadcastReceiver;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        /**
         * Display UI
         * color list : http://namaste-android.blogspot.com/2012/03/argb-color-codes.html
         * color list : http://angrytools.com/android/button/
         */

        //DistanceText
//        mDistanceText = (TextView) findViewById(R.id.distance_text);
//        mDistanceText.setText("--.--");

        //startButton
        startButton = (Button) findViewById(R.id.start_reset_button);
        startButton.setText("Start");
        startButton.getBackground().setColorFilter(0xFF008B8B, PorterDuff.Mode.MULTIPLY);
        startButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startReset(v);
            }
        });

        //stopButton
        stopButton = (Button) findViewById(R.id.stop_resume_button);
        stopButton.setText("Stop");
        stopButton.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
        stopButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                stopResume(v);
            }
        });

        //get location info
        mLatitudeLabel = "Latitude";
        mLongitudeLabel = "Longitude";
        mLatitudeText = (TextView) findViewById((R.id.latitude_text));
        mLongitudeText = (TextView) findViewById((R.id.longitude_text));


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mRequestingLocationUpdates = true;


        // Changing location UI
        textView = (TextView) findViewById(R.id.textView);


        // Try to connect GoogleAPI
        buildGoogleApiClient();


        //mGoogleApiClient.connect();


        //setup the accelerometer
       /* sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDafaultSensroe(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListenner(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
*/


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        //Show stopwatch
        mChronometer = (Chronometer) findViewById(R.id.chronometer);

//        //Action bar
//
//        ActionBar actionBar = getActionBar();
//        ActionBar.Tab tab1=actionBar.newTab();
//        tab1.setText("Tracking");
//        tab1.setTabListener(this);
//        actionBar.addTab(tab1);

//
//        ActionBar.NavigationMode = ActionBarNavigationMode.Tabs;
//        SetContentView(Resource.Layout.Main);
//
//        ActionBar.Tab tab = ActionBar.NewTab();
//        tab.SetText(Resources.GetString(Resource.String.tab1_text));
//        tab.SetIcon(Resource.Drawable.tab1_icon);
//        tab.TabSelected += (sender, args) => {
//            // Do something when tab is selected
//        }
//        ActionBar.AddTab(tab);
//
//        tab = ActionBar.NewTab();
//        tab.SetText(Resources.GetString(Resource.String.tab2_text));
//        tab.SetIcon(Resource.Drawable.tab2_icon);
//        tab.TabSelected += (sender, args) => {
//            // Do something when tab is selected
//        }
//        ActionBar.AddTab(tab);


//        final ActionBar actionBar = getActionBar();

        mBroadcastReceiver = new ActivityDetectionBroadcastReceiver();

    }


    protected void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        // Connect the Client
        client.connect();
        mGoogleApiClient.connect();



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

//        createLocationRequest();
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
                .addApi(ActivityRecognition.API)
                .build();
    }

    /**
     * 2016.6.17
     * Set Up a Location Request
     */
//    protected void createLocationRequest() {
//        // Create the LocationRequest object
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
//                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
//        // Sets the fastest rate for active location updates. This interval is exact, and your
//        // application will never receive updates faster than this value.
//        // Sets the desired interval for active location updates. This interval is
//        // inexact. You may not receive updates at all if no location sources are available, or
//        // you may receive them slower than requested. You may also receive updates faster than
//        // requested if other applications are requesting location at a faster interval.
//    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
//        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//        if (mLastLocation != null) {
//            mLatitudeText.setText(String.format("%s: %f", mLatitudeLabel,
//                    mLastLocation.getLatitude()));
//            mLongitudeText.setText(String.format("%s: %f", mLongitudeLabel,
//                    mLastLocation.getLongitude()));
////            textView.append("\n "+mLastLocation.getLongitude() +" " +location.getLatitude());
//        } else {
//            Toast.makeText(this, R.string.no_location_detected, Toast.LENGTH_LONG).show();
//        }

        // Create the LocationRequest object
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);










//        mListener = new LocationListener() {
//




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

    public void updateButton(View view) {

//        if (stopButton.getText().equals("Start")){
//            mChronometer.start();
//            stopButton.setText("Stop");
//            stopButton.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
//        }
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

    public void startReset(View view) {

        startLocationUpdates();

        if (startButton.getText().equals("Start")) {
            mChronometer.start();
            startButton.setText("Reset");
            startButton.getBackground().setColorFilter(0xFF808080, PorterDuff.Mode.MULTIPLY);
        } else if (startButton.getText().equals("Reset")) {
            mChronometer.stop();
            mChronometer.setBase(SystemClock.elapsedRealtime());
            startButton.setText("Start");
            startButton.getBackground().setColorFilter(0xFF008B8B, PorterDuff.Mode.MULTIPLY);
            stopButton.setText("Stop");
            stopButton.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
        } else {

        }

    }


    public void stopResume(View view) {


        if (startButton.getText().equals("Start")) {

        } else {
            if (stopButton.getText().equals("Stop")) {
//                dChron.stop();
//                mChronometer.stop();
                stopButton.setText("Resume");
                stopButton.getBackground().setColorFilter(0xFF008B8B, PorterDuff.Mode.MULTIPLY);
            } else if (stopButton.getText().equals("Resume")) {
                mChronometer.start();
//                mChronometer.setText((long) (SystemClock.elapsedRealtime() - mChronometer.getBase()));
                stopButton.setText("Stop");
                stopButton.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
            } else {

            }
        }
    }



    @Override
    public void onLocationChanged(Location location) {

        if (location != null) {
            textView.append("\n" + "Lat, Lng : " + location.getLatitude() + ", " + location.getLongitude());
            LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(current).visible(false).draggable(true));

            //if there is on one LatLng, then set same value.
            if (polynum == 0) {
                temp = current;
                polynum = 1;
            }

            // Instantiates a new Polyline object and adds points to define a rectangle
            Polyline polyline = mMap.addPolyline(new PolylineOptions()
                    .add(current, temp)
                    .width(5)
                    .color(Color.BLUE));


            Location locationA = new Location("");
            locationA.setLatitude(current.latitude);
            locationA.setLongitude(current.longitude);

            Location locationB = new Location("");
            locationB.setLatitude(temp.latitude);
            locationB.setLongitude(temp.longitude);


//            double currentLat = current.latitude;
//            double currentLng = current.longitude;
//            double tempLat = temp.latitude;
//            double tempLng = temp.longitude;

//            DetectedActivity detectedActivity = new DetectedActivity();

            //if devices is stop(3), not calculate distance
//            if(detectedActivity.getType() != 3){
            totalDistance += locationA.distanceTo(locationB);
//                totalDistance += distance(currentLat, currentLng, tempLat, tempLng);
//            }


//            Location.distanceBetween(location.getLatitude(), current.latitude, location.getLongitude(), current.longitude, result);
//            totalDistance += result[0];
            mDistanceText.setText(String.format("%.2f", totalDistance));

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
        } else if (mMap.getMapType() == GoogleMap.MAP_TYPE_SATELLITE) {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        } else {
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

        // Add a buttoooon for current my location
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
        // Disconnect the client
        client.disconnect();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.on
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.map.elizabeth.map/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);




    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    /**
    * Called by Google Play services if the connection to GoogleApiClient drops because of an error.
    */
    public void onDisconnected() {
        Log.i(TAG, "Disconnected");
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

//    @Override
//    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
//        //Called when a tab is selected
//        int nTabSelected = tab.getPosition();
//        switch (nTabSelected) {
//            case 0:
//                setContentView(R.layout.actionbar_tab_1);
//                break;
//            case 1:
//                setContentView(R.layout.actionbar_tab_2);
//                break;
//            case 2:
//                setContentView(R.layout.actionbar_tab_3);
//                break;
//        }
//    }

//    @Override
//    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
//
//    }
//
//    @Override
//    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
//
//    }


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


//    private double distance(double lat1, double lon1, double lat2, double lon2) {
//        double theta = lon1 - lon2;
//        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
//        dist = Math.acos(dist);
//        dist = rad2deg(dist);
//        dist = dist * 60 * 1.1515;
//        return (dist);
//    }
//
//    private double deg2rad(double deg) {
//        return (deg * Math.PI / 180.0);
//    }
//    private double rad2deg(double rad) {
//        return (rad * 180.0 / Math.PI);
//    }
    /**
     * Returns a human readable String corresponding to a detected activity type.
     */
    public String getActivityString(int detectedActivityType) {
        Resources resources = this.getResources();
        switch(detectedActivityType) {
            case DetectedActivity.IN_VEHICLE:
                return resources.getString(R.string.in_vehicle);
            case DetectedActivity.ON_BICYCLE:
                return resources.getString(R.string.on_bicycle);
            case DetectedActivity.ON_FOOT:
                return resources.getString(R.string.on_foot);
            case DetectedActivity.RUNNING:
                return resources.getString(R.string.running);
            case DetectedActivity.STILL:
                return resources.getString(R.string.still);
            case DetectedActivity.TILTING:
                return resources.getString(R.string.tilting);
            case DetectedActivity.UNKNOWN:
                return resources.getString(R.string.unknown);
            case DetectedActivity.WALKING:
                return resources.getString(R.string.walking);
            default:
                return resources.getString(R.string.unidentifiable_activity, detectedActivityType);
        }
    }

    /**
     * Receiver for intents sent by DetectedActivitiesIntentService via a sendBroadcast().
     * Receives a list of one or more DetectedActivity objects associated with the current state of
     * the device.
     */
    public class ActivityDetectionBroadcastReceiver extends BroadcastReceiver {
        protected static final String TAG = "receiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<DetectedActivity> updatedActivities =
                    intent.getParcelableArrayListExtra(Constants.ACTIVITY_EXTRA);

            String strStatus = "";
            for (DetectedActivity thisActivity : updatedActivities) {
                strStatus += getActivityString(thisActivity.getType()) + thisActivity.getConfidence() + "%\n";
            }
//            mStatusText.setText(strStatus);
        }
    }
}
