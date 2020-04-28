package com.example.readyrescuecus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CustomerServiceActivity extends AppCompatActivity{

    private Button mConfirmRequest, mAddNotes;
    private ImageButton mNotesButton;
    private CheckBox mTire, mBattery, mLockSmith, mFuel, mEngine, mTowing;
    private RadioButton mRadioBtn;
    private RadioGroup mSeverityGroup;
    private EditText mNote;
    public static String noteText = "";
    public static String severity = "";
    Dialog jobDialog;

    public static List<String> servicesRequested =  new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_service);

        setNavigation();

        setNotes();

        setService();
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

    private void setService() {
        mConfirmRequest = findViewById(R.id.request_Assistance);

        mConfirmRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectServices();

                if (mTire.isChecked()||mBattery.isChecked()||mLockSmith.isChecked()||mFuel.isChecked()||mEngine.isChecked()||mTowing.isChecked()){
                    Intent intent = new Intent(CustomerServiceActivity.this, CustomerMapsActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
                else
                    Toast.makeText(CustomerServiceActivity.this,"At least one service should be selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setNotes() {
        jobDialog = new Dialog(this);

        mNotesButton = findViewById(R.id.add_notes);

        mNotesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jobDialog.setContentView(R.layout.activity_job_details);
                mNote = jobDialog.findViewById(R.id.note_text);
                mSeverityGroup = jobDialog.findViewById(R.id.severityGroup);
                jobDialog.show();
                addNotes();
            }
        });

    }

    private void addNotes() {

        mAddNotes = jobDialog.findViewById(R.id.add_button);

        mAddNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteText = mNote.getText().toString();

                int selectedID = mSeverityGroup.getCheckedRadioButtonId();

                mRadioBtn = jobDialog.findViewById(selectedID);

                severity = mRadioBtn.getText().toString();
                jobDialog.hide();
            }
        });

    }

    public void selectServices(){

        mTire = findViewById(R.id.tire_change);
        mBattery = findViewById(R.id.battery_jump_start);
        mLockSmith = findViewById(R.id.lock_smith_services);
        mFuel = findViewById(R.id.fuel_delivery);
        mEngine = findViewById(R.id.engine_issues);
        mTowing = findViewById(R.id.towing_services);
        servicesRequested.clear();

        String userId = FirebaseAuth.getInstance().getUid();

        DatabaseReference tireChange = FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(userId).child("Services").child("TireChange");
        if (mTire.isChecked()){
            tireChange.setValue(true);
            servicesRequested.add("Tire Change");
        }
        else
            tireChange.setValue(false);

        DatabaseReference batteryChange = FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(userId).child("Services").child("BatteryJumpStart");
        if (mBattery.isChecked()){
            batteryChange.setValue(true);
            servicesRequested.add("Battery Jump Start");
        }
        else
            batteryChange.setValue(false);

        DatabaseReference lockSmithService = FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(userId).child("Services").child("LockSmithService");
        if (mLockSmith.isChecked()){
            lockSmithService.setValue(true);
            servicesRequested.add("Lock Smith Services");
        }
        else
            lockSmithService.setValue(false);

        DatabaseReference FuelDelivery = FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(userId).child("Services").child("FuelDelivery");
        if (mFuel.isChecked()){
            FuelDelivery.setValue(true);
            servicesRequested.add("Fuel, Oil, Coolant Delivery");
        }
        else
            FuelDelivery.setValue(false);

        DatabaseReference EngineIssues = FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(userId).child("Services").child("EngineIssues");
        if (mEngine.isChecked()){
            EngineIssues.setValue(true);
            servicesRequested.add("Engine Related Issues");
        }
        else
            EngineIssues.setValue(false);

        DatabaseReference towingServices = FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(userId).child("Services").child("TowingServices");
        if (mTowing.isChecked()){
           towingServices.setValue(true);
           servicesRequested.add("Towing Services");
        }
        else
            towingServices.setValue(false);



    }

}
