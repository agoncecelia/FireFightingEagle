package com.fluskat.firefightingeagle;

/**
 * Created by Erenis Ramadani on 30-Apr-17. TODO
 */

public class ReqConstants
{
    public static String HOST = "http://10.10.26.20:3000/";

    public static String USERS = HOST + "users/";

    public static String LOGIN = USERS + "authenticate";

    public static String REGISTER = USERS + "register";

    public static String PROFILE = USERS + "profile";

    public static String CALCULATE = HOST + "calculate";

    public static String CHECK_DANGER = HOST + "checkdanger";

    public static String UPDATE_LOCATION = HOST + "receiveLocation";
}
