package com.example.ccuregen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class SnortRules extends AppCompatActivity {
    Spinner sp_ip;

    Button getip,getfromip;

    EditText responcetext;

    private static final String IP ="172.16.101.31";

    private static String JSON_URL = null;

    ArrayList<String> list;
    ArrayAdapter<String> adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snort_rules);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable
                (getResources().getColor(R.color.action_bar_blue)));
        getSupportActionBar().setTitle("Snort Rules");

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        list = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,list);

        responcetext = findViewById(R.id.ET_RESPONSE);

        sp_ip = findViewById(R.id.SP_IP_ADDRESS);
        getfromip = findViewById(R.id.BTN_GET_FROM_IP);
        getip = findViewById(R.id.BTN_GET_IP);

        getip.setOnClickListener(v->{
//            for(int i=1;i<=10;i++){
//                list.add(String.valueOf(i));
//            }
//            sp_ip.setAdapter(adapter);
            LoadData();
        });

        getfromip.setOnClickListener(v->{
            //responcetext.setText("http://"+IP+":3000/getfiles?directorypath="+sp_ip.getSelectedItem().toString());
            String urlip="http://"+IP+":3000/getfiles?directorypath="+sp_ip.getSelectedItem().toString();

            StringRequest str = new StringRequest(Request.Method.GET, urlip, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jobj = new JSONObject(response);
                        JSONArray dataarray = jobj.getJSONArray("content");
                        responcetext.setText(dataarray.get(0).toString());
                        //Log.d("-------------------------->",dataarray.get(0).toString());

                    } catch (Exception e) {
                        Toast.makeText(SnortRules.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(SnortRules.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            RequestQueue rq = Volley.newRequestQueue(this);
            rq.add(str);
        });

    }
//physcal no mal tem nathi
    private void LoadData() {
        list.clear();
        JSON_URL ="http://"+IP+":3000/getlistofips";
        StringRequest str = new StringRequest(Request.Method.GET, JSON_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jobj = new JSONObject(response);
                    JSONArray dataarray = jobj.getJSONArray("folders");
                    for(int i=0;i< dataarray.length();i++){
                        list.add(String.valueOf(dataarray.get(i)));
                    }
                    sp_ip.setAdapter(adapter);
                    Toast.makeText(SnortRules.this, dataarray.get(0).toString(), Toast.LENGTH_SHORT).show();
                    Log.d("-------------------------->",dataarray.get(0).toString());

                } catch (Exception e) {
                    Toast.makeText(SnortRules.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SnortRules.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue rq = Volley.newRequestQueue(this);
        rq.add(str);
    }

}