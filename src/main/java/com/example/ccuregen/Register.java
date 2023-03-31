package com.example.ccuregen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    private EditText etRegisterFullName, etRegisterEmail, etRegisterDoB, etRegisterMobile,
            etRegisterPassword, etRegisterConfirmPassword;
    private ProgressBar progressBar;
    private RadioGroup radioGroupRegisterGender;
    private RadioButton radioButtonRegisterGenderSelected;
    private DatePickerDialog picker;
    private CheckBox checkBox;
    private static final String TAG = "Register";
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable
                (getResources().getColor(R.color.action_bar_blue)));
        getSupportActionBar().setTitle("Register");

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        swipeToRefresh();

        Toast.makeText(Register.this, "You can register now!", Toast.LENGTH_SHORT).show();

        progressBar = findViewById(R.id.progressBar);
        etRegisterFullName = findViewById(R.id.et_register_full_name);
        etRegisterEmail = findViewById(R.id.et_register_email);
        etRegisterDoB = findViewById(R.id.et_register_dob);
        etRegisterMobile = findViewById(R.id.et_register_mobile);
        etRegisterPassword = findViewById(R.id.et_register_password);
        etRegisterConfirmPassword = findViewById(R.id.et_register_confirm_password);
        checkBox = findViewById(R.id.checkbox_terms_conditions);

        radioGroupRegisterGender = findViewById(R.id.rg_register_gender);
        radioGroupRegisterGender.clearCheck();

        etRegisterDoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                picker = new DatePickerDialog(Register.this, R.style.DialogTheme,
                        new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        etRegisterDoB.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        ImageView ivShowHidePassword = findViewById(R.id.iv_show_hide_password);
        ivShowHidePassword.setImageResource(R.drawable.hide_password);
        ivShowHidePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etRegisterPassword.getTransformationMethod().equals(HideReturnsTransformationMethod
                        .getInstance())) {
                    etRegisterPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    ivShowHidePassword.setImageResource(R.drawable.hide_password);
                } else {
                    etRegisterPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    ivShowHidePassword.setImageResource(R.drawable.show_password);
                }
            }
        });

        ImageView ivShowHideConfirmPassword = findViewById(R.id.iv_show_hide_confirm_password);
        ivShowHideConfirmPassword.setImageResource(R.drawable.hide_password);
        ivShowHideConfirmPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etRegisterConfirmPassword.getTransformationMethod().equals(HideReturnsTransformationMethod
                        .getInstance())) {
                    etRegisterConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    ivShowHideConfirmPassword.setImageResource(R.drawable.hide_password);
                } else {
                    etRegisterConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    ivShowHideConfirmPassword.setImageResource(R.drawable.show_password);
                }
            }
        });

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button btnRegister = findViewById
                (R.id.btn_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedGenderId = radioGroupRegisterGender.getCheckedRadioButtonId();
                radioButtonRegisterGenderSelected = findViewById(selectedGenderId);

                String textFullName = etRegisterFullName.getText().toString();
                String textEmail = etRegisterEmail.getText().toString();
                String textDoB = etRegisterDoB.getText().toString();
                String textMobile = etRegisterMobile.getText().toString();
                String textPassword = etRegisterPassword.getText().toString();
                String textConfirmPassword = etRegisterConfirmPassword.getText().toString();
                String textGender;

                String mobileRegex = "[6-9]\\d{9}";
                Matcher mobileMatcher;
                Pattern mobilePattern = Pattern.compile(mobileRegex);
                mobileMatcher = mobilePattern.matcher(textMobile);

                if (TextUtils.isEmpty(textFullName)) {
                    Toast.makeText(Register.this, "Please enter your full name",
                            Toast.LENGTH_LONG).show();
                    etRegisterFullName.setError("Full Name is required");
                    etRegisterFullName.requestFocus();
                } else if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(Register.this, "Please enter your email",
                            Toast.LENGTH_LONG).show();
                    etRegisterEmail.setError("Email is required");
                    etRegisterEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(Register.this, "Please re-enter your email",
                            Toast.LENGTH_LONG).show();
                    etRegisterEmail.setError("Valid email is required");
                    etRegisterEmail.requestFocus();
                } else if (TextUtils.isEmpty(textDoB)) {
                    Toast.makeText(Register.this, "Please enter your date of birth",
                            Toast.LENGTH_LONG).show();
                    etRegisterDoB.setError("Date of Birth is required");
                    etRegisterDoB.requestFocus();
                } else if (radioGroupRegisterGender.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(Register.this, "Please select your gender",
                            Toast.LENGTH_LONG).show();
                    radioButtonRegisterGenderSelected.setError("Gender is required");
                    radioButtonRegisterGenderSelected.requestFocus();
                } else if (TextUtils.isEmpty(textMobile)) {
                    Toast.makeText(Register.this, "Please enter your mobile no.",
                            Toast.LENGTH_LONG).show();
                    etRegisterMobile.setError("Mobile no. is required");
                    etRegisterMobile.requestFocus();
                } else if (textMobile.length() != 10) {
                    Toast.makeText(Register.this, "Please re-enter your mobile no.",
                            Toast.LENGTH_LONG).show();
                    etRegisterMobile.setError("Mobile no. should be 10 digits");
                    etRegisterMobile.requestFocus();
                } else if (!mobileMatcher.find()) {
                    Toast.makeText(Register.this, "Please re-enter your mobile no.",
                            Toast.LENGTH_LONG).show();
                    etRegisterMobile.setError("Mobile no. is not valid");
                    etRegisterMobile.requestFocus();
                } else if (TextUtils.isEmpty(textPassword)) {
                    Toast.makeText(Register.this, "Please enter your password",
                            Toast.LENGTH_LONG).show();
                } else if (textPassword.length() < 6) {
                    Toast.makeText(Register.this, "Password should be at least 6 digits",
                            Toast.LENGTH_LONG).show();
                    etRegisterPassword.setError("Password too weak");
                    etRegisterPassword.requestFocus();
                } else if (TextUtils.isEmpty(textConfirmPassword)) {
                    Toast.makeText(Register.this, "Please confirm your password",
                            Toast.LENGTH_LONG).show();
                    etRegisterConfirmPassword.setError("Password confirmation is required");
                    etRegisterConfirmPassword.requestFocus();
                } else if (!textPassword.equals(textConfirmPassword)) {
                    Toast.makeText(Register.this, "Please enter same password",
                            Toast.LENGTH_LONG).show();
                    etRegisterConfirmPassword.setError("Password confirmation is required");
                    etRegisterConfirmPassword.requestFocus();

                    etRegisterPassword.clearComposingText();
                    etRegisterConfirmPassword.clearComposingText();
                } else if (!checkBox.isChecked()) {
                    Toast.makeText(Register.this, "Please check the services and policy",
                            Toast.LENGTH_SHORT).show();
                } else {
                    textGender = radioButtonRegisterGenderSelected.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(textFullName, textEmail, textDoB, textGender, textMobile,
                            textPassword);
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

    private void registerUser(String textFullName, String textEmail, String textDoB,
                              String textGender, String textMobile, String textPassword) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(textEmail, textPassword).addOnCompleteListener(
                Register.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser firebaseUser = auth.getCurrentUser();

                            UserProfileChangeRequest profileChangeRequest = new
                                    UserProfileChangeRequest.Builder().setDisplayName(textFullName)
                                    .build();
                            firebaseUser.updateProfile(profileChangeRequest);

                            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(
                                     textDoB, textGender, textMobile);

                            DatabaseReference referenceProfile = FirebaseDatabase.getInstance()
                                    .getReference("Registered Users");

                            referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                firebaseUser.sendEmailVerification();
                                                Toast.makeText(Register.this,
                                                        "User registered successfully. " +
                                                                "Please verify your email",
                                                        Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(Register.this,
                                                        Home.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                                                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                                                Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(Register.this,
                                                        "User registered failed. Please try again",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    });
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                etRegisterPassword.setError("Your password is too weak. " +
                                        "Kindly use a mixture of alphabets, numbers and special characters");
                                etRegisterPassword.requestFocus();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                etRegisterPassword.setError("Your email is invalid or already in use. " +
                                        "Kindly re-enter");
                                etRegisterPassword.requestFocus();
                            } catch (FirebaseAuthUserCollisionException e) {
                                etRegisterPassword.setError("User is already registered with this email. " +
                                        "Use another email");
                                etRegisterPassword.requestFocus();
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                                Toast.makeText(Register.this, e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}