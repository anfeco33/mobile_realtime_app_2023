package com.example.qlsv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import io.github.muddz.styleabletoast.StyleableToast;

public class AddUser extends AppCompatActivity {
    private EditText etFullName, etAge, etEmail, etPass, etPhone;
    private Spinner spnStatus, spnRole;
    private Button btnSave;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        etFullName = findViewById(R.id.etFullname);
        etAge = findViewById(R.id.etAge);
        etEmail = findViewById(R.id.etEmail);
        etPass = findViewById(R.id.etPass);
        etPhone = findViewById(R.id.etPhone);
        spnStatus = findViewById(R.id.spnStatus);
        spnRole = findViewById(R.id.spnRole);
        btnSave = findViewById(R.id.btnSave);
        progressBar = findViewById(R.id.ProgressBar);


        firebaseDatabase = FirebaseDatabase.getInstance("https://qlsv-mobile-default-rtdb.asia-southeast1.firebasedatabase.app/");
        databaseReference = firebaseDatabase.getReference("users");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Add New User");
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#D0A2F7")));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Spinner for Status
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddUser.this, android.R.layout.simple_spinner_item, getStatus()) {
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
        ArrayAdapter<String> adapterRole = new ArrayAdapter<String>(AddUser.this, android.R.layout.simple_spinner_item, getRole()) {
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

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                String fullname = etFullName.getText().toString();
                String age = etAge.getText().toString();
                String email = etEmail.getText().toString();
                String password = etPass.getText().toString();
                String phone = etPhone.getText().toString();
                String status = spnStatus.getSelectedItem().toString();
                String role = spnRole.getSelectedItem().toString();
                String image = "";


                if (TextUtils.isEmpty(fullname) || TextUtils.isEmpty(age) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(status) || TextUtils.isEmpty(role))  {
                    StyleableToast.makeText(AddUser.this, "Please fill in all information!", R.style.toast_custom).show();
                    progressBar.setVisibility(View.GONE);
                }
                else {
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(phone)) {
                                StyleableToast.makeText(AddUser.this, "The phone number has already been used!", R.style.toast_custom).show();
                                progressBar.setVisibility(View.GONE);
                            }
                            else {
                                if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                    etEmail.setError("Please enter a valid email address!");
                                    progressBar.setVisibility(View.GONE);
                                    return;
                                }

                                Helper help = new Helper(email, password, fullname, age, phone, status, role, image);
                                databaseReference.child(phone).setValue(help);
                                StyleableToast.makeText(AddUser.this, "Added user successfully", R.style.toast_custom).show();
                                Intent intent = new Intent(AddUser.this, UserManagerment.class);
                                startActivity(intent);
                                progressBar.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    private String[] getStatus() {
        return new String[]{"Normal", "Locked"};
    }

    private String[] getRole() {
        return new String[]{"Admin", "Manager", "Employee"};
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
