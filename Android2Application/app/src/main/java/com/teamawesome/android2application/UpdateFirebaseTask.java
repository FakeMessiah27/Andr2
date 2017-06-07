package com.teamawesome.android2application;

import android.location.Location;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by Thomas on 06/06/2017.
 */

class UpdateFirebaseTask extends AsyncTask<DataSnapshot, Void, ArrayList<LatLng>> {

    @Override
    protected ArrayList<LatLng> doInBackground(DataSnapshot... params) {
        DataSnapshot snapshot = params[0];



        return null;
    }
}
