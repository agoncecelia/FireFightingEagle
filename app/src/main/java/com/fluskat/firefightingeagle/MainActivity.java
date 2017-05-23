package com.fluskat.firefightingeagle;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
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

    private Button reportFire;

    private Button SOS;

    private LatLng mLatLng;

    private Location mLocation;

    private LocationManager locationManager;

    private MainActivity getContext()
    {
        return MainActivity.this;
    }

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
                try
                {
                    startUpdateLocationService();
                    updateLocation();
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
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

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(getContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 150);
            return;
        }
        mLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 100, mLocationListener);
        mapFragment.getMapAsync(this);

    }

    private void startUpdateLocationService()
    {
        Intent intent = new Intent(getContext(), UpdateLocationService.class);
        startService(intent);
    }

    private void drawMarkers(JSONArray fires)
    {
        for (int i = 0; i < fires.length(); i++)
        {
            JSONObject object = fires.optJSONObject(i);
            double lat = object.optDouble("latitude");
            double lng = object.optDouble("longitude");
            mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_red)));
        }
    }

    private void reportFire(LatLng location, boolean sos) throws JSONException
    {
        ReqUtils.jsonRequestWithParams(getContext(), Request.Method.POST, ReqConstants.REPORT_FIRE, report(location, sos),
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d(TAG, "Response: " + response);
                        int status = response.optInt("status");
                        if (status == 200)
                        {
                            Toast.makeText(getContext(), response.optString("msg"), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getContext(), MarkMeSafeActivity.class));
                        }
                    }
                }, new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Log.d(TAG, "Error: " + error.getMessage());
                    }
                });
    }

    private JSONObject params() throws JSONException
    {
        JSONObject object = new JSONObject();
        String gcmToken = FirebaseInstanceId.getInstance().getToken();
        object.put("gcmToken", gcmToken);
        object.put("deviceIMEI", Utils.getIMEI(getContext()));

        JSONObject locationObject = new JSONObject();
        JSONArray arr = new JSONArray();
        arr.put(mLocation.getLatitude());
        arr.put(mLocation.getLongitude());
        locationObject.put("coordinates", arr);
        object.put("location", locationObject);

        Log.d(TAG, "Object: " + object.toString());
        return object;
    }

    private JSONObject report(LatLng location, boolean sos) throws JSONException
    {
        JSONObject object = new JSONObject();
        JSONObject locationObject = new JSONObject();
        JSONArray arr = new JSONArray();
        arr.put(location.latitude);
        arr.put(location.longitude);
        locationObject.put("coordinates", arr);
        object.put("location", locationObject);
        object.put("sos", sos);
        Log.d(TAG, "Report object: " + object);
        return object;
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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 150);
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(mOnMapClickListener);

        if (mLocation != null)
        {
            CameraUpdate cu = CameraUpdateFactory
                    .newLatLngZoom(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), 15f);
            mMap.animateCamera(cu);
            try
            {
                updateLocation();
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        getActiveFires();
//        checkDanger();
    }

    private void updateLocation() throws JSONException
    {
        Log.d(TAG, "update Location called");
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) ==
                PackageManager.PERMISSION_GRANTED)
        {
            ReqUtils.jsonRequestWithParams(getContext(), Request.Method.PUT, ReqConstants.UPDATE_LOCATION, params(),
                    new Response.Listener<JSONObject>()
                    {
                        @Override
                        public void onResponse(JSONObject jsonObject)
                        {
                            Log.d(TAG, "onResponse: " + jsonObject);
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError volleyError)
                        {
                            Log.d(TAG, "onErrorResponse:" + volleyError.getMessage());
                        }
                    });
        }
        else
        {
            ActivityCompat.requestPermissions(getContext(), new String[]{Manifest.permission.READ_PHONE_STATE}, 170);
        }
    }

    private void getActiveFires()
    {
        ReqUtils.jsonRequestWithParams(getContext(), Request.Method.GET, ReqConstants.GET_ACTIVE_FIRES, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        JSONArray array = response.optJSONArray("response");
                        Log.d(TAG, "onResponse fires: " + array);
                    }
                }, new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError volleyError)
                    {

                    }
                });
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            int id = v.getId();
            CustomAlertDialog dialog = new CustomAlertDialog(getContext());
            switch (id)
            {
                case R.id.button_SOS:
                    dialog.initQuestionDialog("Are you sure?", "Report", "Cancel", true,
                            new CustomAlertDialog.CustomQuestionListener()
                            {
                                @Override
                                public void onOkClicked()
                                {
                                    new CheckConnectionTask(new CheckConnectionTask.ConnectionListener()
                                    {
                                        @Override
                                        public void isConnected()
                                        {
                                            try
                                            {
                                                if (mLocation != null)
                                                {
                                                    Log.d(TAG, "isConnected location");
                                                    reportFire(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()),
                                                            true);
                                                }
                                                else
                                                {
                                                    Log.d(TAG, "isConnected else");
                                                    Intent intent = new Intent(Intent.ACTION_DIAL);
                                                    startActivity(intent);
                                                }
                                            }
                                            catch (JSONException e)
                                            {
                                                Log.d(TAG, "isConnected catch");
                                                e.printStackTrace();
                                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                                startActivity(intent);
                                            }
                                        }

                                        @Override
                                        public void notConnected()
                                        {
                                            Log.d(TAG, "notConnected");
                                        }
                                    }).execute();
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
                        Toast.makeText(getContext(), "Locate the fire by clicking on map", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        dialog.initQuestionDialog("Are you sure?", "Report", "Cancel", true,
                                new CustomAlertDialog.CustomQuestionListener()
                                {
                                    @Override
                                    public void onOkClicked() throws Exception
                                    {
                                        reportFire(mLatLng, false);
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

    private LocationListener mLocationListener = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location location)
        {
            mLocation = location;
            if (mMap != null)
            {
                CameraUpdate cu = CameraUpdateFactory
                        .newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15f);
                mMap.animateCamera(cu);
            }
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
            Toast.makeText(getContext(), "Please enable GPS!", Toast.LENGTH_SHORT).show();
        }
    };
}