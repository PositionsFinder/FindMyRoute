package com.github.positionsfinder.findmyroute.Maps;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.positionsfinder.findmyroute.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private TextView info;
    private LocationManager locationManager;
    private ProgressBar pBar;
    private boolean hidePartner = false;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        info = (TextView) findViewById(R.id.info);
        pBar = findViewById(R.id.progressBar);

        // get username for greeting message
        if (getIntent().hasExtra("username")) {
            String msg = getIntent().getExtras().getString("username").toString();
            info.setText("Hallo " + msg + ", Wait until the Location is Loaded!");
        }
        // if user offline!
        if (getIntent().hasExtra("offline")) {
            hidePartner = true;
        }


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //GPS
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        allowGPS();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void allowGPS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET
            }, 10);
            return;
        } else {
            locationManager.requestLocationUpdates("gps", 10000, 10, this);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    allowGPS();
                return;
        }
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

        LatLng myPos = new LatLng(48.135, 11.58);
        mMap.addMarker(new MarkerOptions().position(myPos).title("Munich!"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPos, (mMap.getMaxZoomLevel() - 3)));

    }

    @Override
    public void onLocationChanged(Location location) {
        pBar.setVisibility(View.GONE);

        info.setText(" Long: " + location.getLongitude() + " Lat: " + location.getLatitude());

        mMap.clear();
        LatLng myPos = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(myPos).title("My Current Place!"));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myPos));

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }


    @Override
    public void onProviderDisabled(String s) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    // Main Menu - Hide menu if Offline
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mapmenu, menu);
        return true;
    }

    // Main Menu Options - Hide menu if Offline
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();
        switch (id) {
            case R.id.partner:
                if (hidePartner) {
                    Toast.makeText(this, "Please Login first...", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Connect to Partner", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.statistic:
                Toast.makeText(this, "Statistic", Toast.LENGTH_SHORT).show();
                break;
            case R.id.attractions:
                Toast.makeText(this, "Tourist Attractions", Toast.LENGTH_SHORT).show();
                break;
        }

        return true;
    }

}