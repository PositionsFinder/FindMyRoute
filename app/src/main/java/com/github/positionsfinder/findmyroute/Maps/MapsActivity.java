package com.github.positionsfinder.findmyroute.Maps;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.positionsfinder.findmyroute.DB_Processing.Helper_Position;
import com.github.positionsfinder.findmyroute.DB_Processing.Helper_User;
import com.github.positionsfinder.findmyroute.R;
import com.github.positionsfinder.findmyroute.Splash.Splash;
import com.github.positionsfinder.findmyroute.XmlParser.ParseXmlFile;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jdom2.JDOMException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    //private TextView info;
    private LocationManager locationManager;
    private ProgressBar pBar;
    private boolean hidePartner = false;
    private LatLng myPos;
    private FloatingActionButton fabuttonFriendRoute;
    private FloatingActionButton fabuttonCurrentLocation;
    private Marker currentMarker = null;
    private String userName;
    private String partnerName = null;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        //info = (TextView) findViewById(R.id.info);
        pBar = (ProgressBar) findViewById(R.id.progressBar);
        fabuttonFriendRoute = (FloatingActionButton) findViewById(R.id.fAButton);
        fabuttonCurrentLocation = (FloatingActionButton) findViewById(R.id.fAButtonCurrentLocation);

        // get username for greeting message
        if (getIntent().hasExtra("user")) {
            userName = getIntent().getExtras().getString("user").toString();
            Toast.makeText(this, "Hallo, " + userName + ". Please WAIT until the Location is Loaded!", Toast.LENGTH_LONG).show();
        }
        // if user offline!
        if (getIntent().hasExtra("offline")) {
            hidePartner = true;
        }

        fabuttonFriendRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (hidePartner) {
                    Toast.makeText(MapsActivity.this, "Please Login first...", Toast.LENGTH_SHORT).show();
                } else {
                    if (Splash.vpn) {
                        if (myPos != null) {
                            showConnectToFriendDialogWindow();
                        } else {
                            Toast.makeText(MapsActivity.this, "Position still Loading...", Toast.LENGTH_SHORT).show();

                        }
                    }
                }
            }
        });

        fabuttonCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myPos != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPos, (mMap.getMaxZoomLevel() - 3)));

                    final Snackbar snackBar = Snackbar.make(findViewById(android.R.id.content), "Your Position is: " + myPos, Snackbar.LENGTH_INDEFINITE);
                    snackBar.setAction("Dismiss", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackBar.dismiss();
                        }
                    });
                    snackBar.show();
                    try {
                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                        r.play();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(MapsActivity.this, "Position still Loading...", Toast.LENGTH_SHORT).show();
                }
            }
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //GPS
        locationManager = (LocationManager)

                getSystemService(LOCATION_SERVICE);


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

        //default markerr Munich.
        LatLng myPos = new LatLng(48.135, 11.58);
        //mMap.addMarker(new MarkerOptions().position(myPos).title("Munich!"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPos, (mMap.getMaxZoomLevel() - 3)));

    }

    @Override
    public void onLocationChanged(Location location) {

        if (currentMarker != null) {
            currentMarker.remove();
        }
        pBar.setVisibility(View.GONE);

        // mMap.addMarker(new MarkerOptions().position(myPos).title("My Current Place!"));
        myPos = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions myMarker = new MarkerOptions().position(myPos).title("My Current Place!");
        currentMarker = mMap.addMarker(myMarker);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myPos));

        if ((partnerName != null)) {
            calculateRoute(partnerName);
            myMarker = new MarkerOptions().position(myPos).title("My Current Place!");
            currentMarker = mMap.addMarker(myMarker);
        }
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

        switch (item.getItemId()) {
            case R.id.partner:
                if (hidePartner) {
                    Toast.makeText(this, "Please Login first...", Toast.LENGTH_SHORT).show();
                } else {
                    if (myPos != null) {
                        showConnectToFriendDialogWindow();
                    } else {
                        Toast.makeText(MapsActivity.this, "Position still Loading...", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.attractions:
                if (myPos != null) {
                    dialogAttractionWindow();
                } else {
                    Toast.makeText(MapsActivity.this, "Position still Loading...", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return true;
    }

    private void dialogAttractionWindow() {
        try {
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(MapsActivity.this);
            builderSingle.setTitle("Select an Attraction");

            AssetManager am = getAssets();
            InputStream inputStream = am.open("sqlite_xml.xml");
            ParseXmlFile parsedFile = new ParseXmlFile(inputStream);

            ArrayList<String> lStr = parsedFile.getDirectionName();

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MapsActivity.this, android.R.layout.select_dialog_singlechoice, lStr);

            builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String strName = arrayAdapter.getItem(which);
                    AlertDialog.Builder builderInner = new AlertDialog.Builder(MapsActivity.this);
                    builderInner.setMessage(parsedFile.getDescription(strName));
                    builderInner.setTitle("Route to " + strName);
                    builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mMap.clear();
                            mMap.addMarker(new MarkerOptions().position(myPos).title("My Current Place!"));
                            Helper_Position.drawDirectionsLatLng(mMap, myPos, (parsedFile.getLatLng(strName)));
                            dialog.dismiss();
                        }
                    });
                    builderInner.show();
                }
            });

            builderSingle.show();

        } catch (JDOMException | IOException e) {
            e.printStackTrace();
        }
    }

    private void showConnectToFriendDialogWindow() {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MapsActivity.this);
        builderSingle.setTitle("Select the user you want to meet");

        ArrayList<String> userNames = Helper_User.getOnlineUsers(getApplicationContext());

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MapsActivity.this, android.R.layout.select_dialog_singlechoice, userNames);

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                partnerName = arrayAdapter.getItem(which);
                AlertDialog.Builder builderInner = new AlertDialog.Builder(MapsActivity.this);

/*                HashMap<String, Object> friendsPosMap = Helper_Position.getFriendsLatestPosition(getApplicationContext(), partnerName);
                double lat = 0;
                double lon = 0;

                try {
                    lat = Double.parseDouble(friendsPosMap.get("LAT").toString());
                    lon = Double.parseDouble(friendsPosMap.get("LON").toString());
                } catch (NumberFormatException | NullPointerException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }

                Helper_Position.drawDirectionsLatLng(mMap, myPos, new LatLng(lat, lon));
                Toast.makeText(getApplicationContext(), "Friends latest Pos: " + lat + ", " + lon, Toast.LENGTH_SHORT).show();*/

                calculateRoute(partnerName);
                dialog.dismiss();

            }
        });

        builderSingle.show();

    }

    private void calculateRoute(String strName) {


        HashMap<String, Object> friendsPosMap = Helper_Position.getFriendsLatestPosition(getApplicationContext(), strName);
        double lat = 0;
        double lon = 0;

        try {
            lat = Double.parseDouble(friendsPosMap.get("LAT").toString());
            lon = Double.parseDouble(friendsPosMap.get("LON").toString());
        } catch (NumberFormatException | NullPointerException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(myPos).title("My Current Place!"));
        Helper_Position.drawDirectionsLatLng(mMap, myPos, new LatLng(lat, lon));
        Toast.makeText(getApplicationContext(), "Friends latest Pos: " + lat + ", " + lon, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        System.out.println("hello.... im in onResume");
        Helper_User.setUserOnline(getApplicationContext(), userName);
        super.onResume();
    }

    protected void onDestroy() {
        System.out.println("hallo.... im in onDestroy");
        Helper_User.setUserOffline(getApplicationContext(), userName);
        super.onDestroy();
    }
}
