package com.fluskat.firefightingeagle;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * Created by Erenis Ramadani on 30-Apr-17.
 */

public class Utils {

    public static User mUser;

    public static String getIMEI(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }
}
