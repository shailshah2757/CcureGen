package com.example.ccuregen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class ChangePassword extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private EditText etCurrentPassword, etNewPassword, etConfirmNewPassword;
    private TextView tvAuthenticated;
    private Button btnChangePasswordAuthenticate, btnChangePassword;
    private ProgressBar progressBar;
    private String userCurrentPassword;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable
                (getResources().getColor(R.color.action_bar_blue)));
        getSupportActionBar().setTitle("Change Password");

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        swipeToRefresh();

        etNewPassword = findViewById(R.id.et_change_password_new);
        etCurrentPassword = findViewById(R.id.et_change_password_current);
        etConfirmNewPassword = findViewById(R.id.et_change_password_new_confirm);
        tvAuthenticated = findViewById(R.id.tv_change_password_authenticated);
        progressBar = findViewById(R.id.progressBar);
        btnChangePassword = findViewById(R.id.btn_change_password);
        btnChangePasswordAuthenticate = findViewById(R.id.btn_change_password_authenticate);

        etNewPassword.setEnabled(false);
        etConfirmNewPassword.setEnabled(false);
        btnChangePassword.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser.equals("")) {
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ChangePassword.this, UserProfile.class);
            startActivity(intent);
            finish();
        } else {
            reAuthenticateUser(firebaseUser);
        }

        ImageView ivShowHideCurrentPassword = findViewById(R.id.iv_show_hide_current_password);
        ivShowHideCurrentPassword.setImageResource(R.drawable.hide_password);
        ivShowHideCurrentPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etCurrentPassword.getTransformationMethod().equals(HideReturnsTransformationMethod
                        .getInstance())) {
                    etCurrentPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    ivShowHideCurrentPassword.setImageResource(R.drawable.hide_password);
                } else {
                    etCurrentPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    ivShowHideCurrentPassword.setImageResource(R.drawable.show_password);
                }
            }
        });

        ImageView ivShowHideNewPassword = findViewById(R.id.iv_show_hide_new_password);
        ivShowHideNewPassword.setImageResource(R.drawable.hide_password);
        ivShowHideNewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etNewPassword.getTransformationMethod().equals(HideReturnsTransformationMethod
                        .getInstance())) {
                    etNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    ivShowHideNewPassword.setImageResource(R.drawable.hide_password);
                } else {
                    etNewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    ivShowHideNewPassword.setImageResource(R.drawable.show_password);
                }
            }
        });

        ImageView ivShowHideConfirmNewPassword = findViewById(R.id.iv_show_hide_new_confirm_password);
        ivShowHideConfirmNewPassword.setImageResource(R.drawable.hide_password);
        ivShowHideConfirmNewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etConfirmNewPassword.getTransformationMethod().equals(HideReturnsTransformationMethod
                        .getInstance())) {
                    etConfirmNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    ivShowHideConfirmNewPassword.setImageResource(R.drawable.hide_password);
                } else {
                    etConfirmNewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    ivShowHideConfirmNewPassword.setImageResource(R.drawable.show_password);
                }
            }
        });

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

    private void reAuthenticateUser(FirebaseUser firebaseUser) {
        btnChangePasswordAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userCurrentPassword = etCurrentPassword.getText().toString();
                if (TextUtils.isEmpty(userCurrentPassword)) {
                    Toast.makeText(ChangePassword.this, "Password is required", Toast.LENGTH_SHORT).show();
                    etCurrentPassword.setError("Please enter your current password to authenticate");
                    etCurrentPassword.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);

                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(),
                            userCurrentPassword);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);

                                etCurrentPassword.setEnabled(false);
                                etNewPassword.setEnabled(true);
                                etConfirmNewPassword.setEnabled(true);

                                btnChangePasswordAuthenticate.setEnabled(false);
                                btnChangePassword.setEnabled(true);

                                tvAuthenticated.setText("You are authenticated/verified. " +
                                        "You can change password now!");
                                Toast.makeText(ChangePassword.this, "Password has been verified. Change password now",
                                        Toast.LENGTH_SHORT).show();

                                btnChangePassword.setBackgroundTintList(ContextCompat.getColorStateList(
                                        ChangePassword.this, R.color.dark_green));

                                btnChangePassword.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        changePassword(firebaseUser);
                                    }
                                });
                            } else {
                                try {
                                    throw task.getException();
                                } catch (Exception e) {
                                    Toast.makeText(ChangePassword.this, e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    private void changePassword(FirebaseUser firebaseUser) {
        String userNewPassword = etNewPassword.getText().toString();
        String userConfirmNewPassword = etConfirmNewPassword.getText().toString();

        if (TextUtils.isEmpty(userNewPassword)) {
            Toast.makeText(this, "New Password is required", Toast.LENGTH_SHORT).show();
            etNewPassword.setError("Please enter your new password");
            etNewPassword.requestFocus();
        } else if (TextUtils.isEmpty(userConfirmNewPassword)) {
            Toast.makeText(this, "Please confirm your new password", Toast.LENGTH_SHORT).show();
            etConfirmNewPassword.setError("Please re-enter your new password");
            etConfirmNewPassword.requestFocus();
        } else if (!userNewPassword.matches(userConfirmNewPassword)) {
            Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show();
            etConfirmNewPassword.setError("Please re-enter same password");
            etConfirmNewPassword.requestFocus();
        } else if (userCurrentPassword.matches(userNewPassword)) {
            Toast.makeText(this, "New password cannot be same as old password", Toast.LENGTH_SHORT).show();
            etNewPassword.setError("Please enter a new password");
            etNewPassword.requestFocus();
        } else {
            progressBar.setVisibility(View.VISIBLE);

            firebaseUser.updatePassword(userNewPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ChangePassword.this, "Password has been changed successfully",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ChangePassword.this, UserProfile.class);
                        startActivity(intent);
                        finish();
                    } else {
                        try {
                            throw task.getException();
                        } catch (Exception e) {
                            Toast.makeText(ChangePassword.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_update_profile) {
            Intent intent = new Intent(ChangePassword.this, UpdateProfile.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_update_email) {
            Intent intent = new Intent(ChangePassword.this, UpdateEmail.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_change_password) {
            Intent intent = new Intent(ChangePassword.this, ChangePassword.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_delete_profile) {
            Intent intent = new Intent(ChangePassword.this, DeleteProfile.class);
            startActivity(intent);
        } else if (id == R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ChangePassword.this, Home.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }


}