package com.example.readyrescuecus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CustomerGameActivity extends AppCompatActivity {

    private Button mGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_game);

        setNavigation();

        navigateToGame();

    }

    private void navigateToGame() {
        mGame = findViewById(R.id.start_game);
        mGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerGameActivity.this, QuizQuestionsActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }

    private void setNavigation() {
//        Initialize variables
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
//        Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.navigation_game);
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
}
