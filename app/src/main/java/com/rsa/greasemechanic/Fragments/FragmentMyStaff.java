package com.rsa.greasemechanic.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.rsa.greasemechanic.Activities.AddStaffActivity;
import com.rsa.greasemechanic.Adapters.AdapterStaff;
import com.rsa.greasemechanic.Models.ModelStaff;
import com.rsa.greasemechanic.R;
import com.rsa.greasemechanic.Activities.StaffDetailsActivity;

import java.util.ArrayList;

public class FragmentMyStaff extends Fragment implements AdapterStaff.OnStaffClickListener {

    private FloatingActionButton newStaffBTN;
    private RecyclerView recyclerView;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private CollectionReference staffRef;
    private ArrayList<ModelStaff> staffs = new ArrayList<>();
    private ArrayList<String> staffID = new ArrayList<>();
    private AdapterStaff sAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = (ViewGroup) inflater.inflate(R.layout.fragment_my_staff, container, false);

        initValues(view);

        newStaffBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddStaffActivity.class));
            }
        });

        setUpRecycler();

        return view;
    }

    private void setUpRecycler() {
        staffRef.orderBy("staffExperience", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                staffs.clear();
                staffID.clear();
                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                    String id = document.getId();
                    String image = document.getString("staffImage");
                    String name = document.getString("staffName");
                    String dob = document.getString("staffDOB");
                    String experience = document.getString("staffExperience");

                    ModelStaff staff = new ModelStaff(image, name, dob, experience);
                    staffs.add(staff);
                    staffID.add(id);

                    if (staffs.size() == 0) {
                        recyclerView.setVisibility(View.GONE);
//                        noStaffsLayout.setVisibility(View.VISIBLE);
                    } else {
                        sAdapter = new AdapterStaff(getActivity(), staffs, FragmentMyStaff.this);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setAdapter(sAdapter);
//                        noStaffsLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }

                }
            }
        });
    }

    private void initValues(View view) {
        newStaffBTN = (FloatingActionButton) view.findViewById(R.id.fragment_my_staff_add_staff_btn);
        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_my_staff_recycler);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        staffRef = db.collection("Service Providers").document(mAuth.getCurrentUser().getUid()).collection("Staff");
    }

    @Override
    public void OnStaffClick(int position, View view) {
        startActivity(new Intent(getContext(), StaffDetailsActivity.class).putExtra("STAFF ID", staffID.get(position)));
    }
}
