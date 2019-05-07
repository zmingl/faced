package com.example.mei.faced;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class CreateMeetingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meeting);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.hide();
        }

    }
}
