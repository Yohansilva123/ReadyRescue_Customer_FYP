package com.example.readyrescuecus;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.readyrescuecus.servicefunctions.ServiceDetails;
import com.example.readyrescuecus.servicefunctions.ValidateServices;
import com.example.readyrescuecus.utils.CalculateRatings;
import com.example.readyrescuecus.utils.RandomGenerator;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    Dialog jobDialog, pointsDialog;

    private GoogleMap mMap;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    private RadioButton mRadioBtn;
    private RadioGroup mSeverityGroup;
    private Marker mMechanicMarker;

    private FusedLocationProviderClient mFusedLocationClient;

    private Button mRequest, mCancel, mJobDone, mAddFav, mDone, mServices;
    private TextView mMechanicName, mMechanicNumber, mfinalMechanicName, mPrice, mRating, mAvailablePoints;
    private EditText mPointsValue;
    public LatLng mPickupLocation;
    private RelativeLayout mLayout;

    public static String userId, customerPresent, requestCancelled, userName, favMechanic, customerNumber, jobPrice, mechanicRating, jobEnd;
    public String points = "0";
    private int radius = 1;
    private Boolean mechanicFound = false;
    public static String mechanicFoundID = "";
    public static Boolean requestSent = false;
    public static String requestSentIds = "";
    private Boolean requestAccepted = false;
    private Boolean jobCompleted = false;

    public static ArrayList<String> arrayList = new ArrayList<>();
    public static ArrayList<String> arrayIds = new ArrayList<>();

    public static ServiceDetails serviceDetails = new ServiceDetails();
    public static DataSnapshot services, jobState;

    RandomGenerator random = new RandomGenerator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setNavigation();

        getDBSnapshot();

        setCustomerRequest();
    }

    private void setNavigation() {
 //        Initialize variables
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

//        Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

//        Item Selected Listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.navigation_home:
                        startActivity(new Intent(getApplicationContext(), CustomerServiceActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navigation_favourite:
                        startActivity(new Intent(getApplicationContext(), CustomerFavoritesActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navigation_game:
                        startActivity(new Intent(getApplicationContext(), CustomerGameActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navigation_profile:
                        startActivity(new Intent(getApplicationContext(), CustomerProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

    public void getDBSnapshot(){
        DatabaseReference dBRef = FirebaseDatabase.getInstance().getReference();

        dBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                serviceDetails.getServiceDetails(dataSnapshot.child("Users"));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void setCustomerRequest() {
        //        Initialize dialog box
        jobDialog = new Dialog(this);

        mRequest = findViewById(R.id.confirm_request);
        mRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRequest.getText().toString().equalsIgnoreCase("Cancel Request")){
                    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference availableRef = FirebaseDatabase.getInstance().getReference("MechanicAvailable");
                    GeoFire geoFire = new GeoFire(availableRef);
                    geoFire.removeLocation(userID, new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) { }
                    });
                    Intent intent = new Intent(CustomerMapsActivity.this, CustomerServiceActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }else {
                    userId = FirebaseAuth.getInstance().getUid();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
                    GeoFire geoFire = new GeoFire(ref);
                    geoFire.setLocation(userId, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), new GeoFire.CompletionListener(){
                        @Override
                        public void onComplete(String key, DatabaseError error) {
                        }
                    });
                    mPickupLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(mPickupLocation).title("Assist Here"));
                    mRequest.setText("Cancel Request");
                    favMechanic = CustomerFavoritesActivity.mechanicID;

                    getDBSnapshot();

                    serviceDetailsPopUp();

                    if (favMechanic == null){
                        getClosestMechanic();
                    }

                    else {
                        try {
                            getFavMechanic(favMechanic);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private void getFavMechanic(String key) throws InterruptedException {

            if (ValidateServices.validateMechanicService(key)&&ValidateServices.favMechanicWorking(key)){
                mechanicFound = true;
                mechanicFoundID = key;

                if (!requestSent) {
                    DatabaseReference mechanicRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Mechanics").child(mechanicFoundID);
                    HashMap map = new HashMap();
                    services = serviceDetails.dataSnapshot;
                    map.put("CustomerID", userId);
                    map.put("CustomerName", services.child("Customers").child(userId).child("Name").getValue().toString());
                    map.put("CustomerPhone", services.child("Customers").child(userId).child("Phone").getValue().toString());
                    mechanicRef.updateChildren(map);
                    mRequest.setText("Cancel Request");
                    requestSent = true;
                }

                DatabaseReference dBRef = FirebaseDatabase.getInstance().getReference();
                dBRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (Boolean.parseBoolean(dataSnapshot.child("Users").child("Mechanics").child(mechanicFoundID).child("CustomerAccepted").getValue().toString())) {
                            requestAccepted = true;
                            getMechanicLocation();
                            setJobDetails();
                            cancelRequest();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {    }
                });
            }else {
                Toast.makeText(CustomerMapsActivity.this,"Your favorite mechanic cannot be reached at the moment", Toast.LENGTH_LONG).show();
                CustomerFavoritesActivity.mechanicID = null;
                Intent intent = new Intent(CustomerMapsActivity.this, CustomerServiceActivity.class);
                startActivity(intent);
                finish();
                return;
            }
    }

    private void getClosestMechanic() {
        getDBSnapshot();
        DatabaseReference mechanicLocation = FirebaseDatabase.getInstance().getReference().child("MechanicAvailable");
        GeoFire geoFire = new GeoFire(mechanicLocation);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mPickupLocation.latitude, mPickupLocation.longitude), radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!mechanicFound&&ValidateServices.validateMechanicService(key)&&ValidateServices.mechanicWorking(key)) {
                    mechanicFound = true;
                    mechanicFoundID = key;

                    if (!requestSent||!(requestSentIds.contains(key))) {
                        DatabaseReference mechanicRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Mechanics").child(mechanicFoundID);
                        HashMap map = new HashMap();
                        services = serviceDetails.dataSnapshot;
                        customerNumber = services.child("Customers").child(userId).child("Phone").getValue().toString();
                        map.put("CustomerID", userId);
                        map.put("CustomerName", services.child("Customers").child(userId).child("Name").getValue().toString());
                        map.put("CustomerPhone", services.child("Customers").child(userId).child("Phone").getValue().toString());
                        mechanicRef.updateChildren(map);
                        requestSent = true;
                    }
                    getDBSnapshot();
                    services = serviceDetails.dataSnapshot;
                    if (Boolean.parseBoolean(services.child("Mechanics").child(mechanicFoundID).child("CustomerAccepted").getValue().toString())) {
                        requestAccepted = true;
                        getMechanicLocation();
                        setJobDetails();
                        cancelRequest();
                    } else {
                        mechanicFound = false;
                        getClosestMechanic();
                    }
                }
            }
            @Override
            public void onKeyExited(String key) {  }
            @Override
            public void onKeyMoved(String key, GeoLocation location) {  }
            @Override
            public void onGeoQueryReady() {
                if (!mechanicFound){
                    radius++;
                    getClosestMechanic();
                }
            }
            @Override
            public void onGeoQueryError(DatabaseError error) {  }
        });
    }

    private void serviceDetailsPopUp() {
        pointsDialog = new Dialog(this);
        pointsDialog.setContentView(R.layout.activity_points_details);
        mDone = pointsDialog.findViewById(R.id.done);
        mServices = findViewById(R.id.set_points);
        mPointsValue = pointsDialog.findViewById(R.id.enter_points);
        mAvailablePoints = pointsDialog.findViewById(R.id.available_points);

        mServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference jobDetails = FirebaseDatabase.getInstance().getReference().child("Users").child("Mechanics").child(mechanicFoundID).child("JobDetails");
                final String availablePoints;
                services = serviceDetails.dataSnapshot;
                availablePoints = services.child("Customers").child(userId).child("Points").getValue().toString();
                mAvailablePoints.setText(availablePoints);
                pointsDialog.show();
                mDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        points = mPointsValue.getText().toString();
                        if ((Integer.parseInt(points)<=200)&&(Integer.parseInt(points)<=Integer.parseInt(mAvailablePoints.getText().toString()))){
                            pointsDialog.dismiss();
                            Toast.makeText(CustomerMapsActivity.this,"Points will be redeemed", Toast.LENGTH_LONG).show();
                            int updatedPoints = Integer.parseInt(availablePoints)-Integer.parseInt(points);
                            DatabaseReference dBRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userId).child("Points");
                            dBRef.setValue(updatedPoints);
                            jobDetails.child("jobPoints").setValue(points);
                        }
                        else {
                            points = "0";
                            Toast.makeText(CustomerMapsActivity.this,"The maximum points per job is 200", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });
    }

    public void getMechanicLocation(){

        mCancel = findViewById(R.id.cancel_job);
        mLayout = findViewById(R.id.mechanic_details);
        mMechanicName = findViewById(R.id.customer_text);
        mMechanicNumber = findViewById(R.id.Mechanic_Number);

        DatabaseReference mechanicLocationRef = FirebaseDatabase.getInstance().getReference().child("MechanicAvailable").child(mechanicFoundID).child("l");
        mechanicLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLog = 0;

                    locationLat = Double.parseDouble(map.get(0).toString());
                    locationLog = Double.parseDouble(map.get(1).toString());

                    LatLng mechanicLatLng = new LatLng(locationLat, locationLog);
                    if (mMechanicMarker != null)
                        mMechanicMarker.remove();

                    mMechanicMarker = mMap.addMarker(new MarkerOptions().position(mechanicLatLng).title("The Mechanic"));
                    mLayout.setVisibility(View.VISIBLE);
                    mMechanicName.setText(services.child("Mechanics").child(mechanicFoundID).child("Name").getValue().toString());
                    mMechanicNumber.setText(services.child("Mechanics").child(mechanicFoundID).child("Phone").getValue().toString());
                    getMechanicRating();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        removeRequest();
    }

    private void getMechanicRating() {
        mRating = findViewById(R.id.rating);
        String ratingType = "no Rating";
        Map<String, Object> map = (Map <String, Object>) services.child("Mechanics").child(mechanicFoundID).child("CustomerRatings").getValue();
        if (map==null){
            mRating.setText(" 0/3 No Ratings");
        }
        else{
            for (int i=0; i<map.size(); i++){
                Object firstKey = map.keySet().toArray()[i];
                Object valueForFirstKey = map.get(firstKey);
                arrayList.add(valueForFirstKey.toString());
            }
            mechanicRating = CalculateRatings.getRatings(arrayList);
            if (Double.parseDouble(mechanicRating)>=2.4&&Double.parseDouble(mechanicRating)<=3)
                ratingType = "Good";
            if (Double.parseDouble(mechanicRating)>=1.8&& Double.parseDouble(mechanicRating)<=2.3)
                ratingType = "Neutral";
            if (Double.parseDouble(mechanicRating)>=1&&Double.parseDouble(mechanicRating)<=1.7)
                ratingType = "Bad";
            mRating.setText(" " + mechanicRating + "/3 " + ratingType);
        }
    }

    private void removeRequest(){
        DatabaseReference requestCancel = FirebaseDatabase.getInstance().getReference().child("Users").child("Mechanics").child(mechanicFoundID);
        requestCancel.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("CustomerID")==null&&!Boolean.parseBoolean(map.get("CustomerAccepted").toString())){
                        mLayout.setVisibility(View.INVISIBLE);
                        mRequest.setText("Confirm Request");
                        mMap.clear();
                        requestSent = false;
                        mechanicFound = false;
                        requestAccepted = false;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    public void cancelRequest(){
//        Once cancel request button is clicked the db data of the customer from the mechanic is removed
        final DatabaseReference serviceConfirmRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Mechanics").child(mechanicFoundID);

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serviceConfirmRef.child("CustomerID").removeValue();
                serviceConfirmRef.child("CustomerName").removeValue();
                serviceConfirmRef.child("CustomerPhone").removeValue();
                serviceConfirmRef.child("JobDetails").removeValue();
                serviceConfirmRef.child("CustomerAccepted").setValue(false);
                mLayout.setVisibility(View.INVISIBLE);
                mRequest.setText("Confirm Request");
                mMap.clear();
                requestSent = false;
                mechanicFound =false;
                requestAccepted = false;
                Intent intent = new Intent(CustomerMapsActivity.this, CustomerServiceActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }
    public void setJobDetails(){
//        Once Mechanic has accepted the customer job details are set to the mechanic
        final DatabaseReference jobDetails = FirebaseDatabase.getInstance().getReference().child("Users").child("Mechanics").child(mechanicFoundID).child("JobDetails");

        if (!jobCompleted) {
            jobDetails.child("Price").setValue("0.00");
            jobDetails.child("Time").setValue("0.00");
            jobDetails.child("AdditionalServices").setValue("none");

            if (CustomerServiceActivity.noteText.equalsIgnoreCase("")){
                jobDetails.child("AdditionalNotes").setValue("none");
            }
            else {
                jobDetails.child("AdditionalNotes").setValue(CustomerServiceActivity.noteText);
            }

            if (CustomerServiceActivity.severity.equalsIgnoreCase("")){
                jobDetails.child("JobSeverity").setValue("none");
            }
            else {
                jobDetails.child("JobSeverity").setValue(CustomerServiceActivity.severity);
            }

            jobDetails.child("jobPoints").setValue(points);
            jobDetails.child("JobStarted").setValue(false);
            jobDetails.child("JobCompleted").setValue(false);
            jobDetails.child("CustomerNumber").setValue(customerNumber);
            jobDetails.child("CustomerServices").setValue(CustomerServiceActivity.servicesRequested);
        }

        jobDetails.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Map<String, Object> map = (Map <String, Object>) dataSnapshot.getValue();
                    if (map.get("JobCompleted")!=null) {
                        if (Boolean.parseBoolean(map.get("JobCompleted").toString())) {
                            jobCompleted = true;
                            jobPrice = map.get("Price").toString();
                            jobEnd = "Mechanic: " + services.child("Mechanics").child(mechanicFoundID).child("Name").getValue().toString() +
                                    " Additional cost of " + map.get("AdditionalServiceCost").toString() + " for " + map.get("AdditionalServices").toString();
                            jobStatusDetails();
                        }
                    }
                    else
                        jobCompleted = false;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {   }
        });

    }

    public void jobStatusDetails(){
        try {
            final DatabaseReference serviceConfirmRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Mechanics").child(mechanicFoundID);

            if (jobCompleted){
                jobDialog.setContentView(R.layout.activity_job_completed);
                mJobDone = jobDialog.findViewById(R.id.confirm_request);
                mAddFav = jobDialog.findViewById(R.id.favorite);
                mfinalMechanicName = jobDialog.findViewById(R.id.name_mechanic);
                mPrice = jobDialog.findViewById(R.id.price);
                mfinalMechanicName.setText(jobEnd);
                mPrice.setText("Rs. " + jobPrice);
                mJobDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        jobDialog.dismiss();
                        serviceConfirmRef.child("CustomerID").removeValue();
                        serviceConfirmRef.child("CustomerName").removeValue();
                        serviceConfirmRef.child("CustomerPhone").removeValue();
                        serviceConfirmRef.child("JobDetails").removeValue();
                        serviceConfirmRef.child("CustomerAccepted").setValue(false);
                        mLayout.setVisibility(View.INVISIBLE);
                        mRequest.setText("Confirm Request");
                        mMap.clear();
                        requestSent = false;
                        mechanicFound =false;
                        requestAccepted = false;
                        jobCompleted = false;
                        setRating(serviceConfirmRef);
                        Intent intent = new Intent(CustomerMapsActivity.this, CustomerServiceActivity.class);
                        startActivity(intent);
                        finish();
                        return;
                    }
                });

                mAddFav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        services = serviceDetails.dataSnapshot;
                        userName = services.child("Customers").child(userId).child("Name").getValue().toString();
                        serviceConfirmRef.child("CustomerFavoritesRequest").child("CustomerID").setValue(userId);
                        serviceConfirmRef.child("CustomerFavoritesRequest").child("CustomerName").setValue(userName);
                    }
                });
                jobDialog.show();
            }
        }catch (Exception e){}
    }

    private void setRating(DatabaseReference serviceDetails) {
        mSeverityGroup = jobDialog.findViewById(R.id.severityGroup);
        int selectedID = mSeverityGroup.getCheckedRadioButtonId();
        mRadioBtn = jobDialog.findViewById(selectedID);
        if (mRadioBtn!=null){
            String ratingType = mRadioBtn.getText().toString();
            switch (ratingType){
                case "Good":
                    serviceDetails.child("CustomerRatings").child(random.timeStamp()).setValue(3);
                    break;
                case "Neutral":
                    serviceDetails.child("CustomerRatings").child(random.timeStamp()).setValue(2);
                    break;
                case "Bad":
                    serviceDetails.child("CustomerRatings").child(random.timeStamp()).setValue(1);
                    break;
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            checkLocationPermission();
        }
    }

    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {

            checkLocationPermission();
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
            for (Location location : locationResult.getLocations()){
                mLastLocation = location;

                LatLng latlng = new LatLng(location.getLatitude(),location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            }
        }
    };

    private void checkLocationPermission() {
        ActivityCompat.requestPermissions(CustomerMapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:{
                if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }
                }
                break;
            }
        }
    }
    @Override
    protected void onStop() {
        super.onStop();

    }
}
