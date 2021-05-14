package com.rsa.greasemechanic.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.rsa.greasemechanic.LoadingDialog;
import com.rsa.greasemechanic.R;

import java.io.IOException;
import java.util.List;

public class GetLocation extends FragmentActivity implements OnMapReadyCallback {

    private static final int ACCESS_LOCATION_REQUEST_CODE = 1001;

    private ImageView fixedMarker;
    private LoadingDialog loadingDialog;
    private TextView addressArea, address;
    private Button confirmAddressBTN;

    private GoogleMap mMap;
    private Geocoder geocoder;
    private FusedLocationProviderClient client;

    private String locationAddress;
    private Double locationLatitude;
    private Double locationLongitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_location);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.get_location_map);
        mapFragment.getMapAsync(this);

        initValues();

        confirmAddressBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.showLoadingDialog("Saving your address...");
                saveAddress();
            }
        });
    }

    private void saveAddress() {
        Intent intent = new Intent();

        intent.putExtra("Longitude", locationLongitude);
        intent.putExtra("Latitude", locationLatitude);
        intent.putExtra("Address", address.getText().toString());
        intent.putExtra("Address Area", addressArea.getText().toString());

        setResult(RESULT_OK, intent);
        loadingDialog.dismissLoadingDialog();
        finish();
    }

    private void initValues() {
        geocoder = new Geocoder(this);
        client = LocationServices.getFusedLocationProviderClient(this);

        fixedMarker = (ImageView) findViewById(R.id.get_location_fixed_marker);
        address = (TextView) findViewById(R.id.get_location_address);
        addressArea = (TextView) findViewById(R.id.get_location_area);
        confirmAddressBTN = (Button) findViewById(R.id.get_location_confirm_btn);

        loadingDialog = new LoadingDialog(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map_style));


        } catch (Resources.NotFoundException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                try {
                    LatLng latLng = mMap.getCameraPosition().target;
                    List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    locationAddress = addresses.get(0).getLocality();
                    if (!locationAddress.isEmpty()) {
                        addressArea.setText(locationAddress);
                    } else {
                        addressArea.setText("Unknown Locality");
                    }
                    locationAddress = addresses.get(0).getAddressLine(0);
                    address.setText(locationAddress);
                    locationLatitude = latLng.latitude;
                    locationLongitude = latLng.longitude;
                } catch (Exception e) {
                    Log.d("ARRAY INDEX ERROR", e.getMessage());
                }
            }
        });

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                mMap.clear();
                fixedMarker.setVisibility(View.VISIBLE);
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableUserLocation();
            zoomToUserLocation();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, ACCESS_LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, ACCESS_LOCATION_REQUEST_CODE);
            }
        }

    }

    private void zoomToUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Task<Location> locationTask = client.getLastLocation();
        locationTask.addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    LatLng latLng = new LatLng(task.getResult().getLatitude(), task.getResult().getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                    try {
                        List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                        locationAddress = addresses.get(0).getAddressLine(0);
                        address.setText(locationAddress);
                        locationLatitude = latLng.latitude;
                        locationLongitude = latLng.longitude;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ACCESS_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation();
                zoomToUserLocation();
            } else {

            }
        }
    }
}