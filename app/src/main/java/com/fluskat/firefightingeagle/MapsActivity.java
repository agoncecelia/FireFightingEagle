package com.fluskat.firefightingeagle;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback
{
    private static String TAG = MapsActivity.class.getSimpleName();

    private GoogleMap mMap;

    private int zoomLevel;

    private int radius;

    private LocationManager locationManager;

    private Location mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mapFragment.getMapAsync(this);
    }

    private void checkDanger()
    {
        String URL = ReqConstants.CHECK_DANGER;
        try
        {
            ReqUtils.jsonRequestWithParams(MapsActivity.this, Request.Method.POST, URL, params(), new Response.Listener<JSONObject>()
            {
                @Override
                public void onResponse(JSONObject response)
                {
                    Log.d(TAG, "Response: " + response.toString());
                    boolean success = response.optBoolean("success");
                    if (!success)
                    {
                        Toast.makeText(MapsActivity.this, response.optString("msg"), Toast.LENGTH_SHORT).show();
                        JSONArray fires = response.optJSONArray("fires");
                        drawMarkers(fires);
                    }
                    else
                    {

                    }
                }
            }, new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    if (error.networkResponse != null && error.networkResponse.data != null)
                    {
                        String string = new String(error.networkResponse.data);
                        Log.d(TAG, "Response Error: " + string);
                    }
                    Log.d(TAG, "Response Error: " + error.getMessage());
                }
            });
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private JSONObject params() throws JSONException
    {
        JSONObject object = new JSONObject();
        object.put("lat", mLocation.getLatitude());
        object.put("lng", mLocation.getLongitude());

        return object;
    }

    private void drawMarkers(JSONArray fires)
    {
        for (int i = 0; i < fires.length(); i++)
        {
            JSONObject object = fires.optJSONObject(i);
            double lat = object.optDouble("latitude");
            double lng = object.optDouble("longitude");
            mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)));
        }
    }

    private void setZoomLevel()
    {
        double scale;
        scale = radius / 500;
        zoomLevel = (int) (16 - Math.log(scale) / Math.log(2));
//        return
    }

    //------------------------------------------------------------------------------------------------------------------------------

    private LocationListener mLocationListener = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location location)
        {
            mLocation = location;
            Log.d(TAG, "onLocationChanged: " + location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.d(TAG, "OnStatusChanged: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.d(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.d(TAG, "onProviderDisabled: " + provider);
        }
    };

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
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 150);
            return;
        }
        mMap.setMyLocationEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            mLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 500, mLocationListener);
        }
        else
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 150);
        }
        if (mLocation != null)
        {
            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), 15f);
            mMap.animateCamera(cu);
        }
        checkDanger();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            int id = v.getId();
            switch (id)
            {

            }
        }
    };
}
