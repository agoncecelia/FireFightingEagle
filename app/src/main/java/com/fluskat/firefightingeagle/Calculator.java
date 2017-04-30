package com.fluskat.firefightingeagle;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;


public class Calculator extends AppCompatActivity
{
    private static String TAG = Calculator.class.getSimpleName();

    private EditText mWindSpeed;

    private EditText mTemperature;

    private EditText mHumidity;

    private TextView mRiskLevel;

    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        initViews();
        btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int id = v.getId();
                switch (id)
                {
                    case R.id.calculate_btn:
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
        });

    }

    public void initViews()
    {
        mWindSpeed = (EditText) findViewById(R.id.windSpeed);
        mTemperature = (EditText) findViewById(R.id.temperature);
        mHumidity = (EditText) findViewById(R.id.humidity);
        mRiskLevel = (TextView) findViewById(R.id.riskLevelText);
        btn = (Button) findViewById(R.id.calculate_btn);
    }

    private Calculator getContext()
    {
        return Calculator.this;
    }


    private void makeRequest() throws JSONException
    {
        double windspeed = Double.valueOf(mWindSpeed.getText().toString());
        double humidity = Double.valueOf(mHumidity.getText().toString());
        double temperature = Double.valueOf(mTemperature.getText().toString());
        JSONObject object = new JSONObject();
        object.put("windSpeed", windspeed);
        object.put("temperature", temperature);
        object.put("humidity", humidity);
        ReqUtils.jsonRequestWithParams(getContext(), Request.Method.POST, ReqConstants.CALCULATE, object, new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response)
            {
                Log.d(TAG, "Response: " + response);
                mRiskLevel.setText("Danger level: " + response.optString("riskLevel") + "(" + response.optInt("riskSum") + ")");
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

    private boolean validate()
    {
        boolean valid = true;
        if (mHumidity.getText().toString().equals(""))
        {
            mHumidity.setError("Required");
            valid = false;
        }
        if (mTemperature.getText().toString().equals(""))
        {
            mTemperature.setError("Required");
            valid = false;
        }
        if (mWindSpeed.getText().toString().equals(""))
        {
            mWindSpeed.setError("Required");
            valid = false;
        }
        return valid;
    }
}