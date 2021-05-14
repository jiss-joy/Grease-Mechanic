package com.rsa.greasemechanic.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.ornach.nobobutton.NoboButton;
import com.rsa.greasemechanic.LoadingDialog;
import com.rsa.greasemechanic.R;
import com.rsa.greasemechanic.ShowSnackbar;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

public class AddStaffActivity extends AppCompatActivity {

    public static final int PERMISSIONS_REQUEST_CODE = 1;
    public int IMAGE_UPLOAD_CODE = 1;

    private RelativeLayout relativeLayout;
    private CircularImageView staffPicture;
    private TextInputEditText name, dob, workExp;
    private NoboButton uploadAadhaarBTN, submitBTN;
    private LottieAnimationView checkAnimation;
    private DatePickerDialog.OnDateSetListener mOnDateSetListener;
    private View dobView;
    private LoadingDialog loadingDialog;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private CollectionReference staffRef;

    private Uri profilePicURI, aadhaarImgURI;
    private boolean proPicFlag = false, aadhaarPicFlag = false;
    private String mDob, mYear, mMonth, mDay;
    private int yearIndex = 1999, monthIndex = 1, dayIndex = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_staff);

        setToolbarListeners();
        initValues();

        staffPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IMAGE_UPLOAD_CODE = 1;
                getPermissions();
            }
        });

        dobView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(
                        AddStaffActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mOnDateSetListener,
                        yearIndex, monthIndex - 1, dayIndex);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mOnDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                mYear = String.valueOf(year);
                mMonth = String.valueOf(month + 1);
                mDay = String.valueOf(day);
                yearIndex = year;
                monthIndex = month + 1;
                dayIndex = day;
                mDob = mDay + "/" + mMonth + "/" + mYear;
                dob.setText(mDob);
            }
        };

        uploadAadhaarBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IMAGE_UPLOAD_CODE = 2;
                getPermissions();
            }
        });

        submitBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyDetails();
            }
        });
    }

    private void verifyDetails() {
        if (!proPicFlag) {
            ShowSnackbar.show(AddStaffActivity.this, "Please add the staff's image.",
                    relativeLayout, getResources().getColor(R.color.red), getResources().getColor(R.color.white));
        } else if (name.getText().toString().isEmpty() || dob.getText().toString().isEmpty() || workExp.getText().toString().isEmpty()) {
            ShowSnackbar.show(AddStaffActivity.this, "Required fields cannot be empty.",
                    relativeLayout, getResources().getColor(R.color.red), getResources().getColor(R.color.white));
        } else if (!aadhaarPicFlag) {
            ShowSnackbar.show(AddStaffActivity.this, "Please add the staff's Aadhaar.",
                    relativeLayout, getResources().getColor(R.color.red), getResources().getColor(R.color.white));
        } else {
            loadingDialog.showLoadingDialog("Uploading Staff Data...");
            uploadStaff();
        }
    }

    private void uploadStaff() {
        mStorageRef = FirebaseStorage.getInstance()
                .getReference()
                .child("Staff Pictures")
                .child(mAuth.getCurrentUser().getUid())
                .child(name.getText().toString());
        final StorageReference filepath1 = mStorageRef.child(name.getText().toString() + " Image");
        final StorageReference filepath2 = mStorageRef.child(name.getText().toString() + " Aadhaar");

        filepath1.putFile(profilePicURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Get a URL to the uploaded content
                if (taskSnapshot.getMetadata() != null) {
                    taskSnapshot.getStorage()
                            .getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUrl1 = uri.toString();

                                    filepath2.putFile(aadhaarImgURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            if (taskSnapshot.getMetadata() != null) {
                                                taskSnapshot.getStorage()
                                                        .getDownloadUrl()
                                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri) {
                                                                final String downloadUrl2 = uri.toString();

                                                                Map<String, Object> staff = new HashMap<>();
                                                                Log.d("TAG", "onSuccess: " + downloadUrl1);
                                                                Log.d("TAG", "onSuccess: " + downloadUrl2);
                                                                staff.put("staffImage", downloadUrl1);
                                                                staff.put("staffName", name.getText().toString());
                                                                staff.put("staffDOB", dob.getText().toString());
                                                                staff.put("staffExperience", workExp.getText().toString());
                                                                staff.put("staffAadhaar", downloadUrl2);

                                                                staffRef.add(staff).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                        if (task.isSuccessful()) {
                                                                            loadingDialog.dismissLoadingDialog();
                                                                            ShowSnackbar.show(AddStaffActivity.this, "Staff Added Successfully.",
                                                                                    relativeLayout, getResources().getColor(R.color.green), getResources().getColor(R.color.white));
                                                                            finish();
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        ShowSnackbar.show(AddStaffActivity.this, "Something went wrong! Try again later.",
                                                                relativeLayout, getResources().getColor(R.color.red), getResources().getColor(R.color.white));
                                                        Log.d("TAG", "onFailure: " + e.getMessage());
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }

                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            ShowSnackbar.show(AddStaffActivity.this, "Something went wrong! Try again later.",
                                    relativeLayout, getResources().getColor(R.color.red), getResources().getColor(R.color.white));
                            Log.d("TAG", "onFailure: " + e.getMessage());
                        }
                    });
                }
            }
        });
    }


    private void getPermissions() {
        if (ContextCompat.checkSelfPermission(AddStaffActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(AddStaffActivity.this, Manifest.permission.CAMERA)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(AddStaffActivity.this);
                builder.setTitle("Please grant this Permission to continue.");
                builder.setMessage("Camera");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(AddStaffActivity.this,
                                new String[]{
                                        Manifest.permission.CAMERA,
                                },
                                PERMISSIONS_REQUEST_CODE
                        );
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.create();
                builder.show();
            } else {
                ActivityCompat.requestPermissions(AddStaffActivity.this,
                        new String[]{
                                Manifest.permission.CAMERA
                        },
                        PERMISSIONS_REQUEST_CODE
                );
            }

        } else {
            //Permissions already granted part.
            CropImage.startPickImageActivity(AddStaffActivity.this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                loadingDialog.showLoadingDialog("Uploading Image...");
                Uri resultUri = result.getUri();
                if (IMAGE_UPLOAD_CODE == 1) {
                    profilePicURI = resultUri;
                    Glide.with(this).load(profilePicURI).circleCrop().fitCenter().into(staffPicture);
                    Log.d("TAG", "onActivityResult: " + profilePicURI);
                    proPicFlag = true;

                } else if (IMAGE_UPLOAD_CODE == 2) {
                    aadhaarImgURI = resultUri;
                    Log.d("TAG", "onActivityResult: " + aadhaarImgURI);
                    checkAnimation.playAnimation();
                    aadhaarPicFlag = true;
                }
                loadingDialog.dismissLoadingDialog();
                ShowSnackbar.show(AddStaffActivity.this, "Image upload successful.",
                        relativeLayout, getResources().getColor(R.color.green), getResources().getColor(R.color.white));

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "Could not upload.\nPlease try again later.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (PERMISSIONS_REQUEST_CODE == requestCode) {
            if ((grantResults.length > 0) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions Granted", Toast.LENGTH_SHORT).show();
                CropImage.startPickImageActivity(AddStaffActivity.this);
            } else {
                //Permissions Denied part.
            }
        }
    }


    private void setToolbarListeners() {
        ImageButton toolbar_backBTN;

        toolbar_backBTN = findViewById(R.id.toolbar_back_btn);
        toolbar_backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initValues() {
        relativeLayout = (RelativeLayout) findViewById(R.id.add_staff_relative_layout);
        staffPicture = (CircularImageView) findViewById(R.id.add_staff_picture);
        dob = (TextInputEditText) findViewById(R.id.add_staff_dob);
        name = (TextInputEditText) findViewById(R.id.add_staff_staff_name);
        workExp = (TextInputEditText) findViewById(R.id.add_staff_work_ex);
        dobView = (View) findViewById(R.id.add_staff_dob_view);
        uploadAadhaarBTN = (NoboButton) findViewById(R.id.add_staff_upload_aadhaar);
        submitBTN = (NoboButton) findViewById(R.id.add_staff_submit_btn);
        checkAnimation = (LottieAnimationView) findViewById(R.id.add_staff_aadhaar_indicator);
        loadingDialog = new LoadingDialog(this);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        staffRef = db.collection("Service Providers").document(mAuth.getCurrentUser().getUid()).collection("Staff");
    }
}