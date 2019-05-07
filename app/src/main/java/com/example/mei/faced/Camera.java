package com.example.mei.faced;


import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


public class Camera {
    public static Uri getPhotoUri(File file, Context context){
        Uri imageUri;
        try{
            if (file.exists()){
                file.delete();
            }
            file.createNewFile();

        }catch (IOException e){
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >=24){
            imageUri = FileProvider.getUriForFile(context,
                    "com.example.mei.faced.fileprovider",file);
        }else{
            imageUri= Uri.fromFile(file);
        }
        return imageUri;
    }
}
