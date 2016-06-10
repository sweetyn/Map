package com.map.elizabeth.map;

import android.Manifest;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private GoogleApiClient mLocationClient;
    private LocationListener mListener;
    private GoogleApiClient mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mLocationClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mLocationClient.connect();
        //setup the accelerometer
       /* sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDafaultSensroe(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListenner(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
*/
        //Accessing Google API
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
//                .addApi(Drive.API)
//                .addScope(Drive.SCOPE_FILE)
                .build();

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
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
//        if (getCurrentFocus() == mMap.getMyLocation())
    }

    public void stopRecord(View view) {

    }


    public void changeType (View view){
        if(mMap.getMapType()== GoogleMap.MAP_TYPE_NORMAL) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }
        else
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

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
        mMap.setMyLocationEnabled(true);
        //KmlLayer layer = new KmlLayer(getMap(), R.raw.kmlFile, getApplicationContext());
        //mMap.addMarker()
        //mMap.
    }


    public void getShowCurrentLocation(MenuItem item) {
        Location currentLocation = LocationServices.FusedLocationApi
                .getLastLocation(mLocationClient);
        if(currentLocation==null){
            Toast.makeText(this,"Couldn't connet", Toast.LENGTH_SHORT).show();
        } else{
            LatLng latLng= new LatLng(
                    currentLocation.getLatitude(),
                    currentLocation.getLongitude()
            );
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 15);
            mMap.animateCamera(update);

        }
    }


    public void showCurrentLocation(MenuItem item) {
        Location currentLocation = LocationServices.FusedLocationApi
                .getLastLocation(mLocationClient);
        if(currentLocation==null){
            Toast.makeText(this,"Couldn't connet", Toast.LENGTH_SHORT).show();
        } else{
            LatLng latLng= new LatLng(
                    currentLocation.getLatitude(),
                    currentLocation.getLongitude()
            );
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 15);
            mMap.animateCamera(update);

        }
    }

    private void gotoLocation(double lat, double lng, float zoom){
        LatLng latLng = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mMap.moveCamera(update);
    }



    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Toast.makeText(MapsActivity.this,
                    "Location changed:" + location.getLatitude()+","+
                            location.getLatitude(), Toast.LENGTH_SHORT).show();
                gotoLocation(location.getLatitude(), location.getLongitude(), 15);
            }
        };

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }


//        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
//                mGoogleApiClient);
//        if (mLastLocation != null) {
//            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
//            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
//        }


    }
//        LocationRequest request = LocationRequest.create();
//        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        request.setInterval(5000);
//        request.setFasttestInterval(1000);
//        LocationServices.FusedLocationApi.requestLocationUpdates(
//            mLocationClient, request, mListener
//        );


    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



}
