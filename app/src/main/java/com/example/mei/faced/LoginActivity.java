package com.example.mei.faced;


import android.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.example.mei.faced.ServiceProvider;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {


    private EditText username;
    private EditText password;
    private Button Loginbt;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.hide();
        }
        initViews();
        Loginbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取用户信息
                String un = username.getText().toString();
                String pw = password.getText().toString();
                if(!TextUtils.isEmpty(un)&&!TextUtils.isEmpty(pw)){
                    ServiceProvider serviceProvider = ServiceProvider.getInstance(getApplicationContext());
                    String url = "http://192.168.1.5:8000/accounts?account="+un+"&password="+pw;
                    Response.Listener listener = new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(this.getClass().toString(), "onResponse: " + response.toString());
                        }
                    };

                    Response.ErrorListener errorListener = new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(this.getClass().toString(), "onResponse: " + new String(error.networkResponse.data));
                        }

                    };
                    serviceProvider.request(url, null,listener,errorListener);
                    Log.d(this.getClass().toString(), "onClick: " + url);
                }
            }
        });
    }
    private void initViews(){
        Loginbt = (Button)findViewById(R.id.login);
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
    }
}
