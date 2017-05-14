package com.fluskat.firefightingeagle;

import android.content.Context;
import android.telephony.TelephonyManager;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Erenis Ramadani on 30-Apr-17.
 */

public class Utils
{

    public static User mUser;

    public static String getIMEI(Context context)
    {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    public static HttpURLConnection checkInternet() throws IOException
    {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://www.google.com").openConnection();
        connection.setRequestProperty("User-Agent", "Test");
        connection.setRequestProperty("Connection", "close");
        connection.setConnectTimeout(2500);
        connection.connect();
        return connection;
    }
}
