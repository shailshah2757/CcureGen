package com.example.ccuregen;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateProfile extends AppCompatActivity {

    private EditText etUpdateName, etUpdateDob, etUpdateMobile;
    private RadioGroup rgUpdateGender;
    private RadioButton rBtnUpdateGenderSelected;
    private String textFullName, textDob, textGender, textMobile;
    private FirebaseAuth authProfile;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeContainer;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable
                (getResources().getColor(R.color.action_bar_blue)));
        getSupportActionBar().setTitle("Update Profile");

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        swipeToRefresh();

        progressBar = findViewById(R.id.progressBar);
        etUpdateName = findViewById(R.id.et_update_profile_name);
        etUpdateDob = findViewById(R.id.et_update_profile_dob);
        etUpdateMobile = findViewById(R.id.et_update_profile_mobile);

        rgUpdateGender = findViewById(R.id.rg_update_profile_gender);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        assert firebaseUser != null;
        showProfile(firebaseUser);

        TextView tvUploadProfilePic = findViewById(R.id.tv_upload_profile_pic);
        tvUploadProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateProfile.this, UploadProfilePic.class);
                startActivity(intent);
                finish();
            }
        });

        TextView tvUpdateProfileEmail = findViewById(R.id.tv_update_profile_email);
        tvUpdateProfileEmail.setOnClickListener(v -> {
            Intent intent = new Intent(UpdateProfile.this, UpdateEmail.class);
            startActivity(intent);
            finish();
        });

        etUpdateDob.setOnClickListener(v -> {
            String[] textSADob = textDob.split("/");

            int day = Integer.parseInt(textSADob[0]);
            int month = Integer.parseInt(textSADob[1]) - 1;
            int year = Integer.parseInt(textSADob[2]);

            DatePickerDialog picker;

            picker = new DatePickerDialog(UpdateProfile.this, R.style.DialogTheme,
                    (view, year1, month1, dayOfMonth) -> etUpdateDob.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1), year, month, day);
            picker.show();
        });

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button btnUpdateProfile = findViewById(R.id.btn_update_profile);
        btnUpdateProfile.setOnClickListener(v -> updateProfile(firebaseUser));
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

    private void updateProfile(FirebaseUser firebaseUser) {
        int selectedGenderId = rgUpdateGender.getCheckedRadioButtonId();
        rBtnUpdateGenderSelected = findViewById(selectedGenderId);

        String mobileRegex = "[6-9]\\d{9}";
        Matcher mobileMatcher;
        Pattern mobilePattern = Pattern.compile(mobileRegex);
        mobileMatcher = mobilePattern.matcher(textMobile);

        if (TextUtils.isEmpty(textFullName)) {
            Toast.makeText(UpdateProfile.this, "Please enter your full name",
                    Toast.LENGTH_LONG).show();
            etUpdateName.setError("Full Name is required");
            etUpdateName.requestFocus();
        } else if (TextUtils.isEmpty(textDob)) {
            Toast.makeText(UpdateProfile.this, "Please enter your date of birth",
                    Toast.LENGTH_LONG).show();
            etUpdateDob.setError("Date of Birth is required");
            etUpdateDob.requestFocus();
        } else if (TextUtils.isEmpty(rBtnUpdateGenderSelected.getText())) {
            Toast.makeText(UpdateProfile.this, "Please select your gender",
                    Toast.LENGTH_LONG).show();
            rBtnUpdateGenderSelected.setError("Gender is required");
            rBtnUpdateGenderSelected.requestFocus();
        } else if (TextUtils.isEmpty(textMobile)) {
            Toast.makeText(UpdateProfile.this, "Please enter your mobile no.",
                    Toast.LENGTH_LONG).show();
            etUpdateMobile.setError("Mobile no. is required");
            etUpdateMobile.requestFocus();
        } else if (textMobile.length() != 10) {
            Toast.makeText(UpdateProfile.this, "Please re-enter your mobile no.",
                    Toast.LENGTH_LONG).show();
            etUpdateMobile.setError("Mobile no. should be 10 digits");
            etUpdateMobile.requestFocus();
        } else if (!mobileMatcher.find()) {
            Toast.makeText(UpdateProfile.this, "Please re-enter your mobile no.",
                    Toast.LENGTH_LONG).show();
            etUpdateMobile.setError("Mobile no. is not valid");
            etUpdateMobile.requestFocus();
        } else {
            textGender = rBtnUpdateGenderSelected.getText().toString();
            textFullName = etUpdateName.getText().toString();
            textDob = etUpdateDob.getText().toString();
            textMobile = etUpdateMobile.getText().toString();

            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textDob, textGender,
                    textMobile);

            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference(
                    "Registered Users");

            String userID = firebaseUser.getUid();

            progressBar.setVisibility(View.VISIBLE);

            referenceProfile.child(userID).setValue(writeUserDetails).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest
                            .Builder().setDisplayName(textFullName).build();
                    firebaseUser.updateProfile(profileUpdates);

                    Toast.makeText(UpdateProfile.this, "Update Successful!",
                            Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(UpdateProfile.this, UserProfile.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                    Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        throw Objects.requireNonNull(task.getException());
                    } catch (Exception e) {
                        Toast.makeText(UpdateProfile.this, e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            });
        }
    }

    private void showProfile(FirebaseUser firebaseUser) {
        String userIDofRegistered = firebaseUser.getUid();

        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference
                ("Registered Users");

        progressBar.setVisibility(View.VISIBLE);
        referenceProfile.child(userIDofRegistered).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails != null) {
                    textFullName = firebaseUser.getDisplayName();
                    textDob = readUserDetails.dob;
                    textGender = readUserDetails.gender;
                    textMobile = readUserDetails.mobile;

                    etUpdateName.setText(textFullName);
                    etUpdateDob.setText(textDob);
                    etUpdateMobile.setText(textMobile);

                    if (textGender.equals("Male")) {
                        rBtnUpdateGenderSelected = findViewById(R.id.radio_male);
                    } else {
                        rBtnUpdateGenderSelected = findViewById(R.id.radio_female);
                    }
                    rBtnUpdateGenderSelected.setChecked(true);
                } else {
                    Toast.makeText(UpdateProfile.this, "Something went wrong!",
                            Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateProfile.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
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
            Intent intent = new Intent(UpdateProfile.this, UpdateProfile.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_update_email) {
            Intent intent = new Intent(UpdateProfile.this, UpdateEmail.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_change_password) {
            Intent intent = new Intent(UpdateProfile.this, ChangePassword.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_delete_profile) {
            Intent intent = new Intent(UpdateProfile.this, DeleteProfile.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UpdateProfile.this, Home.class);
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