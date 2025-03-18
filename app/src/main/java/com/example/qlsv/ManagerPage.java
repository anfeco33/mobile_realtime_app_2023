package com.example.qlsv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ManagerPage extends AppCompatActivity {
    CardView cvStudentManagement;
    TextView tvWelcome;
    private BottomNavigationView bottomNavigationView;
    String userPhoneNumber, userRole, userEmail, userFullname, userAge;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_page);

        Log.d("MainActivity", "MainActivity created");
        cvStudentManagement = findViewById(R.id.cvStudentManagement);
        tvWelcome = findViewById(R.id.tvWelcome);

        bottomNavigationView = findViewById(R.id.BottomNavigationView);

        userPhoneNumber = getIntent().getStringExtra("phone");
        userRole = getIntent().getStringExtra("role");
        userEmail = getIntent().getStringExtra("email");
        userFullname = getIntent().getStringExtra("fullname");
        userAge = getIntent().getStringExtra("age");

        if (getIntent().getStringExtra("role").equalsIgnoreCase("employee")) {
            tvWelcome.setText("Welcome Employee");
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.itemHome) {
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    tvWelcome.setText("Welcome Manager");
                    cvStudentManagement.setVisibility(View.VISIBLE);
                    tvWelcome.setVisibility(View.VISIBLE);
                    if (getIntent().getStringExtra("role").equalsIgnoreCase("employee")) {
                        tvWelcome.setText("Welcome Employee");
                    }
                    return true;
                } else if (item.getItemId() == R.id.itemSetting) {
                    // hide
                    findViewById(R.id.cvStudentManagement).setVisibility(View.GONE);
                    tvWelcome.setVisibility(View.GONE);
                    Settings settingsFragment = Settings.newInstance(userPhoneNumber, userRole, userEmail, userFullname, userAge);

                    // Leftovers
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, settingsFragment)
                            .addToBackStack(null)
                            .commit();
                    return true;
                }
                return false;
            }
        });

        cvStudentManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerPage.this, StudentManagement.class);
                intent.putExtra("role", getIntent().getStringExtra("role"));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}