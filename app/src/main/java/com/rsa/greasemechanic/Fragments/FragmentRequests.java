package com.rsa.greasemechanic.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.rsa.greasemechanic.Activities.RegisterServiceActivity;
import com.rsa.greasemechanic.R;

import static android.view.View.GONE;

public class FragmentRequests extends Fragment {

    private Button registerBTN, registerAgainBTN;
    private CardView registerReminder, registrationPending, registrationDeclined;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private CollectionReference userRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_requests, container, false);

        initValues(view);

        registerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), RegisterServiceActivity.class));
            }
        });

        registerAgainBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), RegisterServiceActivity.class));
            }
        });

        setUpListener();

        return view;
    }

    private void setUpListener() {
        userRef.document(currentUser.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String status = value.getString("userRegistrationStatus");
                switch (status) {
                    case "":
                        registerReminder.setVisibility(View.VISIBLE);
                        registrationPending.setVisibility(View.GONE);
                        registrationDeclined.setVisibility(View.GONE);
                        break;
                    case "Verified":
                        registerReminder.setVisibility(GONE);
                        registrationPending.setVisibility(View.GONE);
                        registrationDeclined.setVisibility(View.GONE);
                        break;
                    case "Pending":
                        registrationPending.setVisibility(View.VISIBLE);
                        registrationDeclined.setVisibility(View.GONE);
                        registerReminder.setVisibility(GONE);
                        break;
                    case "Declined":
                        registrationDeclined.setVisibility(View.VISIBLE);
                        registrationPending.setVisibility(View.GONE);
                        registerReminder.setVisibility(GONE);
                        break;
                }
            }
        });
    }

    private void initValues(View view) {
        registerBTN = (Button) view.findViewById(R.id.fragment_requests_register_btn);
        registerAgainBTN = (Button) view.findViewById(R.id.fragment_requests_register_again_btn);
        registerReminder = (CardView) view.findViewById(R.id.fragment_requests_register_reminder);
        registrationPending = (CardView) view.findViewById(R.id.fragment_requests_register_pending);
        registrationDeclined = (CardView) view.findViewById(R.id.fragment_requests_register_declined);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userRef = db.collection("Users");
    }
}
