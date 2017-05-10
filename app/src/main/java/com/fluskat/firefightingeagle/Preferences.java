package com.fluskat.firefightingeagle;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by Erenis Ramadani on 30-Apr-17.
 */

public class Preferences
{
    public static void setToken(Context context, String token)
    {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("token", token).apply();
    }

    public static String getToken(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("token", "");
    }

}
