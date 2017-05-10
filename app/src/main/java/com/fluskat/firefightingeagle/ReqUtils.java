package com.fluskat.firefightingeagle;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Erenis on 05-Feb-16.
 */
public class ReqUtils {
    private static final String TAG = ReqUtils.class.getSimpleName();

    public static void jsonRequestWithHeaders(Context act, int reqType, String url, final Map<String, String> header,
                                              Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        JsonObjectRequest request = new JsonObjectRequest(reqType, url, null, listener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return header;
            }
        };
        MySingleton.getInstance(act).addToRequestQueue(request);
    }

    public static void stringRequestWithHeaders(Activity act, int reqType, String url, Response.Listener<String> listener,
                                                Response.ErrorListener errorListener, final Map<String, String> headers) {
        StringRequest request = new StringRequest(reqType, url, listener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }
        };
        MySingleton.getInstance(act).addToRequestQueue(request);
    }

    public static void jsonRequestWithParams(Context act, int reqType, String url, JSONObject params,
                                             Response.Listener<JSONObject> listener,
                                             Response.ErrorListener errorListener) {
        JsonObjectRequest request = new JsonObjectRequest(reqType, url, params, listener, errorListener);
        MySingleton.getInstance(act).addToRequestQueue(request);
    }

    public static void jsonRequestWithHeadersAndParams(Activity act, int reqType, String url,
                                                       final Map<String, String> headers, JSONObject params,
                                                       Response.Listener<JSONObject> listener,
                                                       Response.ErrorListener errorListener) {
        JsonObjectRequest request = new JsonObjectRequest(reqType, url, params, listener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }
        };
        try {
            Log.d("TAG12", "Request token: " + request.getHeaders().get("Authorization"));
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }
        MySingleton.getInstance(act).addToRequestQueue(request);
    }
}
