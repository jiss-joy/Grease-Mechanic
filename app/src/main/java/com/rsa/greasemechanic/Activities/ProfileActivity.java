package com.rsa.greasemechanic.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rsa.greasemechanic.Authentication.AuthenticationBridgeActivity;
import com.rsa.greasemechanic.LoadingDialog;
import com.rsa.greasemechanic.R;

public class ProfileActivity extends AppCompatActivity {

    private TextInputEditText bName, gstIn, pan, date, pin, email;
    private Button submitBTN, logOutBTN;
    private LinearLayout linearLayout;
    private LoadingDialog loadingDialog;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private CollectionReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        logOutBTN = (Button) findViewById(R.id.log_out_btn);
        mAuth = FirebaseAuth.getInstance();
        logOutBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                finish();
                startActivity(new Intent(ProfileActivity.this, AuthenticationBridgeActivity.class));
            }
        });
    }

}