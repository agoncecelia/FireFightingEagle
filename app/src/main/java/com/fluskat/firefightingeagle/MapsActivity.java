package com.fluskat.firefightingeagle;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * Created by Erenis Ramadani on 13-May-17. TODO:add documentation
 */

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback
{
    private GoogleMap mMap;

    private Button report;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps_activity);
        report = (Button) findViewById(R.id.report_maps);
        report.setOnClickListener(mOnClickListener);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapArbesa);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case 150:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    initLocation();

                }
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        initLocation();
        mMap.setOnMapClickListener(mOnMapClickListener);
    }

    private void initLocation()
    {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {

            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 500, mLocationListener);
            if (mMap != null)
            {

                mMap.setMyLocationEnabled(true);
                if (location != null)
                {
                    CameraUpdate Cu = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15f);
                    mMap.animateCamera(Cu);
                }
            }
        }
        else
        {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 150);
        }
    }

    private LocationListener mLocationListener = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location location)
        {

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle)
        {

        }

        @Override
        public void onProviderEnabled(String s)
        {

        }

        @Override
        public void onProviderDisabled(String s)
        {
            //TODO: notify user to enable GPS
        }
    };

    private GoogleMap.OnMapClickListener mOnMapClickListener = new GoogleMap.OnMapClickListener()
    {
        @Override
        public void onMapClick(LatLng latLng)
        {
            mMap.addMarker(new MarkerOptions().position(latLng).snippet("Fire here"));
            report.setEnabled(true);
        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            int id = view.getId();
            switch (id)
            {
                case R.id.report_maps:
                    //TODO:implement API call
                    break;
            }
        }
    };
}
