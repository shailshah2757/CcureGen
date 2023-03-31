package com.example.ccuregen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
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

public class UpdateEmail extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private ProgressBar progressBar;
    private TextView tvAuthenticated;
    private String userOldEmail, userNewEmail, userPassword;
    private Button btnUpdateEmail;
    private EditText etNewEmail, etUpdateEmailPassword;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable
                (getResources().getColor(R.color.action_bar_blue)));
        getSupportActionBar().setTitle("Update Email");

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        swipeToRefresh();

        progressBar = findViewById(R.id.progressBar);
        etUpdateEmailPassword = findViewById(R.id.et_update_email_verify_password);
        etNewEmail = findViewById(R.id.et_update_email_new);
        tvAuthenticated = findViewById(R.id.tv_update_email_authenticated);
        btnUpdateEmail = findViewById(R.id.btn_update_email);

        btnUpdateEmail.setEnabled(false);
        etNewEmail.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        userOldEmail = firebaseUser.getEmail();
        TextView tvOldEmail = findViewById(R.id.tv_update_email_old);
        tvOldEmail.setText(userOldEmail);

        if (firebaseUser.equals("")) {
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        } else {
            reAuthenticate(firebaseUser);
        }

        ImageView ivShowHideUpdateProfilePassword = findViewById(R.id.iv_show_hide_update_email_password);
        ivShowHideUpdateProfilePassword.setImageResource(R.drawable.hide_password);
        ivShowHideUpdateProfilePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etUpdateEmailPassword.getTransformationMethod().equals(HideReturnsTransformationMethod
                        .getInstance())) {
                    etUpdateEmailPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    ivShowHideUpdateProfilePassword.setImageResource(R.drawable.hide_password);
                } else {
                    etUpdateEmailPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    ivShowHideUpdateProfilePassword.setImageResource(R.drawable.show_password);
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

    private void reAuthenticate(FirebaseUser firebaseUser) {
        Button btnVerifyUser = findViewById(R.id.btn_authenticate_user);
        btnVerifyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPassword = etUpdateEmailPassword.getText().toString();
                if (TextUtils.isEmpty(userPassword)){
                    Toast.makeText(UpdateEmail.this, "Password is required",
                            Toast.LENGTH_SHORT).show();
                    etUpdateEmailPassword.setError("Please enter your password for authentication");
                    etUpdateEmailPassword.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);

                    AuthCredential credential = EmailAuthProvider.getCredential(userOldEmail,
                            userPassword);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(UpdateEmail.this, "Password has been verified. " +
                                                "You can update email now",
                                        Toast.LENGTH_SHORT).show();
                                tvAuthenticated.setText("You are authenticated. You can update your email now");

                                etNewEmail.setEnabled(true);
                                etUpdateEmailPassword.setEnabled(false);
                                btnVerifyUser.setEnabled(false);
                                btnUpdateEmail.setEnabled(true);

                                btnUpdateEmail.setBackgroundTintList(ContextCompat.getColorStateList(
                                        UpdateEmail.this, R.color.dark_green));

                                btnUpdateEmail.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        userNewEmail = etNewEmail.getText().toString();
                                        if (TextUtils.isEmpty(userNewEmail)) {
                                            Toast.makeText(UpdateEmail.this, "New Email is required",
                                                    Toast.LENGTH_SHORT).show();
                                            etNewEmail.setError("Please enter new email");
                                            etNewEmail.requestFocus();
                                        } else if (!Patterns.EMAIL_ADDRESS.matcher(userNewEmail).matches()) {
                                            Toast.makeText(UpdateEmail.this, "Please enter valid email address",
                                                    Toast.LENGTH_SHORT).show();
                                            etNewEmail.setError("Please provide valid email");
                                            etNewEmail.requestFocus();
                                        } else if (userOldEmail.matches(userNewEmail)) {
                                            Toast.makeText(UpdateEmail.this, "New email cannot be same as old email",
                                                    Toast.LENGTH_SHORT).show();
                                            etNewEmail.setError("Please enter new email");
                                            etNewEmail.requestFocus();
                                        } else {
                                            progressBar.setVisibility(View.VISIBLE);
                                            updateEmail(firebaseUser);
                                        }
                                    }
                                });
                            } else {
                                try {
                                    throw task.getException();
                                } catch (Exception e) {
                                    Toast.makeText(UpdateEmail.this, e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void updateEmail(FirebaseUser firebaseUser) {
        firebaseUser.updateEmail(userNewEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()) {
                    firebaseUser.sendEmailVerification();
                    Toast.makeText(UpdateEmail.this, "Email has been update successfully. Please verify!",
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UpdateEmail.this, UserProfile.class);
                    startActivity(intent);
                    showAlertDialog();
//                    finish();
                } else {
                    try {
                        throw task.getException();
                    } catch (Exception e) {
                        Toast.makeText(UpdateEmail.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateEmail.this);
        builder.setTitle("Email Not Verified");
        builder.setMessage("Please verify your email");

        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        AlertDialog alertDialog = builder.create();

        alertDialog.show();
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
            Intent intent = new Intent(UpdateEmail.this, UpdateProfile.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_update_email) {
            Intent intent = new Intent(UpdateEmail.this, UpdateEmail.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_change_password) {
            Intent intent = new Intent(UpdateEmail.this, ChangePassword.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_delete_profile) {
            Intent intent = new Intent(UpdateEmail.this, DeleteProfile.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UpdateEmail.this, Home.class);
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