package com.example.mei.faced;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private String url;
    private RecyclerView recList = null;
    public static ListActivity instance = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //判断请求会议列表者的身份
        Intent intent = getIntent();
        final String who = intent.getStringExtra("who");

        //"+" button
        FloatingActionButton addMeeting = (FloatingActionButton) findViewById(R.id.addMeeting);
        addMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (who.equals("att")){
                    showInputDialog();
                }else{
                    ////
                    ///
                    ////
                    /////创建会议
                    ///
                    ///
                    Intent intent = new Intent(ListActivity.this,ListActivity.class);
                    intent.putExtra("who",who);
                    startActivity(intent);
                }
            }
            private void showInputDialog() {
                /*@setView 装入一个EditView
                 */
                final EditText editText = new EditText(ListActivity.this);
                AlertDialog.Builder inputDialog =
                        new AlertDialog.Builder(ListActivity.this);
                inputDialog.setTitle("请输入您获得的会议ID").setView(editText);
                inputDialog.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ServiceProvider serviceProvider = ServiceProvider.getInstance(getApplicationContext());
                                String url = "http://192.168.1.2:8000/accounts/addusers";
                                Response.Listener listener = new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        //"status": "OK"
                                        try {
                                            boolean successfulLogin = response.getString("status").equals("OK");
                                            Log.d(this.getClass().toString(), "onResponsebool: " + successfulLogin);
                                            if (successfulLogin) {
                                                //如果正确
                                                finish();
                                                startActivity(getIntent());
                                                Toast.makeText(ListActivity.this, "恭喜你，成功加入"+editText.getText().toString()+"号会议", Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(ListActivity.this, "加入会议失败", Toast.LENGTH_SHORT).show();
                                    }

                                };
                                String body = "{\"meeting_id\":" + editText.getText() + "}";
                                serviceProvider.request(url, body,listener,errorListener);
                                Toast.makeText(ListActivity.this, editText.getText().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            }
        });


        //获取会议列表的网络请求
        ServiceProvider serviceProvider = ServiceProvider.getInstance(getApplicationContext());
        if (who.equals("att")){
            url = "http://192.168.1.2:8000/accounts/att_meeting_list";
        }
        else{
            url = "http://192.168.1.2:8000/accounts/create_meeting_list";
        }
        Response.Listener listener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    Log.d(getClass().toString(), "onResponse: " + response);
                    List<MeetingInfo> result = new ArrayList<MeetingInfo>();
                    if (response.length()==0){
                        Toast.makeText(ListActivity.this, "您暂时尚未参加会议", Toast.LENGTH_SHORT).show();
                    }
                    for (int i=0;i<response.length();i++) {
                        JSONObject json = (JSONObject)response.get(i);
                        String meetingId = json.getInt("id") + "";
                        String meetingName = json.getString("name");
                        String startAt = json.getString("start_at");
                        String endAt = json.getString("end_at");
                        String status = json.optString("status");
                        String founderName= json.getString("founder_name");
                        String hc = json.getInt("hc") + "";
                        String total = json.getInt("total") + "";
                        //转换从数据库获取的date的格式
                        LocalDate startTime = LocalDate.parse(startAt, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                        LocalDate endTime = LocalDate.parse(endAt, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                        LocalDate now = LocalDate.now();
                            if (startTime.isAfter(now)){
                                status = "未开始";
                            }else if (now.isAfter(endTime)){
                                    status = "已结束";
                            }else if (who.equals("att") && status.equals("未签到")) {
                                //更新数据库签到状态
                                status = "点击此处签到";
                            } else if (who.equals("att") && status.equals("已签到")) {
                                //更新数据库签到状态
                                status = "已签到";
                            } else {
                                status = "进行中";
                            }
                        appendMeeting(result,meetingName,meetingId,founderName,startAt,endAt,status,hc,total);
                       // Log.d(getClass().toString(), "Object: " + o);
                    }
                    MeetingAdapter ca = new MeetingAdapter(result);
                    recList.setAdapter(ca);

//                    createList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d(this.getClass().toString(), "onResponse: " + response.toString());
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(this.getClass().toString(), "onResponse: ");
                Toast.makeText(ListActivity.this,"会议列表获取失败",Toast.LENGTH_SHORT).show();
            }

        };
        serviceProvider.request(url, null,listener,errorListener);

        /*
        RecyclerView 的相关设置
         */
        recList = (RecyclerView) findViewById(R.id.recyclerview);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);



    }



    private void appendMeeting(List<MeetingInfo> list,String mn,String mi,String fn,String sa,String ea,String st,String hc,String total) {
        MeetingInfo ci = new MeetingInfo();
        ci.meetingName = MeetingInfo.MTN_PREFIX + mn;
        ci.status = MeetingInfo.ST_PREFIX + st;
        ci.meetingId = mi;
        ci.founderName = MeetingInfo.FN_PREFIX + fn;
        ci.startAt = MeetingInfo.SA_PREFIX + sa;
        ci.endAt = MeetingInfo.EA_PREFIX + ea;
        ci.hc = MeetingInfo.HC_PREFIX + hc + "/" + total;
        list.add(ci);
    }
}

