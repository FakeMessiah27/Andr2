package com.teamawesome.android2application;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    // Google maps variables
    private static final int MAP_PERMISSION = 001;
    private GoogleMap map;
    private GoogleApiClient googleApiClient;
    private Marker marker;
    private FusedLocationProviderApi fusedLocationProviderApi;
    private LocationRequest locationRequest;

    // Firebase variables

    // Reference to the root of the database
    DatabaseReference RootRef;

    DatabaseReference latitudeRef;
    DatabaseReference longitudeRef;
    DatabaseReference locationsRef;

    // Authentication variables
    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        RootRef = FirebaseDatabase.getInstance().getReference();
        latitudeRef = RootRef.child("locations").child(currentUser.getUid()).child("latitude");
        longitudeRef = RootRef.child("locations").child(currentUser.getUid()).child("longitude");
        locationsRef = RootRef.child("locations");

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationProviderApi = LocationServices.FusedLocationApi;
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();

        locationsRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    return;
                }

                if (dataSnapshot.hasChildren()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            map.clear();
                            List<LatLng> allOtherLocations = new ArrayList<LatLng>();
                            LatLng currentPosition = null;

                            for (DataSnapshot user : dataSnapshot.getChildren()) {
                                if (user.getKey().toString().equals(currentUser.getUid().toString())) {
                                    Marker ownPosition = map.addMarker(new MarkerOptions()
                                            .position(new LatLng(user.child("latitude").getValue(Double.class),
                                                    user.child("longitude").getValue(Double.class))).title("Your location"));
                                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(ownPosition.getPosition(), 15));

                                    currentPosition = new LatLng(user.child("latitude").getValue(Double.class), user.child("longitude").getValue(Double.class));
                                }
                                else {
                                    map.addMarker(new MarkerOptions().position(new LatLng(user.child("latitude")
                                            .getValue(Double.class), user.child("longitude").getValue(Double.class))));

                                    allOtherLocations.add(new LatLng(user.child("latitude").getValue(Double.class), user.child("longitude").getValue(Double.class)));
                                }

                                for (LatLng otherLocation : allOtherLocations)
                                {
                                    // Calculate distance and stuff

                                    // If distance closer or something, do stuff, play sound, whatever.
                                }
                            }

                            // Do stuff with locations.
                        }
                    }).run();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        super.onStart();
    }

    @Override
    protected  void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MAP_PERMISSION);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(final Location location) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    latitudeRef.setValue(location.getLatitude());
                    longitudeRef.setValue(location.getLongitude());
                }
            }).start();
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MAP_PERMISSION);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu); //your file name
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.MenuProfile:
                intent = new Intent(this, ProfileActivity.class);
                intent.putExtra("uid", currentUser.getUid());
                startActivity(intent);
                return true;
            case R.id.MenuLogOut:
                auth.signOut();
                intent = new Intent(this, LogInActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
