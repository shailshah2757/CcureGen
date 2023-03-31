package com.example.ccuregen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import java.util.Objects;

public class PasswordCracking extends AppCompatActivity {
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_cracking);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable
                (getResources().getColor(R.color.action_bar_blue)));
        getSupportActionBar().setTitle("Password Cracking");

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        swipeToRefresh();

    }

    private void swipeToRefresh() {
        swipeContainer = findViewById(R.id.swipe_container);

        swipeContainer.setOnRefreshListener(() -> {
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);
            swipeContainer.setRefreshing(false);
        });

        swipeContainer.setColorSchemeResources( android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }
}