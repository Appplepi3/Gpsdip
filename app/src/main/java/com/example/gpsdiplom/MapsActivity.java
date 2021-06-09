package com.example.gpsdiplom;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModel;


import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.example.gpsdiplom.App;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnPolygonClickListener, GoogleMap.OnPolylineClickListener {

    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private UiSettings mUiSettings;
    private LocationManager locationManager;
    private boolean isShowPopularPlaces = false;
    ArrayList<LatLng> llist = new ArrayList<LatLng>();

    LatLngBounds.Builder b = new LatLngBounds.Builder();

    boolean longpress = false;
    private double lat;
    private double clat;
    private double lon;
    private double clon;
    HelperDB helper;
    SQLiteDatabase gpsDB;
    Cursor infoDB;

    String date;

    Button start_save_line;
    LocationListener locationlistener;
    PolylineOptions pOptions = new PolylineOptions();
    Polyline polygon;
    LatLng latLng;
    float ppp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_launcher);
        setTitle("Карта");

        helper = new HelperDB(this);
        gpsDB = helper.getWritableDatabase();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        start_save_line = findViewById(R.id.start_save_line);
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
        mUiSettings = mMap.getUiSettings();

        mMap.setOnMyLocationButtonClickListener(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //mMap.setMyLocationEnabled(true);
        //mUiSettings.setMyLocationButtonEnabled(true);
        enableMyLocation();

        start_save_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_save_line.setSelected(!start_save_line.isSelected());
                if (start_save_line.isSelected()) {
                    if (isShowPopularPlaces) {
                        setShowPopularPlaces();
                    }
                    Log.i("***", "onClick: OFF");

                    start_save_line.setText("Закончить запись маршрута");

                    Location mylocation = mMap.getMyLocation();

                    llist.add(new LatLng(mylocation.getLatitude(), mylocation.getLongitude()));
                    mMap.addMarker(new MarkerOptions().position(llist.get(0)).title("Start Point"));
                    pOptions.add(new LatLng(mylocation.getLatitude(), mylocation.getLongitude()));
                    b.include(llist.get(0));
                    Toast.makeText(MapsActivity.this, "Запись маршрута начата", Toast.LENGTH_SHORT).show();

                } else {
                    Log.i("***", "onClick: ON");
                    start_save_line.setText("Начать запись маршрута");

                    Toast.makeText(MapsActivity.this, "Запись маршрута окончена", Toast.LENGTH_SHORT).show();

                    Location mylocation = mMap.getMyLocation();

                    llist.add(new LatLng(mylocation.getLatitude(), mylocation.getLongitude()));
                    mMap.addMarker(new MarkerOptions().position(new LatLng(mylocation.getLatitude(), mylocation.getLongitude())).title("End Point"));

                    mMap.getUiSettings().setZoomControlsEnabled(true);
                    pOptions.add(new LatLng(mylocation.getLatitude(), mylocation.getLongitude()));
                    b.include(new LatLng(mylocation.getLatitude(), mylocation.getLongitude()));
                    LatLngBounds bounds = b.build();

                    int width = getResources().getDisplayMetrics().widthPixels;
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200, 200, 5);
                    mMap.animateCamera(cu);
                }
            }
        });


        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                if (!longpress) {
                    if (isShowPopularPlaces) {
                        setShowPopularPlaces();
                    }
                    Location mylocation = mMap.getMyLocation();
                    llist.add(new LatLng(mylocation.getLatitude(), mylocation.getLongitude()));
                    mMap.addMarker(new MarkerOptions().position(llist.get(0)).title("Start Point"));
                    pOptions.add(new LatLng(mylocation.getLatitude(), mylocation.getLongitude()));
                    b.include(llist.get(0));
                    longpress = true;

                    String latitude2 = mylocation.getLatitude() + "";
                    String longitude2 = mylocation.getLatitude() + "";
                    String date = (new Date()) + "";

                    Toast.makeText(MapsActivity.this, "Запись маршрута начата", Toast.LENGTH_SHORT).show();
                } else {
                    longpress = false;

                    Toast.makeText(MapsActivity.this, "Запись маршрута окончена", Toast.LENGTH_SHORT).show();

                    Location mylocation = mMap.getMyLocation();

                    llist.add(new LatLng(mylocation.getLatitude(), mylocation.getLongitude()));
                    mMap.addMarker(new MarkerOptions().position(new LatLng(mylocation.getLatitude(), mylocation.getLongitude())).title("End Point"));

                    mMap.getUiSettings().setZoomControlsEnabled(true);
                    pOptions.add(new LatLng(mylocation.getLatitude(), mylocation.getLongitude()));
                    b.include(new LatLng(mylocation.getLatitude(), mylocation.getLongitude()));
                    LatLngBounds bounds = b.build();

                    int width = getResources().getDisplayMetrics().widthPixels;
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200, 200, 5);
                    mMap.animateCamera(cu);

                }
            }
        });
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
            mUiSettings.setMyLocationButtonEnabled(true);
            mUiSettings.setZoomControlsEnabled(true);
            mUiSettings.setCompassEnabled(true);
            mUiSettings.setAllGesturesEnabled(true);
            mUiSettings.setRotateGesturesEnabled(true);
        }
    }


    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "Моё местоположение", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onPolygonClick(Polygon polygon) { }

    @Override
    public void onPolylineClick(Polyline polyline) { }

    @Override
    protected void onResume() {
        super.onResume();
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("GPS не включено").setMessage("Включить GPS")
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    }).setNegativeButton("Нет", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else {
            locationlistener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.d("Latitude and Longitude:", location.getLatitude() + " " + location.getLongitude());
                    if (longpress) {
                        lat = location.getLatitude();
                        lon = location.getLongitude();

                        LatLng toAdd = new LatLng(location.getLatitude(), location.getLongitude());
                        llist.add(toAdd);

                        pOptions.add(toAdd);

                        mMap.getUiSettings().setZoomControlsEnabled(true);

                        b.include(toAdd);
                        polygon = mMap.addPolyline(pOptions);
                        LatLngBounds bounds = b.build();
                        int width = getResources().getDisplayMetrics().widthPixels;
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200, 200, 5);
                        mMap.animateCamera(cu);
                    }
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

            if ((ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationlistener);
        }
    }

    private int getRadiusByZoomMap(float zoom) {
        if (zoom > 14) {
            return 100;
        }
        if (zoom > 10) {
            return 1000;
        }
        if (zoom > 5) {
            return 20000;
        }
        if (zoom > 2) {
            return 200000;
        }
        return 500000;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onclickShowPopularPlaces(View view) {
        setShowPopularPlaces();
    }

    public void setShowPopularPlaces() {
        isShowPopularPlaces = !isShowPopularPlaces;
        Button btn = findViewById(R.id.btnStartService);
        btn.setText(isShowPopularPlaces ? "Скрыть популярные места" : "Показать популярные места");
        showPopularPlaces();
    }

    public void showPopularPlaces() {

        if (!isShowPopularPlaces) {
            mMap.clear();
            return;
        }
        infoDB = helper.getCursor();

        if (!infoDB.moveToFirst()) {
            mMap.clear();
            return;
        }

        float zoom = mMap.getCameraPosition().zoom;
        Double latitude = 0.0, longitude = 0.0;

        int dbLat = infoDB.getColumnIndex("latitude"),
                dbLong = infoDB.getColumnIndex("longitude");

        do {
            latitude = Double.parseDouble(infoDB.getString(dbLat));
            longitude = Double.parseDouble(infoDB.getString(dbLong));
            mMap.clear();
            Circle circle = mMap.addCircle(new CircleOptions()
                    .center(new LatLng(latitude, longitude))
                    .radius(getRadiusByZoomMap(zoom))
                    .strokeWidth((float) 1)
                    .strokeColor(Color.rgb(40, 120, 181))
                    .fillColor(Color.argb(30, 40, 120, 182)));

        } while (infoDB.moveToNext());
    }

    public void onclickhom(View view) {
        startActivity(new Intent(MapsActivity.this, MainActivity.class));
    }

    public void onclickhomemaps(View view) {
        startActivity(new Intent(MapsActivity.this, MainActivitydistant.class));
    }

}
