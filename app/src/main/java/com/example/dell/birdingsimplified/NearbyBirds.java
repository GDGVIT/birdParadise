package com.example.dell.birdingsimplified;

import android.Manifest;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.birdingsimplified.Adapters.NearbyBirdsRecyclerAdapter;
import com.example.dell.birdingsimplified.Interfaces.LocationBird_API;
import com.example.dell.birdingsimplified.Location.LocationAddress;
import com.example.dell.birdingsimplified.Models.NearbyBirdsModel;
import com.example.dell.birdingsimplified.Models.NearbyBirdsRecyclerModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NearbyBirds extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    public Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    int MY_PERMISSIONS_REQUEST_READ_LOCATION;
    double latitude, longitude;
    String locationAddress = "";

    List<NearbyBirdsRecyclerModel> recyclerList = new ArrayList<>();
    NearbyBirdsRecyclerModel model;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_birds);

        recyclerView = (RecyclerView)findViewById(R.id.nearbyBirdsRecycler);

        if (ContextCompat.checkSelfPermission(NearbyBirds.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(NearbyBirds.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_LOCATION);

        }

        Retrofit retrofit = new Retrofit.Builder().baseUrl(LocationBird_API.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        LocationBird_API birdApi = retrofit.create(LocationBird_API.class);
        Call<List<NearbyBirdsModel>> call = birdApi.getBirds();

        call.enqueue(new Callback<List<NearbyBirdsModel>>() {
            @Override
            public void onResponse(Call<List<NearbyBirdsModel>> call, Response<List<NearbyBirdsModel>> response) {

                List<NearbyBirdsModel> birdsList = response.body();
//                Toast.makeText(getBaseContext(),birdsList.get(0).getName()+"",Toast.LENGTH_LONG).show();

                for(int i=0;i<birdsList.size();i++){

                    model = new NearbyBirdsRecyclerModel();
                    model.setName(birdsList.get(i).getName());
                    model.setColor(birdsList.get(i).getColor());
                    model.setLocation(birdsList.get(i).getState());
                    recyclerList.add(model);
                }

                NearbyBirdsRecyclerAdapter adapter = new NearbyBirdsRecyclerAdapter(recyclerList,NearbyBirds.this);
                RecyclerView.LayoutManager manager = new GridLayoutManager(NearbyBirds.this,1);
                recyclerView.setLayoutManager(manager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(adapter);
                Toast.makeText(getBaseContext(),"Reached", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(Call<List<NearbyBirdsModel>> call, Throwable t) {

                Toast.makeText(getBaseContext(),"Please check your internet connection", Toast.LENGTH_LONG).show();

            }
        });


        final ProgressDialog pdLoading = new ProgressDialog(NearbyBirds.this);
        pdLoading.setMessage("Fetching your address..");
        pdLoading.setCancelable(false);
        pdLoading.show();


        if (checkPlayServices()) {
            buildGoogleApiClient();
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                adress();

                final Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        pdLoading.dismiss();
                        Toast.makeText(getBaseContext(), locationAddress, Toast.LENGTH_LONG).show();

                    }
                }, 3000);

            }
        }, 3000);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        displayLocation();

    }

    @Override
    public void onConnectionSuspended(int i) {

        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    private void displayLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();


        } else {

            Toast.makeText(getBaseContext(),"Not able to get location",Toast.LENGTH_LONG).show();

        }
    }

    public void adress() {
        LocationAddress locationAddress = new LocationAddress();
        locationAddress.getAddressFromLocation(latitude, longitude,
                getApplicationContext(), new GeocoderHandler());
    }


    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
        }
    }

}
