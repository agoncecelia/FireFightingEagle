package com.fluskat.firefightingeagle.gcm;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by Erenis on 13-Jul-16.
 */
public class MyFcmListenerService extends FirebaseMessagingService
{
    private static final String TAG = MyFcmListenerService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
//        super.onMessageReceived(remoteMessage);
        String from = remoteMessage.getFrom();
        Map data = remoteMessage.getData();

        Log.d(TAG, from + ", " + data);
    }
}
