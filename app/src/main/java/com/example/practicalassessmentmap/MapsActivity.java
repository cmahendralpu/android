package com.example.practicalassessmentmap;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.practicalassessmentmap.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 101;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 102;

    private GeofencingClient geofencingClient;

    private GeofenceMonitorHelper geofenceMonitorHelper;

    private float GEOFENCE_RADIUS = 100;

    private List<LatLng> locationArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        locationArrayList.add(new LatLng(12.953013054035946, 77.5417514266668));
        locationArrayList.add(new LatLng(12.95428866232216, 77.5438757362066));
        locationArrayList.add(new LatLng(12.95558517552543, 77.54565672299249));
        locationArrayList.add(new LatLng(12.956442543452548, 77.54752354046686));
        locationArrayList.add(new LatLng(12.95675621390793, 77.54919723889215));
        locationArrayList.add(new LatLng(12.957069883968225, 77.55112842938287));
        locationArrayList.add(new LatLng(12.957711349517467, 77.55308710458465));
        locationArrayList.add(new LatLng(12.958464154110917, 77.55514704110809));
        locationArrayList.add(new LatLng(12.959656090062529, 77.5559409749765));
        locationArrayList.add(new LatLng(12.960814324441305, 77.5574167738546));
        locationArrayList.add(new LatLng(12.961253455257907, 77.5592192183126));
        locationArrayList.add(new LatLng(12.96156308861349, 77.56126500049922));
        locationArrayList.add(new LatLng(12.961814019848779, 77.56308890262935));
        locationArrayList.add(new LatLng(12.962775920574138, 77.56592131534907));
        locationArrayList.add(new LatLng(12.963340512746937, 77.5676379291186));
        locationArrayList.add(new LatLng(12.96411114676894, 77.56951526792183));
        locationArrayList.add(new LatLng(12.964382986146292, 77.5717254081501));
        locationArrayList.add(new LatLng(12.964584624077109, 77.57370388966811));

        locationArrayList.add(new LatLng(12.964542802687657, 77.57591402989638));
        locationArrayList.add(new LatLng(12.963795326167373, 77.57798997552734));
        locationArrayList.add(new LatLng(12.96358621848664, 77.58015720041138));
        locationArrayList.add(new LatLng(12.963481664580394, 77.58189527185303));


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        geofencingClient = LocationServices.getGeofencingClient(this);

        geofenceMonitorHelper = new GeofenceMonitorHelper(this);
    }

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

        // Add a marker in Sydney and move the camera
        addMarkerAndGeo();

        float zoomLevel = 15.0f;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationArrayList.get(0), zoomLevel
        ));

        enableUserLocation();

    }

    public void addMarkerAndGeo() {
        for (int i = 0; i < locationArrayList.size(); i++) {
            // below line is use to add marker to each location of our array list.
            mMap.addMarker(new MarkerOptions().position(locationArrayList.get(i)).title(getString(R.string.location) +(i + 1)));
            addMarkerCircle(locationArrayList.get(i), GEOFENCE_RADIUS);
            addGeofence(locationArrayList.get(i), (i + 1), GEOFENCE_RADIUS);
            // below line is use to zoom our camera on map.
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkLocationPermission();

    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
            )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this)
                        .setTitle(R.string.alert_title)
                        .setMessage(R.string.alert_msg)
                        .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                            requestLocationPermission();
                        }).create();
                //Prompt the user once explanation has been shown


                alertDialog.show();
            } else {
                // No explanation needed, we can request the permission.
                requestLocationPermission();
            }
        } else {
            checkBackgroundLocation();
        }
    }

    private void checkBackgroundLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestBackgroundLocationPermission();
        }
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                },
                FINE_LOCATION_ACCESS_REQUEST_CODE
        );
    }

    private void requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    },
                    BACKGROUND_LOCATION_ACCESS_REQUEST_CODE
            );
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    FINE_LOCATION_ACCESS_REQUEST_CODE
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
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
                mMap.setMyLocationEnabled(true);
            } else {
                //We do not have the permission..

            }
        }

        if (requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have location permission
                Toast.makeText(this, getString(R.string.permision_granted_msg), Toast.LENGTH_SHORT).show();
                addMarkerAndGeo();
            } else {
                //We do not have location permission
                Toast.makeText(this, getString(R.string.permission_error), Toast.LENGTH_SHORT).show();

            }
        }
    }


    private void addGeofence(LatLng latLng, int id, float radius) {
        Geofence geofence = geofenceMonitorHelper.getGeofence(String.valueOf(id), latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);

        GeofencingRequest geofencingRequest = geofenceMonitorHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceMonitorHelper.getPendingIntent();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent).addOnSuccessListener(unused -> Log.d(TAG, "onSuccess: Geofence added."))
                .addOnFailureListener(e -> {
                    String errorMessage = geofenceMonitorHelper.getError(e);
                    Log.d(TAG, "onFailure: " + errorMessage);
                });
    }


    private void addMarkerCircle(LatLng latLng, float radius) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255, 255, 0, 0));
        circleOptions.fillColor(Color.argb(64, 255, 0, 0));
        circleOptions.strokeWidth(4);
        mMap.addCircle(circleOptions);

    }

    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            //Ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //We need to show user a dialog for displaying why the permission is needed and then ask for the permission...
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            }
        }
    }


}