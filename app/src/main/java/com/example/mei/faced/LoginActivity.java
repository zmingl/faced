package com.example.mei.faced;

import android.content.Intent;
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
import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {


    private EditText username;
    private EditText password;
    private Button Loginbt;
    private Button registerbt;
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
                    String url = "http://192.168.1.2:8000/accounts/admin_login?mobile="+un+"&password="+pw;
                    Response.Listener listener = new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            /**"status": "OK"*/
                            try {
                                boolean successfulLogin = response.getString("status").equals("OK");
                                Log.d(this.getClass().toString(), "onResponsebool: " +successfulLogin);
                                if(successfulLogin){
                                    //如果正确跳转会议列表页
                                    String who = "admin";
                                    Intent intent = new Intent(LoginActivity.this,ListActivity.class);
                                    intent.putExtra("who",who);
                                    startActivity(intent);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.d(this.getClass().toString(), "onResponse: " + response.toString());
                        }
                    };

                    Response.ErrorListener errorListener = new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(this.getClass().toString(), "onResponse: ");
                            Toast.makeText(LoginActivity.this,"用户名或密码错误",Toast.LENGTH_SHORT).show();
                        }

                    };
                    serviceProvider.request(url, null,listener,errorListener);
                    Log.d(this.getClass().toString(), "onClick: " + url);
                }
            }
        });

        registerbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String who = "admin";
                Intent intent = new Intent(LoginActivity.this,InfoActivity.class);
                intent.putExtra("who",who);
                startActivity(intent);
            }
        });
    }
    private void initViews(){
        Loginbt = (Button)findViewById(R.id.login);
        registerbt = (Button)findViewById(R.id.register);
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
    }
}
