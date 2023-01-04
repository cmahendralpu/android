package com.example.practicalassessmentmap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;
// broadcast receiver for receive geofence enter or exit
public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GeofenceBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        NotificationHelper notificationHelper =new NotificationHelper(context);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes
                    .getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {


            int transitionType = geofencingEvent.getGeofenceTransition();

            switch (transitionType) {
                case Geofence.GEOFENCE_TRANSITION_ENTER:
                    Toast.makeText(context, R.string.geofence_entry_text, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, context.getString(R.string.geofence_entry_text)+" "+geofencingEvent.getTriggeringLocation().getLatitude()+" "+geofencingEvent.getTriggeringLocation().getLongitude());
                    notificationHelper.showNotification(context.getString(R.string.geofence_entry_text)+" "+geofencingEvent.getTriggeringLocation().getLatitude()+" "+geofencingEvent.getTriggeringLocation().getLongitude(), context);
                    break;
                case Geofence.GEOFENCE_TRANSITION_EXIT:
                    Log.d(TAG, context.getString(R.string.geofence_exit_text)+" "+geofencingEvent.getTriggeringLocation().getLatitude()+" "+geofencingEvent.getTriggeringLocation().getLongitude());
                    Toast.makeText(context, R.string.geofence_exit_text, Toast.LENGTH_SHORT).show();
                    notificationHelper.showNotification(context.getString(R.string.geofence_exit_text)+" "+geofencingEvent.getTriggeringLocation().getLatitude()+" "+geofencingEvent.getTriggeringLocation().getLongitude(), context);
                    break;
            }


        } else {
            // Log the error.
            Log.e(TAG, notificationHelper.getString(R.string.invalid_type_error)+
                    geofenceTransition);
        }
    }

}