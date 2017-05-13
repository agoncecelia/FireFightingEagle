package com.fluskat.firefightingeagle;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback
{
    private static String TAG = MainActivity.class.getSimpleName();

    private GoogleMap mMap;

    private int zoomLevel;

    private int radius;

    private Button reportFire;

    private Button SOS;

    private LatLng mLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Log.d(TAG, "Token:" + FirebaseInstanceId.getInstance().getToken());
        if (getIntent().hasExtra("location"))
        {
            boolean arbesa = getIntent().getBooleanExtra("location", true);
            if (arbesa)
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 150);
            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 170);
            }
            return;
        }
        initViews();
        initMap();
        startUpdateLocationService();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case 150:
                initViews();
                initMap();
                break;
            case 170:
                startUpdateLocationService();
                break;
        }
    }

    private void initViews()
    {
        reportFire = (Button) findViewById(R.id.button_report_fire);
        SOS = (Button) findViewById(R.id.button_SOS);

        reportFire.setOnClickListener(mOnClickListener);
        SOS.setOnClickListener(mOnClickListener);
    }

    private void initMap()
    {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mapFragment.getMapAsync(this);

    }

    private void startUpdateLocationService()
    {
        Intent intent = new Intent(MainActivity.this, UpdateLocationService.class);
        startService(intent);
    }

    private void checkDanger()
    {
        String URL = ReqConstants.CHECK_DANGER;
        Log.d(TAG, "checkDanger: ");
        try
        {
            ReqUtils.jsonRequestWithParams(MainActivity.this, Request.Method.POST, URL, params(), new Response.Listener<JSONObject>()
            {
                @Override
                public void onResponse(JSONObject response)
                {
                    Log.d(TAG, "Response: " + response.toString());
                    boolean success = response.optBoolean("success");
                    if (!success)
                    {
                        Toast.makeText(MainActivity.this, response.optString("msg"), Toast.LENGTH_SHORT).show();
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
//        object.put("lat", mLocation.getLatitude());
//        object.put("lng", mLocation.getLongitude());

        return object;
    }

    private void drawMarkers(JSONArray fires)
    {
        for (int i = 0; i < fires.length(); i++)
        {
            JSONObject object = fires.optJSONObject(i);
            double lat = object.optDouble("latitude");
            double lng = object.optDouble("longitude");
            mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_red)));
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
        mMap.setOnMapClickListener(mOnMapClickListener);

       /* CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude()), 15f);
        mMap.animateCamera(cu);*/
        checkDanger();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            int id = v.getId();
            CustomAlertDialog dialog = new CustomAlertDialog(MainActivity.this);
            switch (id)
            {
                case R.id.button_SOS:
                    dialog.initQuestionDialog("Are you sure?", "Report", "Cancel", true, new CustomAlertDialog.CustomQuestionListener()
                    {
                        @Override
                        public void onOkClicked() throws Exception
                        {

                        }

                        @Override
                        public void onCancel() throws JSONException
                        {
                        }
                    });

                    break;
                case R.id.button_report_fire:
                    if (reportFire.getText().toString().toLowerCase(Locale.ENGLISH).contains("select"))
                    {
                        Toast.makeText(MainActivity.this, "Locate the fire by clicking on map", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        dialog.initQuestionDialog("Are you sure?", "Report", "Cancel", true, new CustomAlertDialog.CustomQuestionListener()
                        {
                            @Override
                            public void onOkClicked() throws Exception
                            {
                                //TODO:make request
                            }

                            @Override
                            public void onCancel() throws JSONException
                            {
                            }
                        });
                    }
                    break;
            }
        }
    };

    private GoogleMap.OnMapClickListener mOnMapClickListener = new GoogleMap.OnMapClickListener()
    {
        @Override
        public void onMapClick(LatLng latLng)
        {
            mLatLng = latLng;
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng).title("Fire").snippet("Is this fire's location?"));
            reportFire.setText("Report Fire");
        }
    };
}