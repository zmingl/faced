package com.example.mei.faced;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.inputmethodservice.ExtractEditText;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class InfoActivity extends AppCompatActivity {
    private EditText mobile;
    private EditText username;
    private EditText password;
    public static final int TAKE_PHOTO = 1;
    private Uri imageUri;
    private String who;
    private String user;
    private String pwd;
    private String phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.hide();
        }
        initEdits();
        //判断请求会议列表者的身份
        Intent intent = getIntent();
        who = intent.getStringExtra("who");

        Button submit=(Button)findViewById(R.id.takephoto);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取注册所属会议信息
                user = username.getText().toString();
                pwd = password.getText().toString();
                phone = mobile.getText().toString();
                //摄像头
                File outputImage = new File(getExternalCacheDir(),"Face_image_login.jpg");
                imageUri = Camera.getPhotoUri(outputImage,getApplicationContext());

                //发起请求
                if(!TextUtils.isEmpty(user)&&!TextUtils.isEmpty(pwd)&&!TextUtils.isEmpty(phone)){
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, TAKE_PHOTO);
                }
            }
        });
    }
    //初始化输入View
    private void initEdits(){
        username = (EditText) findViewById(R.id.myName);
        password = (EditText)findViewById(R.id.password);
        mobile = (EditText)findViewById(R.id.mobile);
    }
    //将获得的注册照片传到服务端处理url为register，query string为registerPhoto=base64
    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case TAKE_PHOTO:
                if(resultCode == RESULT_OK){
                    try{
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 15, baos);
                        //转换成Base64字符串
                        String base64 = Base64.encodeToString(baos.toByteArray(),Base64.NO_WRAP);
                        ServiceProvider serviceProvider = ServiceProvider.getInstance(getApplicationContext());
                        String url = "http://192.168.1.2:8000/accounts/register";
                        Response.Listener listener = new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                //      "status": "OK"
                                try {
                                    boolean successfulLogin = response.getString("status").equals("OK");
                                    Log.d(this.getClass().toString(), "onResponsebool: " +successfulLogin);
                                    if(successfulLogin){
                                        //如果正确跳转会议列表页
                                        Intent intent = new Intent(InfoActivity.this,ListActivity.class);
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
                                Toast.makeText(InfoActivity.this,"注册失败，请联系管理员",Toast.LENGTH_SHORT).show();
                            }

                        };
                        JSONObject body = new JSONObject();
                        body.put("username",user);
                        body.put("password",pwd);
                        body.put("mobile",phone);
                        body.put("pic",base64);
                        serviceProvider.request(url, body.toString(),listener,errorListener);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }
}
