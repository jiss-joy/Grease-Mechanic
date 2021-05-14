package com.rsa.greasemechanic.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ornach.nobobutton.NoboButton;
import com.rsa.greasemechanic.LoadingDialog;
import com.rsa.greasemechanic.R;
import com.rsa.greasemechanic.ShowSnackbar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RegisterServiceActivity extends AppCompatActivity {

    public static final int LOCATION_RESULT_CODE = 1001;

    private TextInputLayout locationLayout;
    private TextInputEditText gstIn, email;
    private NoboButton location;
    private Button submitBTN;
    private LinearLayout linearLayout;
    private LoadingDialog loadingDialog;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private CollectionReference requestRef;
    private CollectionReference userRef;

    private String locationAddress;
    private String locationAddressArea;
    private Double locationLatitude;
    private Double locationLongitude;
    private boolean locationFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_service);

        intiValues();

        submitBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.showLoadingDialog("Verifying...");
                verifyDetails();
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(RegisterServiceActivity.this, GetLocation.class), LOCATION_RESULT_CODE);
            }
        });
    }

    private void verifyDetails() {
        if (gstIn.getText().toString().isEmpty()) {
            loadingDialog.dismissLoadingDialog();
            ShowSnackbar.show(RegisterServiceActivity.this, "Required fields cannot be empty.",
                    linearLayout, getResources().getColor(R.color.red), getResources().getColor(R.color.white));
        } else if (gstIn.length() < 15 || gstIn.length() > 15) {
            loadingDialog.dismissLoadingDialog();
            ShowSnackbar.show(RegisterServiceActivity.this, "GST Number has to be 15 digits.",
                    linearLayout, getResources().getColor(R.color.red), getResources().getColor(R.color.white));
        } else if (!locationFlag) {
            loadingDialog.dismissLoadingDialog();
            ShowSnackbar.show(RegisterServiceActivity.this, "Location cannot be empty.",
                    linearLayout, getResources().getColor(R.color.red), getResources().getColor(R.color.white));
        } else {
            loadingDialog.showLoadingDialog("Submitting request...");
            submitRequest();
        }
    }

    private void submitRequest() {
        Map<String, Object> registrationRequest = new HashMap<>();

        registrationRequest.put("userGSTIN", gstIn.getText().toString());
        registrationRequest.put("userRegistrationStatus", "Pending");
        registrationRequest.put("userLongitude", locationLongitude);
        registrationRequest.put("userLatitude", locationLatitude);
        registrationRequest.put("userAddress", locationAddress);
        registrationRequest.put("userAddressArea", locationAddressArea);
        if (!email.getText().toString().isEmpty()) {
            registrationRequest.put("userEmail", email.getText().toString());
        }


        //Write to FireStore Database
        requestRef.document(mAuth.getCurrentUser().getUid())
                .update(registrationRequest)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            loadingDialog.dismissLoadingDialog();
                            finish();
                        }
                    }
                });
    }

    private void intiValues() {
        submitBTN = (Button) findViewById(R.id.register_service_submit_btn);
        linearLayout = (LinearLayout) findViewById(R.id.register_service_layout);
//        locationLayout = (TextInputLayout) findViewById(R.id.register_service_layout_2);
        loadingDialog = new LoadingDialog(this);

        gstIn = (TextInputEditText) findViewById(R.id.register_service_gstin);
        location = (NoboButton) findViewById(R.id.register_service_location);
        email = (TextInputEditText) findViewById(R.id.register_service_email);

        //Database
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        requestRef = db.collection("Users");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOCATION_RESULT_CODE && resultCode == RESULT_OK && data != null) {
            Log.d("TAG", "onActivityResult: Great Back here!!!");
            locationLongitude = data.getDoubleExtra("Longitude", 0.00);
            locationLatitude = data.getDoubleExtra("Latitude", 0.00);
            locationAddress = data.getStringExtra("Address");
            locationAddressArea = data.getStringExtra("Address Area");
            location.setText(locationAddress);
            locationFlag = true;
        }

    }
}