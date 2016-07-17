package com.map.elizabeth.map;

import android.app.IntentService;
import android.content.Intent;
import android.provider.SyncStateContract;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

/**
 * Created by hyangmi.jo on 7/12/2016.
 */
public class DetectedActivitiesIntentService extends IntentService{
    protected static final String TAG = "detection_is";
    public DetectedActivitiesIntentService() {
        super(TAG);
    }

    protected void onHandleIntent(Intent intent){
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
        Intent localIntent = new Intent(Constants.BROADCAST_ACTION);

        // Get the list of the probable acitivites associated with the current state of the
        // devices. Each acivity is associated with a confiednce level, which is an  intent between
        // 0 and 100.
        ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();

        // Log each activity.
        Log.i(TAG, "activities detected");

        // Broadcast the list of detected activities.
        localIntent.putExtra(Constants.ACTIVITY_EXTRA, detectedActivities);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);



    }
}
