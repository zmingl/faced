package com.example.mei.faced;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ServiceProvider {

    private static ServiceProvider instance = null;
    private RequestQueue requestQueue;
    private static Context ctx;
    private RetryPolicy timeoutPolicy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);



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


    public<T> void request(String url, String body, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        HttpRequest request = new HttpRequest<T>(url,body, listener, errorListener);
        request.setRetryPolicy(timeoutPolicy);
        requestQueue.add(request);
    }
}

class HttpRequest<T> extends JsonRequest<T> {

    public HttpRequest(String url, String body, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(body == null ? Method.GET : Method.POST,url,body,listener,errorListener);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        String COOKIE_KEY = "Cookie";
        Map<String,String> cookies = new HashMap<>();
        cookies.put(COOKIE_KEY,CookieManager.getCookies());
        return cookies;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        String SET_COOKIE_KEY = "Set-Cookie";
        if (response.headers.containsKey(SET_COOKIE_KEY)) {
            String cookies = response.headers.get(SET_COOKIE_KEY);
            CookieManager.setCookies(cookies);
        }


        try {
            String jsonString =
                    new String(
                            response.data,
                            HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            try {
                return Response.success((T)(new JSONObject(jsonString)), HttpHeaderParser.parseCacheHeaders(response));
            } catch (Exception e) {
                return Response.success((T)(new JSONArray(jsonString)), HttpHeaderParser.parseCacheHeaders(response));
            }
        }
        catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }

    }
}

class CookieManager {
    private static String cookies;
    public static void setCookies(String c) {
        cookies = c;
    }

    public static String getCookies() {
        return cookies;
    }
}
