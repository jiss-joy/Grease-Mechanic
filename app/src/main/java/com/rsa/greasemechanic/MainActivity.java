package com.rsa.greasemechanic;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.rsa.greasemechanic.Activities.ProfileActivity;
import com.rsa.greasemechanic.Activities.RegisterServiceActivity;
import com.rsa.greasemechanic.Activities.StaffsActivity;
import com.rsa.greasemechanic.Authentication.AuthenticationBridgeActivity;
import com.rsa.greasemechanic.Network.ConnectivityReceiver;
import com.rsa.greasemechanic.Network.MyApp;
import com.rsa.greasemechanic.Network.NoNetworkActivity;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    public static final int REQUEST_CHECK_SETTING = 1001;

    private ImageButton menuBTN, notificationBTN;
    private Toolbar toolbar;
    private Button registerBTN, registerAgainBTN;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private CardView registerReminder, registrationPending, registrationDeclined;
    private TextView uid, gstin, number;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private CollectionReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_main);

        initValues();
        setSupportActionBar(toolbar);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.side_nav_services:
//                        startActivity(new Intent(MainActivity.this, MyAppointmentsActivity.class));
                        break;
                    case R.id.side_nav_staffs:
                        startActivity(new Intent(MainActivity.this, StaffsActivity.class));
                        break;
                    case R.id.side_nav_profile:
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                        break;
                    case R.id.side_nav_contact_us:
//                        startActivity(new Intent(MainActivity.this, ContactUsActivity.class));
                        break;
                    case R.id.side_nav_privacy_policy:
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/document/d/e/2PACX-1vQR5E0-H9OD1N0Hp9UxhAcC0OUXMJ6HSI2DJMH9qCVPcaUF6uQwJ-AWV0HqSKe4-MDmTyIBQXrGl2De/pub")));
                        break;
                    case R.id.side_nav_terms_and_conditions:
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/document/d/e/2PACX-1vSFtKDl9mE_hMMfbK_9Ge6CWlO8inkLlP_W-ZeMmv1owuSqAa-vF5IV4MJIdZ_0zuhB-QbyB8aYH9JS/pub")));
                        break;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        menuBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
//
//        notificationBTN.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                startActivity(new Intent(MainActivity.this, NotificationsActivity.class));
//            }
//        });

        registerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterServiceActivity.class));
            }
        });

        registerAgainBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterServiceActivity.class));
            }
        });
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


    private void initValues() {
        menuBTN = (ImageButton) findViewById(R.id.mechanic_main_menu_btn);
        notificationBTN = (ImageButton) findViewById(R.id.mechanic_main_notification_btn);
        toolbar = (Toolbar) findViewById(R.id.mechanic_main_toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.side_nav);
        registerBTN = (Button) findViewById(R.id.mechanic_main_register_btn);
        registerAgainBTN = (Button) findViewById(R.id.mechanic_main_register_again_btn);
        registerReminder = (CardView) findViewById(R.id.mechanic_main_register_reminder);
        registrationPending = (CardView) findViewById(R.id.mechanic_main_register_pending);
        registrationDeclined = (CardView) findViewById(R.id.mechanic_main_register_declined);

        //Database
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userRef = db.collection("Users");
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser == null) {
            finish();
            startActivity(new Intent(MainActivity.this, AuthenticationBridgeActivity.class));
        } else {
//            requestGPS();
            View view = navigationView.getHeaderView(0);
            loadSideNavData(view);
            setUpListener();
        }
    }

    private void loadSideNavData(View view) {
        uid = (TextView) view.findViewById(R.id.nav_header_uid);
        gstin = (TextView) view.findViewById(R.id.nav_header_gstin);
        number = (TextView) view.findViewById(R.id.nav_header_number);

        db.collection("Users").document(currentUser.getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        uid.setText(value.getId());
                        gstin.setText(value.getString("userGSTIN"));
                        number.setText(value.getString("userPhone"));
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        ConnectivityReceiver connectivityReceiver = new ConnectivityReceiver();
        registerReceiver(connectivityReceiver, intentFilter);

        MyApp.getInstance().setConnectivityListener(this);
    }

    private void exit() {
        this.finishAffinity();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            exit();
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            startActivity(new Intent(this, NoNetworkActivity.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHECK_SETTING) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    break;
                case Activity.RESULT_CANCELED:
            }
        }
    }
}