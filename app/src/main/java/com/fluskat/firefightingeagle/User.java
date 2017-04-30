package com.fluskat.firefightingeagle;

import org.json.JSONObject;

/**
 * Created by Dell on 4/29/2017.
 */

public class User
{
    private String id;

    private String username;

    private String fullName;

    private String email;

    private String gcmToken;

    public User(JSONObject object)
    {
        id = object.optString("id");
        username = object.optString("username");
        email = object.optString("email");
        fullName = object.optString("name");
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getFullName()
    {
        return fullName;
    }

    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getGcmToken()
    {
        return gcmToken;
    }

    public void setGcmToken(String gcmToken)
    {
        this.gcmToken = gcmToken;
    }
}
