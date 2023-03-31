package com.example.ccuregen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Button;

import java.util.Objects;

public class Steganography extends AppCompatActivity {
    //private SwipeRefreshLayout swipeContainer;

    Button btn_encode,btn_decode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steganography);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable
                (getResources().getColor(R.color.action_bar_blue)));
        getSupportActionBar().setTitle("Steganography");

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        btn_decode = findViewById(R.id.decode_button);
        btn_encode = findViewById(R.id.encode_button);

        //swipeToRefresh();

    }

    @Override
    protected void onStart() {
        super.onStart();
        btn_encode.setOnClickListener(v->{
            startActivity(new Intent(this,Encode.class));
        });
        btn_decode.setOnClickListener(v->{
            startActivity(new Intent(this,Decode.class));
        });
    }

    //    private void swipeToRefresh() {
//        swipeContainer = findViewById(R.id.swipe_container);
//
//        swipeContainer.setOnRefreshListener(() -> {
//            startActivity(getIntent());
//            finish();
//            overridePendingTransition(0,0);
//            swipeContainer.setRefreshing(false);
//        });
//
//        swipeContainer.setColorSchemeResources( android.R.color.holo_blue_bright,
//                android.R.color.holo_green_light,
//                android.R.color.holo_orange_light,
//                android.R.color.holo_red_light);
//    }
}