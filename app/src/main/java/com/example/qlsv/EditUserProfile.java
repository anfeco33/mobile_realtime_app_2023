package com.example.qlsv;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class EditUserProfile extends AppCompatActivity {
    private EditText etFullName, etAge, etEmail, etPass, etPhone;
    private Spinner spnStatus, spnRole;
    private Button btnSave;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

        etFullName = findViewById(R.id.etFullname);
        etAge = findViewById(R.id.etAge);
        etEmail = findViewById(R.id.etEmail);
        etPass = findViewById(R.id.etPass);
        etPhone = findViewById(R.id.etPhone);
        spnStatus = findViewById(R.id.spnStatus);
        spnRole = findViewById(R.id.spnRole);
        btnSave = findViewById(R.id.btnSave);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Edit profile");
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#D0A2F7")));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Spinner for Status
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditUserProfile.this, android.R.layout.simple_spinner_item, getStatus()) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (position == 0) {
                    TextView textView = view.findViewById(android.R.id.text1);
                    textView.setTextColor(getResources().getColor(R.color.colorHintText));
                }
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                if (position == 0) {
                    TextView textView = view.findViewById(android.R.id.text1);
                    textView.setTextColor(getResources().getColor(R.color.colorHintText));
                }
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnStatus.setAdapter(adapter);

        // Spinner for Role
        ArrayAdapter<String> adapterRole = new ArrayAdapter<String>(EditUserProfile.this, android.R.layout.simple_spinner_item, getRole()) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (position == 0) {
                    TextView textView = view.findViewById(android.R.id.text1);
                    textView.setTextColor(getResources().getColor(R.color.colorHintText));
                }
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                if (position == 0) {
                    TextView textView = view.findViewById(android.R.id.text1);
                    textView.setTextColor(getResources().getColor(R.color.colorHintRedText));
                }
                return view;
            }
        };
        adapterRole.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnRole.setAdapter(adapterRole);

        etFullName.setText(getIntent().getStringExtra("fullname"));
        etAge.setText(getIntent().getStringExtra("age"));
        etEmail.setText(getIntent().getStringExtra("email"));
        etPass.setText(getIntent().getStringExtra("password"));
        etPhone.setText(getIntent().getStringExtra("phone"));
        if (getIntent().getStringExtra("status").equals("Normal")) {
            spnStatus.setSelection(0);
        } else {
            spnStatus.setSelection(1);
        }
        if (getIntent().getStringExtra("role").equals("Admin") || getIntent().getStringExtra("role").equals("admin")) {
            spnRole.setSelection(0);
        } else if (getIntent().getStringExtra("role").equals("Manager") || getIntent().getStringExtra("role").equals("manager")) {
            spnRole.setSelection(1);
        } else {
            spnRole.setSelection(2);
        }

        String phone = getIntent().getStringExtra("phone");
        firebaseDatabase = FirebaseDatabase.getInstance("https://qlsv-mobile-default-rtdb.asia-southeast1.firebasedatabase.app/");
        databaseReference = firebaseDatabase.getReference("users/" + phone);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> updates = new HashMap<>();
                updates.put("fullname", etFullName.getText().toString());
                updates.put("age", etAge.getText().toString());
                updates.put("email", etEmail.getText().toString());
                updates.put("password", etPass.getText().toString());
                updates.put("phone", etPhone.getText().toString());
                updates.put("status", spnStatus.getSelectedItem().toString());
                updates.put("role", spnRole.getSelectedItem().toString());

                databaseReference.updateChildren(updates, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        Toast.makeText(EditUserProfile.this, "Updated successfully!!!", Toast.LENGTH_SHORT).show();
                    }
                });
                Intent intent = new Intent(EditUserProfile.this, UserManagerment.class);
                startActivity(intent);
            }
        });
    }

    private String[] getStatus() {
        return new String[]{"Normal", "Locked"};
    }

    private String[] getRole() {
        return new String[]{"Admin", "Manager", "Employee"};
    }
}