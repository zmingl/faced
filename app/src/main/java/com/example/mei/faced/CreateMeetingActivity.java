package com.example.mei.faced;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class CreateMeetingActivity extends AppCompatActivity {
    private EditText meetingName;
    private EditText startAt;
    private EditText endAt;
    private Button confirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meeting);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        meetingName = (EditText)findViewById(R.id.meetingNameInput);
        startAt = (EditText)findViewById(R.id.startAtInput);
        endAt = (EditText)findViewById(R.id.endAtInput);
        confirm = (Button) findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取用户信息
                String mn = meetingName.getText().toString();
                String sa = startAt.getText().toString();
                String ea = endAt.getText().toString();
                if(!TextUtils.isEmpty(mn)&&!TextUtils.isEmpty(sa)&&!TextUtils.isEmpty(ea)){
                    ServiceProvider serviceProvider = ServiceProvider.getInstance(getApplicationContext());
                    String url = getResources().getString(R.string.server_host)+"create";
                    Response.Listener listener = new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                int meetingId = response.getInt("meeting_id");
                                String who = "admin";
                                Intent intent = new Intent(CreateMeetingActivity.this,ListActivity.class);
                                intent.putExtra("who",who);
                                startActivity(intent);
                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    Response.ErrorListener errorListener = new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(this.getClass().toString(), "onResponse: ");
                            Toast.makeText(CreateMeetingActivity.this,"会议创建失败",Toast.LENGTH_SHORT).show();
                        }

                    };
                    try {
                        JSONObject body = new JSONObject();
                        body.put("name", mn );
                        body.put("start", sa);
                        body.put("end", ea);
                        serviceProvider.request(url, body.toString(), listener, errorListener);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    @Override
    protected void onStop(){
        super.onStop();
    }
}
