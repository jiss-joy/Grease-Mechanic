package com.rsa.greasemechanic.Network;

import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.rsa.greasemechanic.R;

public class NoNetworkActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    private Button retryBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_network);

        retryBTN = (Button) findViewById(R.id.no_network_btn);

        retryBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInternetConnection();
            }
        });
    }

    private void checkInternetConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();

        if (isConnected) {
//            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {

        }
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

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
    }
}