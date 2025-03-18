package com.example.qlsv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;

public class UserManagerment extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    private List<Helper> helpers;
    private HelperAdapter helperAdapter;
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_managerment);

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        listView = findViewById(R.id.listView);
        helpers = new ArrayList<>();
        helperAdapter = new HelperAdapter(this, helpers);
        listView.setAdapter(helperAdapter);

        databaseReference = FirebaseDatabase.getInstance("https://qlsv-mobile-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users");
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                helpers.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Helper helper = dataSnapshot.getValue(Helper.class);
                    helpers.add(helper);
                }

                helperAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to read value.", error.toException());
            }
        };
        databaseReference.addValueEventListener(valueEventListener);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Helper selectedHelper = helperAdapter.getItem(position);
                if (selectedHelper != null) {
                    String phone = selectedHelper.getPhone();
                    Intent intent = new Intent(UserManagerment.this, UserProfile.class);
                    intent.putExtra("phone", phone);
                    intent.putExtra("role", getIntent().getStringExtra("role"));
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_user_toolbar_menu, menu);
        setDisplayHomeAsUpEnabled();
        return true;
    }
    private void setDisplayHomeAsUpEnabled() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (id == R.id.menu_add) {
//            if (getIntent().getStringExtra("role").equalsIgnoreCase("employee")) {
//                StyleableToast.makeText(UserManagerment.this, "You do not have permission", R.style.toast_custom).show();
//            } else {
                Intent intent = new Intent(UserManagerment.this, AddUser.class);
                startActivity(intent);
                return true;
            //}
        }
        return super.onOptionsItemSelected(item);
    }
}