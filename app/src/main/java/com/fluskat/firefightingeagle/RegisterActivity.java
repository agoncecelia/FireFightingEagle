package com.fluskat.firefightingeagle;

import android.content.Intent;
import android.os.Bundle;
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
 * Created by Erenis Ramadani on 30-Apr-17.
 */

public class RegisterActivity extends AppCompatActivity
{
    private static final String TAG = RegisterActivity.class.getSimpleName();

    private EditText mName;

    private EditText mEmail;

    private EditText mUserName;

    private EditText mPassword;

    private EditText mRepeatPassword;

    private Button mRegister;

    private String email;

    private String password;

    private String userName;

    private String name;

    private RegisterActivity getContext()
    {
        return RegisterActivity.this;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
    }

    //----------------------------------------------------------------------------------------

    private void initViews()
    {
        mName = (EditText) findViewById(R.id.widget_edit_text_name_register);
        mEmail = (EditText) findViewById(R.id.widget_edit_text_email_register);
        mUserName = (EditText) findViewById(R.id.widget_edit_text_username_register);
        mPassword = (EditText) findViewById(R.id.widget_edit_text_password_register);
        mRepeatPassword = (EditText) findViewById(R.id.widget_edit_text_repeat_password_register);
        mRegister = (Button) findViewById(R.id.widget_button_register);

        mRegister.setOnClickListener(mOnClickListener);
    }

    private boolean validate()
    {
        boolean valid = true;
        name = mName.getText().toString();
        email = mEmail.getText().toString();
        password = mPassword.getText().toString();
        userName = mUserName.getText().toString();
        String repeatPassword = mRepeatPassword.getText().toString();
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
        if (repeatPassword.equals(""))
        {
            valid = false;
            mRepeatPassword.setError("Required");
        }
        if (!password.equals(repeatPassword))
        {
            valid = false;
            mRepeatPassword.setError("Passwords does not match");
            mPassword.setError("Passwords does not match");
        }
        if (userName.equals(""))
        {
            valid = false;
            mUserName.setError("Required");
        }
        if (name.equals(""))
        {
            valid = false;
            mName.setError("Required");
        }
        return valid;
    }

    private void makeRequest() throws JSONException
    {
        ReqUtils.jsonRequestWithParams(getContext(), Request.Method.POST, ReqConstants.REGISTER, params(), new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response)
            {
                Log.d(TAG, "Response: " + response);
                boolean success = response.optBoolean("success");
                if (success)
                {
                    Utils.mUser = new User(response.optJSONObject("user"));
                    Preferences.setToken(getContext().getBaseContext(), response.optString("token"));

                    Intent intent = new Intent(getContext(), MainActivity.class);
                    startActivity(intent);
                    getContext().finish();
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
        object.put("name", name);
        object.put("username", userName);
        object.put("password", password);
        object.put("email", email);
        return object;
    }

    //-----------------------------------------------------------------------------------------

    private View.OnClickListener mOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            int id = v.getId();

            switch (id)
            {
                case R.id.widget_button_register:
                    if (validate())
                    {
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