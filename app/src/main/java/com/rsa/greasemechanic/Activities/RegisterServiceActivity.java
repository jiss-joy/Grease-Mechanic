package com.rsa.greasemechanic.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rsa.greasemechanic.LoadingDialog;
import com.rsa.greasemechanic.R;
import com.rsa.greasemechanic.ShowSnackbar;

import java.util.HashMap;
import java.util.Map;

public class RegisterServiceActivity extends AppCompatActivity {

    private TextInputEditText gstIn, email;
    private Button submitBTN;
    private LinearLayout linearLayout;
    private LoadingDialog loadingDialog;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private CollectionReference requestRef;
    private CollectionReference userRef;

    private String status = "Pending";

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
        } else {
            loadingDialog.showLoadingDialog("Submitting request...");
            submitRequest();
        }
    }

    private void submitRequest() {
        Map<String, Object> registrationRequest = new HashMap<>();
        registrationRequest.put("userGSTIN", gstIn.getText().toString());
        registrationRequest.put("userRegistrationStatus", status);
        if (!email.getText().toString().equals("")) {
            registrationRequest.put("userEmail", email.getText().toString());
        }
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
        loadingDialog = new LoadingDialog(this);

        gstIn = (TextInputEditText) findViewById(R.id.register_service_gstin);
        email = (TextInputEditText) findViewById(R.id.register_service_email);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        requestRef = db.collection("Users");
    }
}