package com.fluskat.firefightingeagle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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


public class Calculator extends AppCompatActivity {
    private static String TAG = Calculator.class.getSimpleName();
    private EditText mWindSpeed;
    private EditText mTemperature;
    private EditText mHumidity;
    private TextView mRiskLevel;
    private Button btn;

    private double windspeed, humidity, temperature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        initViews();
        windspeed = Double.valueOf(mWindSpeed.getText().toString());
        humidity = Double.valueOf(mHumidity.getText().toString());
        temperature = Double.valueOf(mTemperature.getText().toString());
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                switch (id) {
                    case R.id.calculate_btn:
                        try {
                            makeRequest();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });

    }

    public void initViews() {
        mWindSpeed = (EditText) findViewById(R.id.windSpeed);
        mTemperature = (EditText) findViewById(R.id.temperature);
        mHumidity = (EditText) findViewById(R.id.humidity);
        mRiskLevel = (TextView) findViewById(R.id.riskLevelText);
        btn = (Button) findViewById(R.id.calculate_btn);
    }
    private Calculator getContext() {
        return Calculator.this;
    }


    private void makeRequest() throws JSONException
    {

        JSONObject object = new JSONObject();
        object.put("windSpeed", windspeed);
        object.put("temperature", temperature);
        object.put("humidity", humidity);
        ReqUtils.jsonRequestWithParams(getContext(), Request.Method.POST, ReqConstants.LOGIN, object, new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response)
            {
                Log.d(TAG, "Response: " + response);
                boolean success = response.optBoolean("success");
//                if (success)
//                {
//                    Preferences.setToken(getContext().getBaseContext(), response.optString("token"));
//                    Utils.mUser = new User(response.optJSONObject("user"));
//
//                    //TODO: move to MainActivity
//                    Intent intent = new Intent(getContext(), MapsActivity.class);
//                    startActivity(intent);
//                    getContext().finish();
//                }
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

}
