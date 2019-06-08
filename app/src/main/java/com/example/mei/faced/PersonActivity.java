package com.example.mei.faced;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PersonActivity extends AppCompatActivity {

    private String meeting_id;
    private RecyclerView recListPerson = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();

        meeting_id = intent.getStringExtra("meeting_id");

        ServiceProvider serviceProvider = ServiceProvider.getInstance(getApplicationContext());
        String url = getResources().getString(R.string.server_host)+"person_list";
        Response.Listener listener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    Log.d(getClass().toString(), "onResponse: " + response);
                    List<PersonInfo> result = new ArrayList<PersonInfo>();
                    if (response.length()==0){
                        Toast.makeText(PersonActivity.this, "参会人数为0", Toast.LENGTH_SHORT).show();
                    }
                    for (int i=0;i<response.length();i++) {
                        JSONObject json = (JSONObject)response.get(i);
                        String peresonName = json.getString("name");
                        String personstatus = json.optString("status");

                        appendPerson(result,peresonName,personstatus);
                        // Log.d(getClass().toString(), "Object: " + o);
                    }
                    PersonAdapter pl = new PersonAdapter(result);
                    recListPerson.setAdapter(pl);

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
                Toast.makeText(PersonActivity.this,"名单列表获取失败",Toast.LENGTH_SHORT).show();
            }

        };
        try {
            JSONObject body = new JSONObject();
            body.put("meeting_id", meeting_id);
            serviceProvider.request(url, body.toString(), listener, errorListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
                        /*
        RecyclerView 的相关设置
         */
        recListPerson = (RecyclerView) findViewById(R.id.recyclerview_person);
        recListPerson.setHasFixedSize(true);
        LinearLayoutManager llmp = new LinearLayoutManager(this);
        llmp.setOrientation(LinearLayoutManager.VERTICAL);
        recListPerson.setLayoutManager(llmp);
    }

    private void appendPerson(List<PersonInfo> list, String pn, String ps) {
        PersonInfo ci = new PersonInfo();
        ci.person_name = PersonInfo.PN_PREFIX + pn;
        ci.person_status = PersonInfo.PS_PREFIX + ps;
        list.add(ci);
    }
}
