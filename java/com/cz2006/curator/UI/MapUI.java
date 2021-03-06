package com.cz2006.curator.UI;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.cz2006.curator.Managers.MapManager;
import com.cz2006.curator.Objects.Museum;
import com.cz2006.curator.Objects.User;
import com.cz2006.curator.R;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

/**
 * MapUI is a boundary class for displaying location of museum in map and
 * getting direction to a certain museum.
 */
public class MapUI extends AppCompatActivity
        implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnInfoWindowClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;

    private GoogleMap mMap;
    //private KmlLayer kl;
    private MapManager mapManager;
    private ArrayList<Museum> museumList;
    private GoogleApiClient client;
    public final static String EXTRA_MESSAGE = "com.cz2006.curator.MESSAGE";

    @Override
     public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.map_ui_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        int id = item.getItemId();
        if(id == R.id.action_search){
            Intent it = new Intent(this,SearchUI.class);

            if(museumList == null || museumList.size() == 0){
                mapManager.refresh();
                museumList = mapManager.getMuseumList();
            }

            Log.e("MapUI","Passing museumList");
            if(museumList == null || museumList.size() == 0){
                Log.e("WARNING","still null");
                return true;
            }
            else Log.e("WARNING","GOOD JOB");
            int i = 0;
            for(Museum m:museumList){
                Log.e(Integer.valueOf(++i).toString(),m.getName());
            }

            it.putExtra("museumList",museumList);
            it.putExtra("userLocation", getUserLocation());

            startActivity(it);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This is a method to get current user location for calculating distance.
     * @return User object with current location as attribute
     */
    public User getUserLocation(){
        Location loc = mMap.getMyLocation();
            if(loc!=null){
                double lat = loc.getLatitude();
                double lon = loc.getLongitude();
                return new User(lat,lon);
            }
            else return new User(0,0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_screen);

        //ActionBar bar = getSupportActionBar();
        //bar.

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("resuming","please wait...");
        //mapManager.refresh(mMap);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();

        mMap.setOnInfoWindowClickListener(this);
        mapManager = new MapManager(mMap);
        mapManager.refresh();

        if(museumList == null || museumList.size() == 0) {
            museumList = mapManager.getMuseumList();
        }

        LatLng museumLoc = new LatLng(1.297240598945015, 103.850948911553);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(museumLoc));
        mMap.animateCamera( CameraUpdateFactory.zoomTo( 15.0f ) );

    }


    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
        else if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "MapUI Page",
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                Uri.parse("android-app://com.cz2006.curator/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "MapUI Page",
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                Uri.parse("android-app://com.cz2006.curator/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        //Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onInfoWindowClick(final Marker marker) {
        Intent intent = new Intent(this, MuseumUI.class);
        String message = mapManager.findID(marker.getTitle());
        // Name should be placed in title
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }
}
