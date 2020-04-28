package com.example.readyrescuecus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CustomerProfileActivity extends AppCompatActivity {

    private Button mLogout;
    private TextView mName, mNumber, mPoints;
    private String userId, userName, userNumber, userPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        setNavigation();

        getUserNameNumber();

        logOut();
    }

    private void setNavigation() {
        //        Initialize variables
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

//        Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);

//        Item Selected Listener

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.navigation_home:
                        startActivity(new Intent(getApplicationContext(),CustomerServiceActivity.class));
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
                        return true;
                }
                return false;
            }
        });
    }

    private void getUserNameNumber() {
        mName = findViewById(R.id.customer_name);
        mNumber = findViewById(R.id.number_heading);
        mPoints = findViewById(R.id.Points);

        userId = FirebaseAuth.getInstance().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //        Setting the values
                if (dataSnapshot.exists()) {
                    userName = dataSnapshot.child("Users").child("Customers").child(userId).child("Name").getValue().toString();
                    userNumber = dataSnapshot.child("Users").child("Customers").child(userId).child("Phone").getValue().toString();
                    userPoints = dataSnapshot.child("Users").child("Customers").child(userId).child("Points").getValue().toString();

                    mName.setText(userName);
                    mNumber.setText(userNumber);
                    mPoints.setText(userPoints);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void logOut() {
        mLogout = findViewById(R.id.logout);
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(CustomerProfileActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }
}