package com.example.mei.faced;

import android.app.Person;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.example.mei.faced.PersonInfo.PN_PREFIX;
import static com.example.mei.faced.PersonInfo.PS_PREFIX;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.PersonViewHolder>{

    private List<PersonInfo> personList;
    private final Handler handler = new Handler();

    public PersonAdapter(List<PersonInfo> personList) {
        this.personList = personList;
    }

    @Override
    public int getItemCount() {
        return personList.size();
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {
        final PersonInfo pi = personList.get(i);
        personViewHolder.PersonName.setText(pi.person_name);
        personViewHolder.Personstatus.setText(pi.person_status);
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.person_item, viewGroup, false);

        return new PersonViewHolder(itemView);
    }

    public static class PersonViewHolder extends RecyclerView.ViewHolder {
        protected TextView PersonName;
        protected TextView Personstatus;

        public PersonViewHolder(View v) {
            super(v);
            PersonName =  (TextView) v.findViewById(R.id.person_name);
            Personstatus =  (TextView) v.findViewById(R.id.person_status);
        }

    }
}

