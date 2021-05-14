package com.rsa.greasemechanic.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.ornach.nobobutton.NoboButton;
import com.rsa.greasemechanic.LoadingDialog;
import com.rsa.greasemechanic.R;

public class StaffDetailsActivity extends AppCompatActivity {

    private CircularImageView proPic;
    private TextView name, dob, exp;
    private NoboButton viewAadhaarBTN;
    private FloatingActionButton editStaffBTN;
    private LoadingDialog loadingDialog;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private CollectionReference staffRef;

    private String staffID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_details);

        staffID = getIntent().getStringExtra("STAFF ID");

        intiValues();

        loadingDialog.showLoadingDialog("Loading data...");
        loadValues();

        viewAadhaarBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StaffDetailsActivity.this, ViewAadhaarActivity.class).putExtra("ID", staffID));
            }
        });

        editStaffBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(StaffDetailsActivity.this, "Under Development", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadValues() {
        staffRef.document(staffID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Glide.with(StaffDetailsActivity.this).load(task.getResult().getString("staffImage")).into(proPic);
                    name.setText(task.getResult().getString("staffName"));
                    dob.setText(task.getResult().getString("staffDOB"));
                    exp.setText(task.getResult().getString("staffExperience") + " Years of Experience");
                    loadingDialog.dismissLoadingDialog();
                }
            }
        });
    }

    private void intiValues() {
        proPic = (CircularImageView) findViewById(R.id.staff_details_pro_pic);
        name = (TextView) findViewById(R.id.staff_details_name);
        dob = (TextView) findViewById(R.id.staff_details_dob);
        exp = (TextView) findViewById(R.id.staff_details_work_exp);
        viewAadhaarBTN = (NoboButton) findViewById(R.id.staff_details_view_aadhaar_btn);
        editStaffBTN = (FloatingActionButton) findViewById(R.id.staff_details_edit_staff_btn);
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