package com.example.qlsv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;

public class AddStudent extends AppCompatActivity {
    private EditText etId, etFullname, etGender, etAge, etClass, etEmail;
    private Button btnSave;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Student student;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        etId = findViewById(R.id.etId);
        etFullname = findViewById(R.id.etFullname);
        etAge = findViewById(R.id.etAge);
        etGender = findViewById(R.id.etGender);
        etClass = findViewById(R.id.etClass);
        etEmail = findViewById(R.id.etEmail);
        btnSave = findViewById(R.id.btnSave);

        firebaseDatabase = FirebaseDatabase.getInstance("https://qlsv-mobile-default-rtdb.asia-southeast1.firebasedatabase.app/");
        databaseReference = firebaseDatabase.getReference("students");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Add New Student");
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#D0A2F7")));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = etId.getText().toString();
                String name = etFullname.getText().toString();
                String gender = etGender.getText().toString();
                String age = etAge.getText().toString();
                String sClass = etClass.getText().toString();
                String email = etEmail.getText().toString();
                List<String> certificates = new ArrayList<>();

                if(TextUtils.isEmpty(id) && TextUtils.isEmpty(name) && TextUtils.isEmpty(gender) && TextUtils.isEmpty(age) && TextUtils.isEmpty(sClass) && TextUtils.isEmpty(email)) {
                    StyleableToast.makeText(AddStudent.this, "Please fill in all information!", R.style.toast_custom).show();
                }
                else {
                    databaseReference.child("students").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(id)) {
                                StyleableToast.makeText(AddStudent.this, "The ID has already been used!", R.style.toast_custom).show();
                            }
                            else {
                                Student student = new Student(id, name, gender, age, sClass, email, certificates);
                                databaseReference.child(id).setValue(student);
                                StyleableToast.makeText(AddStudent.this, "Added student successfully", R.style.toast_custom).show();
                                Intent intent = new Intent(AddStudent.this, StudentManagement.class);
                                startActivity(intent);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
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