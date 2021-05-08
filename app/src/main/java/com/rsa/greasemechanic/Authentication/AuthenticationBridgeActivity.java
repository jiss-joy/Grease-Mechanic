package com.rsa.greasemechanic.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.rsa.greasemechanic.R;

public class AuthenticationBridgeActivity extends AppCompatActivity {

    private TextInputEditText phoneNumber_et;
    private Button submitButton;

    private FirebaseFirestore db;
    private CollectionReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication_bridge);

        initValues();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = phoneNumber_et.getText().toString();
                validateNumber(phoneNumber);

            }
        });
    }

    private void validateNumber(String phoneNumber) {
        userRef.whereEqualTo("userPhoneNumber", phoneNumber)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.getResult().size() == 0) {
                    startActivity(new Intent(AuthenticationBridgeActivity.this, VerifyOtpActivity.class)
                            .putExtra("Phone Number", phoneNumber));
                } else {
                    if (task.getResult().getDocuments().get(0).getString("userType").equals("Client")) {
                        Toast.makeText(AuthenticationBridgeActivity.this, "Please login with the Grease Monkey App.", Toast.LENGTH_SHORT).show();
                    } else {
                        startActivity(new Intent(AuthenticationBridgeActivity.this, VerifyOtpActivity.class)
                                .putExtra("Phone Number", phoneNumber));
                    }
                }
            }
        });
    }

    private void initValues() {
        phoneNumber_et = (TextInputEditText) findViewById(R.id.auth_bridge_phone_number);
        submitButton = (Button) findViewById(R.id.auth_bridge_submit_btn);

        db = FirebaseFirestore.getInstance();
        userRef = db.collection("Users");
    }
}