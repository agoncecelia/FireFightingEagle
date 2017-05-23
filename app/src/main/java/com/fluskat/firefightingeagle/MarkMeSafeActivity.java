package com.fluskat.firefightingeagle;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by A.Kajtazi on 5/9/2017. TODO:
 */

public class MarkMeSafeActivity extends AppCompatActivity
{
    private static final String TAG = MarkMeSafeActivity.class.getCanonicalName();

    private Button markMeSafe;

    private Location mLocation;

    private MarkMeSafeActivity getContext()
    {
        return MarkMeSafeActivity.this;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_safe);
        markMeSafe = (Button) findViewById(R.id.mark_me_safe);
        markMeSafe.setOnClickListener(mOnClickListener);
        initLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case 200:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    try
                    {
                        markMeSafe();
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(getContext(), "You must provide permission in order to complete the request!",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void initLocation()
    {

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(getContext(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 150);
            return;
        }
        mLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 100, mLocationListener);
    }

    private void markMeSafe() throws JSONException
    {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) ==
                PackageManager.PERMISSION_GRANTED)
        {
            JSONObject params = new JSONObject();
            params.put("imei", Utils.getIMEI(getContext()));
            String url = ReqConstants.MARK_ME_SAFE;
            ReqUtils.jsonRequestWithParams(getContext(), Request.Method.PUT, url, params, new Response.Listener<JSONObject>()
            {
                @Override
                public void onResponse(JSONObject jsonObject)
                {
                    Log.d(TAG, "onResponse: " + jsonObject);

                }
            }, new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError volleyError)
                {
                    Log.d(TAG, "onResponse: " + volleyError.getMessage());
                }
            });
        }
        else
        {
            ActivityCompat.requestPermissions(getContext(), new String[]{Manifest.permission.READ_PHONE_STATE}, 200);
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            int id = v.getId();
            switch (id)
            {
                case R.id.mark_me_safe:
                    try
                    {
                        markMeSafe();
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    break;
            }
        }

    };

    private LocationListener mLocationListener = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location location)
        {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {

        }

        @Override
        public void onProviderEnabled(String provider)
        {

        }

        @Override
        public void onProviderDisabled(String provider)
        {

        }
    };
}
