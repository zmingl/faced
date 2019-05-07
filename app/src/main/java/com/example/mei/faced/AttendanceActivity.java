package com.example.mei.faced;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

public class AttendanceActivity extends AppCompatActivity {
    public static final int TAKE_PHOTO = 1;
    private Uri imageUri;
    //private String path;
//    private ImageView picture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.hide();
        }
        Button registerbt=(Button)findViewById(R.id.register);
        Button loginbt=(Button)findViewById(R.id.login);
//点击注册跳转到注册的页面
        registerbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AttendanceActivity.this,InfoActivity.class);
                String who = "att";
                intent.putExtra("who",who);
                startActivity(intent);
            }
        });
//点击登录调用相机抓拍存储照片
        loginbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File outputImage = new File(getExternalCacheDir(),"Face_image_login.jpg");
                //path = outputImage.getPath();
                imageUri = Camera.getPhotoUri(outputImage,getApplicationContext());
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHOTO);
            }
        });
    }
//将获得的登录照片传到服务端处理url为login
    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case TAKE_PHOTO:
                if(resultCode == RESULT_OK){
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 15, baos);
//                        picture.setImageBitmap(bitmap);
//                        String path = imageUri.toString();
                        //Log.d("photo_path", path);
                        //转换成Base64字符串
                        String base64 = Base64.encodeToString(baos.toByteArray(),Base64.NO_WRAP);
                        ServiceProvider serviceProvider = ServiceProvider.getInstance(getApplicationContext());
                        String url = "http://192.168.1.2:8000/accounts/att_login";
                        Response.Listener listener = new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                /**{"status": "OK"}*/
                                try {
                                    boolean successfulLogin = response.getString("status").equals("OK");
                                    Log.d(this.getClass().toString(), "onResponsebool: " + successfulLogin);
                                    if (successfulLogin) {
                                        //如果正确跳转会议列表页
                                        String who = "att";
                                        Intent intent = new Intent(AttendanceActivity.this, ListActivity.class);
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
                                Log.d(this.getClass().toString(), "onResponse: " + error.networkResponse);
                                Toast.makeText(AttendanceActivity.this, "照片上传失败,请再试一次", Toast.LENGTH_SHORT).show();
                            }

                        };
                        String body = "{\"loginPhoto\":\"" + base64 + "\"}";
                        serviceProvider.request(url, body,listener,errorListener);

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
