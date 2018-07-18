package edu.admu.cs295s37.watchout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

@EActivity(R.layout.activity_reporter_map)
public class ReporterMapActivity extends FragmentActivity
        implements GoogleMap.OnMyLocationClickListener
            , OnMapReadyCallback {

    Toast toast;
    Realm realm;
    @Extra
    String uid;
    @ViewById
    Button bProfile;
    @ViewById
    Button bReport;
    @ViewById
    Button bSignOut;
    Context c;


    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int DEFAULT_ZOOM = 15;
    private static final String TAG = ReporterMapActivity.class.getSimpleName();
    //Katipunan
    private final LatLng mDefaultLocation = new LatLng(14.641209, 121.074599);

    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;

    private GoogleMap mMap;
    GeoDataClient mGeoDataClient;
    PlaceDetectionClient mPlaceDetectionClient;

    FusedLocationProviderClient mFusedLocationProviderClient;
    LocationRequest mLocationRequest;
    LocationCallback mLocationCallback;
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    Marker selectedPlace;


    @AfterViews
    public void init(){
        realm = MyRealm.getRealm();

        c = this;

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.reporter_map);
        mapFragment.getMapAsync(this);

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...

                    LatLng newLoc = new LatLng(location.getLatitude(), location.getLongitude());
                    //mMap.addMarker(new MarkerOptions().position(newLoc).title("I am here"));
                    mLastKnownLocation = location;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLoc,DEFAULT_ZOOM));


                }
            }
        };

        try {
            if (mFusedLocationProviderClient == null) {
                mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
                mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(UPDATE_INTERVAL);
                mLocationRequest.setFastestInterval(FATEST_INTERVAL);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
            }
            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);

        }
        catch(SecurityException e)
        {
            e.printStackTrace();
        }


        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName());
                if(selectedPlace != null) {
                    selectedPlace.remove();
                    selectedPlace = null;
                }
                selectedPlace = mMap.addMarker(new MarkerOptions()
                        .position(place.getLatLng())
                        .title(place.getName().toString()));

                selectedPlace.setTag(place);
                selectedPlace.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

                mMap.animateCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
            }

            @Override
            public void onError(Status status) {
                toast = Toast.makeText(c
                        , "The provided credentials are invalid or the user does not exist."
                        , Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL
                                , 0, 0);
                toast.show();
                Log.i(TAG, "An error occurred: " + status);

            }
        });


    }

    @Click(R.id.bProfile)
    public void bProfile() {
        ProfileActivity_.intent(this).uid(uid).start();
    }

    @Click(R.id.bReport)
    public void bReport() {
        NewReportActivity_.intent(this)
                .uid(uid)
                .latLng(new LatLng(mLastKnownLocation.getLatitude()
                        , mLastKnownLocation.getLongitude()))
                .start();
    }

    @Click(R.id.bSignOut)
    public void bSignOut() {
        MyRealm.logoutUser();
        onBackPressed();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        realm.close();
    }

    /**********************
     * MAP-RELATED STUFFS *
     * ********************/

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Katipunan and move the camera
//        LatLng katipunan = new LatLng(14.641209, 121.074599);
//        mMap.addMarker(new MarkerOptions().position(katipunan).title("Marker in Katipunan"));
//        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(katipunan,18));
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(katipunan,18));

        // Do other setup activities here too, as described elsewhere in this tutorial.

        getLocationPermission();
        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        AddMarkers();
        mMap.setOnMyLocationClickListener(this);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                try{
                    Place thePlace = (Place) marker.getTag();
                    NewReportActivity_.intent(c)
                            .uid(uid)
                            .latLng(thePlace.getLatLng())
                            .start();
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Marker Tag", "Not a Place tag");
                }

                try{
                    HazardReport hr = (HazardReport) marker.getTag();
                    ReportDetailActivity_.intent(c)
                            .hrid(hr.getHrid())
                            .latLng(new LatLng(hr.getLatitude(),hr.getLongitude()))
                            .uid(uid)
                            .start();
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Market Tag", "Not a HazardReport tag");
                }
                return false;
            }
        });


    }

    private void AddMarkers() {
        RealmResults<HazardReport> hazardReports = realm.where(HazardReport.class)
            .sort("dateReported", Sort.DESCENDING)
            .findAllAsync();

        hazardReports.addChangeListener(new RealmChangeListener<RealmResults<HazardReport>>() {
            Marker m;
            @Override
            public void onChange(RealmResults<HazardReport> hazardReports) {
                mMap.clear();
                for(HazardReport hr:hazardReports){
                    if(hr.getResponder() == null){
                        m = mMap.addMarker(new MarkerOptions()
                            .title(hr.getTitle())
                            .position(new LatLng(hr.getLatitude(),hr.getLongitude())));
                        m.setTag((HazardReport) hr);
                        m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                    } else {
                        m = mMap.addMarker(new MarkerOptions()
                                .title(hr.getTitle())
                                .position(new LatLng(hr.getLatitude(),hr.getLongitude())));
                        m.setTag((HazardReport) hr);
                        m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        final Location loc = mLastKnownLocation;

        AlertDialog addReport;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            addReport = new AlertDialog.Builder(this
                    , android.R.style.Theme_Material_Dialog_Alert).create();
        } else {
            addReport = new AlertDialog.Builder(this).create();
        }

        addReport.setTitle("Add Report");
        addReport.setMessage("Do you want to add a hazard report on this location?");
        addReport.setButton(AlertDialog.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NewReportActivity_.intent(c)
                        .uid(uid)
                        .latLng(new LatLng(loc.getLatitude(),loc.getLongitude()))
                        .start();
            }
        });
        addReport.setButton(AlertDialog.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        addReport.setIcon(R.drawable.ic_add_location_black_24dp);
        addReport.show();
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {

                LocationCallback mLocationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if (locationResult == null) {
                            return;
                        }
                        for (Location location : locationResult.getLocations()) {
                            // Update UI with location data
                            // ...

                            LatLng newLoc = new LatLng(location.getLatitude(), location.getLongitude());
                            //mMap.addMarker(new MarkerOptions().position(newLoc).title("I am here"));
                            mLastKnownLocation = location;
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(newLoc));


                        }
                    }
                };
//                Task locationResult = mFusedLocationProviderClient.getLastLocation();
//                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
//                    @Override
//                    public void onComplete(@NonNull Task task) {
//                        if (task.isSuccessful()) {
//                            // Set the map's camera position to the current location of the device.
//                            mLastKnownLocation = (Location) task.getResult();
//                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
//                                    new LatLng(mLastKnownLocation.getLatitude(),
//                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
//                        } else {
//                            Log.d(TAG, "Current location is null. Using defaults.");
//                            Log.e(TAG, "Exception: %s", task.getException());
//                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
//                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
//                        }
//                    }
//                });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
