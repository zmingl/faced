package com.example.mei.faced;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class ServiceProvider {

    private static ServiceProvider instance = null;
    private RequestQueue requestQueue;
    private static Context ctx;


    private ServiceProvider(Context context) {
        this.ctx = context;
        if(requestQueue == null)
            requestQueue = getRequestQueue();
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }


    public static synchronized ServiceProvider getInstance(Context context) {
        if (instance == null) {
            instance = new ServiceProvider(context);
        }
        return instance;
    }


    public void request(String url, JSONObject jsonObject, Response.Listener listener, Response.ErrorListener errorListener) {
//        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(url,jsonObject, listener, errorListener);
        requestQueue.add(request);
//        return future;
    }
}
