package com.example.readyrescuecus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.readyrescuecus.servicefunctions.ServiceDetails;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class CustomerFavoritesActivity extends AppCompatActivity {

    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private ListView listView;
    private String userId;
    public static String mechanicID = null;

    public ServiceDetails favMechanic = new ServiceDetails();

    DataSnapshot favorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_favorites);

        setNavigation();

        //        List View initialization

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);

        listView = findViewById(R.id.favorite_list);

        listView.setAdapter(arrayAdapter);

        setFavoritesToList();

        clickFavouriteMechanic();
    }

    private void setNavigation() {
        //        Initialize variables
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

//        Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.navigation_favourite);

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

    private void clickFavouriteMechanic() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(CustomerFavoritesActivity.this[position],Toast.LENGTH_SHORT);
                favMechanic.getFavMechanicName(mechanicID);
                Map<String, Object> map = (Map <String, Object>) favorites.getValue();
                Object firstKey = map.keySet().toArray()[position];
                mechanicID = firstKey.toString();
                Intent intent = new Intent(CustomerFavoritesActivity.this, CustomerServiceActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }

    private void setFavoritesToList() {
        userId = FirebaseAuth.getInstance().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userId).child("MechanicFavorites");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //        Setting the values

                if (dataSnapshot.exists()) {
                    favorites = dataSnapshot;
                    Map<String, Object> map = (Map <String, Object>) dataSnapshot.getValue();
                    for (int i=0; i<map.size(); i++){
                        Object firstKey = map.keySet().toArray()[i];
                        Object valueForFirstKey = map.get(firstKey);
                        arrayList.add(valueForFirstKey.toString());
                        arrayAdapter.notifyDataSetChanged();
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
