package com.example.qlsv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    TextView tvWelcomeAdmin;
    CardView cvUserManagement, cvStudentManagement;
    private BottomNavigationView bottomNavigationView;
    String userPhoneNumber, userRole, userEmail, userFullname, userAge;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("MainActivity", "MainActivity created");
        cvUserManagement = findViewById(R.id.cvUserManagement);
        cvStudentManagement = findViewById(R.id.cvStudentManagement);

        bottomNavigationView = findViewById(R.id.BottomNavigationView);
        tvWelcomeAdmin = findViewById(R.id.tvWelcomeAdmin);

        userPhoneNumber = getIntent().getStringExtra("phone");
        userRole = getIntent().getStringExtra("role");
        userEmail = getIntent().getStringExtra("email");
        userFullname = getIntent().getStringExtra("fullname");
        userAge = getIntent().getStringExtra("age");

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.itemHome) {
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    cvUserManagement.setVisibility(View.VISIBLE);
                    cvStudentManagement.setVisibility(View.VISIBLE);
                    tvWelcomeAdmin.setVisibility(View.VISIBLE);
                    return true;
                } else if (item.getItemId() == R.id.itemSetting) {
                    // hide
                    findViewById(R.id.cvUserManagement).setVisibility(View.GONE);
                    findViewById(R.id.cvStudentManagement).setVisibility(View.GONE);
                    tvWelcomeAdmin.setVisibility(View.GONE);
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


        cvUserManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserManagerment.class);
                intent.putExtra("role", getIntent().getStringExtra("role"));
                startActivity(intent);
            }
        });

        cvStudentManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StudentManagement.class);
                intent.putExtra("role", getIntent().getStringExtra("role"));
                startActivity(intent);
            }
        });
    }

}
