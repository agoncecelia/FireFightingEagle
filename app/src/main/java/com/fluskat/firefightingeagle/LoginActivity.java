package com.fluskat.firefightingeagle;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Erenis Ramadani on 30-Apr-17. Docs
 */

public class LoginActivity extends AppCompatActivity
{
    private static final String TAG = LoginActivity.class.getSimpleName();

    private EditText mEmail;

    private EditText mPassword;

    private String email;

    private String password;

    private LoginActivity getContext()
    {
        return LoginActivity.this;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
    }

    //------------------------------------------------------------------------------------------------------

    private void initViews()
    {
        mEmail = (EditText) findViewById(R.id.widget_edit_text_email_login);
        mPassword = (EditText) findViewById(R.id.widget_edit_text_password_login);
        Button mLogin = (Button) findViewById(R.id.widget_button_login);

        mLogin.setOnClickListener(mOnClickListener);
    }

    private boolean validate()
    {
        boolean valid = true;
        email = mEmail.getText().toString();
        password = mPassword.getText().toString();
        if (email.equals(""))
        {
            valid = false;
            mEmail.setError("Required");
        }
        if (password.equals("") || password.length() < 6)
        {
            valid = false;
            mPassword.setError("Required or too short");
        }

        return valid;
    }

    private void makeRequest() throws JSONException
    {
        ReqUtils.jsonRequestWithParams(getContext(), Request.Method.POST, ReqConstants.LOGIN, params(), new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response)
            {
                Log.d(TAG, "Response: " + response);
                boolean success = response.optBoolean("success");
                if (success)
                {
                    Preferences.setToken(getContext().getBaseContext(), response.optString("token"));
                    User user = new User(response.optJSONObject("user"));

                    //TODO: move to MainActivity
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.d(TAG, "Response Error: " + error.getMessage());
            }
        });
    }

    private JSONObject params() throws JSONException
    {
        JSONObject object = new JSONObject();
        object.put("username", email);
        object.put("password", password);

        return object;
    }

    //-----------------------------------------------------------------------------------------------------

    private View.OnClickListener mOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            int id = v.getId();
            switch (id)
            {
                case R.id.widget_button_login:
                    if (validate())
                    {
                        //Make request
                        try
                        {
                            makeRequest();
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    };
}
