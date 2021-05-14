package com.rsa.greasemechanic.Activities;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rsa.greasemechanic.LoadingDialog;
import com.rsa.greasemechanic.R;

public class ViewAadhaarActivity extends AppCompatActivity {

    private ImageView aadhaarImage;
    private LoadingDialog loadingDialog;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private CollectionReference staffRef;

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_aadhaar);

        id = getIntent().getStringExtra("ID");

        initValues();

        loadingDialog.showLoadingDialog("Loading Aadhaar...");
        loadAadhaar();
    }


    private void loadAadhaar() {
        staffRef.document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    String image = task.getResult().getString("staffAadhaar");
                    Glide.with(ViewAadhaarActivity.this).load(image).into(aadhaarImage);
                    loadingDialog.dismissLoadingDialog();
                }
            }
        });
    }

    private void initValues() {
        aadhaarImage = (ImageView) findViewById(R.id.view_aadhaar_image);
        loadingDialog = new LoadingDialog(this);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        staffRef = db.collection("Service Providers").document(mAuth.getCurrentUser().getUid()).collection("Staff");
    }


    @Override
    public void onBackPressed() {
        finish();
    }
}