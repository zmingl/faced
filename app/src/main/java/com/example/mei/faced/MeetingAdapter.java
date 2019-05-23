package com.example.mei.faced;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.cienet.android.R;
//import com.nostra13.universalimageloader.core.ImageLoader;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import static com.example.mei.faced.MeetingInfo.MTI_PREFIX;

public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.MeetingViewHolder> {

    private List<MeetingInfo> meetingList;
    private final Handler handler = new Handler();

    public MeetingAdapter(List<MeetingInfo> meetingList) {
        this.meetingList = meetingList;
    }

    @Override
    public int getItemCount() {
        return meetingList.size();
    }

    @Override
    public void onBindViewHolder(MeetingViewHolder meetingViewHolder, int i) {
        final MeetingInfo ci = meetingList.get(i);
        meetingViewHolder.meetingName.setText(ci.meetingName);
        meetingViewHolder.meetingId.setText(MTI_PREFIX + ci.meetingId);
        meetingViewHolder.founderName.setText(ci.founderName);
        meetingViewHolder.status.setText(ci.status);
        meetingViewHolder.startAt.setText(ci.startAt);
        meetingViewHolder.endAt.setText(ci.endAt);
        meetingViewHolder.hc.setText(ci.hc);

        if (meetingViewHolder.status.getText().equals("状态：点击此处签到")){
            meetingViewHolder.status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = ListActivity.instance.getApplicationContext();
                ServiceProvider serviceProvider = ServiceProvider.getInstance(context);
                String url = context.getResources().getString(R.string.server_host)+"update_user_meeting";
                Response.Listener listener = new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        /** { "status": "OK"  } */
                        try {
                            boolean successfulLogin = response.getString("status").equals("OK");
                            Log.d(this.getClass().toString(), "onResponsebool: " + successfulLogin);
                            if (successfulLogin) {
                                //如果正确
                                Toast.makeText(ListActivity.instance, "会议状修改成功", Toast.LENGTH_SHORT).show();
                                Intent intent = ListActivity.instance.getIntent();
                                Context context = ListActivity.instance.getApplicationContext();
                                ListActivity.instance.finish();
                                context.startActivity(intent);
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
                        Toast.makeText(ListActivity.instance, "会议状态修改失败，请联系管理员", Toast.LENGTH_SHORT).show();
                    }

                };

                try {
                    JSONObject body = new JSONObject();
                    body.put("status", "已签到");
                    body.put("meeting_id", Integer.parseInt(ci.meetingId));
                    serviceProvider.request(url, body.toString(), listener, errorListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                }
            });
        }
    }

    @Override
    public MeetingViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.list_item, viewGroup, false);

        return new MeetingViewHolder(itemView);
    }

    public static class MeetingViewHolder extends RecyclerView.ViewHolder {
        protected TextView meetingName;
        protected TextView meetingId;
        protected TextView founderName;
        protected Button status;
        protected TextView startAt;
        protected TextView endAt;
        protected Button hc;

        public MeetingViewHolder(View v) {
            super(v);
            meetingName =  (TextView) v.findViewById(R.id.meetingName);
            meetingId =  (TextView) v.findViewById(R.id.meetingId);
            founderName = (TextView)  v.findViewById(R.id.founderName);
            status = (Button)  v.findViewById(R.id.status);
            startAt = (TextView)  v.findViewById(R.id.startAt);
            endAt = (TextView) v.findViewById(R.id.endAt);
            hc = (Button) v.findViewById(R.id.hc);
        }

    }
}

