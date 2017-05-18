package com.fluskat.firefightingeagle;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by A.Kajtazi on 5/10/2017.
 */

public class UpdateLocationService extends Service
{
    private static final String TAG = UpdateLocationService.class.getSimpleName();

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 500, mLocationListener);
        }
        else
        {
            sendNotification(true);
        }

    }

    private void sendNotification(boolean arbesa)
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("location", arbesa);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Notification missing")
                        .setContentText("You have to allow the app to use your location")
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }

    private LocationListener mLocationListener = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location location)
        {
//            mLocation = location;
            try
            {
                updateLocation(location);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
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

    private void updateLocation(Location location) throws JSONException
    {
        Log.d(TAG, "update Location called");
        if (ActivityCompat.checkSelfPermission(UpdateLocationService.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
        {
            ReqUtils.jsonRequestWithParams(UpdateLocationService.this, Request.Method.PUT, ReqConstants.UPDATE_LOCATION, params(location), mListener,
                                           mErrorListener);
        }
        else
        {
            sendNotification(false);
        }
    }

    private JSONObject params(Location location) throws JSONException
    {
        JSONObject object = new JSONObject();
        String gcmToken = FirebaseInstanceId.getInstance().getToken();
        object.put("gcmToken", gcmToken);
        object.put("deviceIMEI", Utils.getIMEI(UpdateLocationService.this));

        JSONObject locationObject = new JSONObject();
        JSONArray arr = new JSONArray();
        arr.put(location.getLatitude());
        arr.put(location.getLongitude());
        locationObject.put("coordinates", arr);
        object.put("location", locationObject);

        Log.d(TAG, "Object: " + object.toString());
        return object;
    }

    private Response.Listener<JSONObject> mListener = new Response.Listener<JSONObject>()
    {
        @Override
        public void onResponse(JSONObject jsonObject)
        {

        }
    };
    private Response.ErrorListener mErrorListener = new Response.ErrorListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {

        }
    };
}
