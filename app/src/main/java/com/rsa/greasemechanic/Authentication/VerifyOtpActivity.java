package com.rsa.greasemechanic.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rsa.greasemechanic.MainActivity;
import com.rsa.greasemechanic.LoadingDialog;
import com.rsa.greasemechanic.R;
import com.rsa.greasemechanic.ShowSnackbar;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class VerifyOtpActivity extends AppCompatActivity {

    public static final long OTP_START_TIME_IN_MILLIS = 60000;

    private TextView otp_sent_tv, mCountDown_tv;
    private EditText otp_et;
    private Button submitBTN, resendBTN;
    private RelativeLayout relativeLayout;
    private CountDownTimer mCountDownTimer;
    private LoadingDialog loadingDialog;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference userRef;

    private String mPhoneNumber;
    private PhoneAuthProvider.ForceResendingToken token;
    private long mTimeLeftInMillis = OTP_START_TIME_IN_MILLIS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        mPhoneNumber = getIntent().getStringExtra("Phone Number");
        initValues();
        sendOTP();

        resendBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetTimer();
                resendOTP();
            }
        });
    }

    private void resendOTP() {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber("+91" + mPhoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signInUser(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(VerifyOtpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        Toast.makeText(VerifyOtpActivity.this, "Too many attempts detected.\n Please try again later", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(VerifyOtpActivity.this, AuthenticationBridgeActivity.class));
                    }

                    @Override
                    public void onCodeSent(@NonNull String sentOTP, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(sentOTP, forceResendingToken);
                        token = forceResendingToken;
                        submitBTN.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                loadingDialog.showLoadingDialog("Authenticating...");
                                String userOTP = otp_et.getText().toString();
                                if (userOTP.isEmpty()) {
                                    loadingDialog.dismissLoadingDialog();
                                    ShowSnackbar.show(VerifyOtpActivity.this, "Please enter the OTP",
                                            relativeLayout, getResources().getColor(R.color.red), getResources().getColor(R.color.white));
                                } else if (userOTP.length() != 6) {
                                    loadingDialog.dismissLoadingDialog();
                                    ShowSnackbar.show(VerifyOtpActivity.this, "Please enter the OTP",
                                            relativeLayout, getResources().getColor(R.color.red), getResources().getColor(R.color.white));
                                } else {
                                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(sentOTP, userOTP);
                                    signInUser(phoneAuthCredential);
                                }
                            }
                        });
                    }
                })
                .setForceResendingToken(token)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void sendOTP() {
        startTimer();
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber("+91" + mPhoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signInUser(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        loadingDialog.dismissLoadingDialog();
                        ShowSnackbar.show(VerifyOtpActivity.this, e.getMessage(),
                                relativeLayout, getResources().getColor(R.color.red), getResources().getColor(R.color.white));
//                        startActivity(new Intent(VerifyOtpActivity.this, PhoneAuthActivity.class));
                    }

                    @Override
                    public void onCodeSent(@NonNull String sentOTP, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(sentOTP, forceResendingToken);
                        token = forceResendingToken;
                        submitBTN.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                loadingDialog.showLoadingDialog("Authenticating...");
                                String userOTP = otp_et.getText().toString();
                                if (userOTP.isEmpty()) {
                                    loadingDialog.dismissLoadingDialog();
                                    ShowSnackbar.show(VerifyOtpActivity.this, "Please enter the OTP",
                                            relativeLayout, getResources().getColor(R.color.red), getResources().getColor(R.color.white));
                                } else if (userOTP.length() != 6) {
                                    loadingDialog.dismissLoadingDialog();
                                    ShowSnackbar.show(VerifyOtpActivity.this, "Please enter the valid OTP",
                                            relativeLayout, getResources().getColor(R.color.red), getResources().getColor(R.color.white));
                                } else {
                                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(sentOTP, userOTP);
                                    signInUser(phoneAuthCredential);
                                }
                            }
                        });
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                resendBTN.setVisibility(View.VISIBLE);
                submitBTN.setVisibility(View.GONE);
                mCountDown_tv.setVisibility(View.GONE);
            }
        }.start();
    }

    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        mCountDown_tv.setText(timeLeftFormatted);
    }

    private void resetTimer() {
        mTimeLeftInMillis = OTP_START_TIME_IN_MILLIS;
        updateCountDownText();
        mCountDown_tv.setVisibility(View.VISIBLE);
        resendBTN.setVisibility(View.GONE);
        submitBTN.setVisibility(View.VISIBLE);
        startTimer();
    }

    private void signInUser(PhoneAuthCredential phoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                                loadingDialog.dismissLoadingDialog();
                                loadingDialog.showLoadingDialog("Please wait while we create your account...");
                                createNewUser();
                            } else {
                                loadingDialog.dismissLoadingDialog();
                                Toast.makeText(VerifyOtpActivity.this, "Welcome", Toast.LENGTH_LONG).show();
                                finish();
                                startActivity(new Intent(VerifyOtpActivity.this, MainActivity.class));
                            }
                        }
                    }
                });
    }

    private void createNewUser() {
        Map<String, Object> user = new HashMap<>();
        user.put("userPhone", mPhoneNumber);
        user.put("userType", "Mechanic");
        user.put("userRegistrationStatus", "");

        userRef.document(mAuth.getCurrentUser().getUid()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
                loadingDialog.dismissLoadingDialog();
                startActivity(new Intent(VerifyOtpActivity.this, MainActivity.class));
            }
        });
    }


    private void initValues() {
        otp_sent_tv = findViewById(R.id.verify_otp_phone_number);
        otp_et = findViewById(R.id.verify_otp_code);
        submitBTN = findViewById(R.id.verify_otp_submit_btn);
        resendBTN = findViewById(R.id.verify_otp_resend_otp_btn);
        resendBTN.setBackgroundColor(getResources().getColor(R.color.primary));
        resendBTN.setTextColor(getResources().getColor(R.color.white));
        resendBTN.setEnabled(false);
        otp_sent_tv.setText("An OTP has been sent to +91-" + mPhoneNumber);
        relativeLayout = findViewById(R.id.verify_otp_relative_layout);
        mCountDown_tv = findViewById(R.id.verify_otp_otp_timer);
        loadingDialog = new LoadingDialog(VerifyOtpActivity.this);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userRef = db.collection("Users");
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}