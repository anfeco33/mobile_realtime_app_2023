package com.example.qlsv;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class EditStudentProfile extends AppCompatActivity {
    private EditText etId, etFullname, etGender, etAge, etClass, etEmail;
    private Button btnSave;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_student_profile);

        etId = findViewById(R.id.etId);
        etFullname = findViewById(R.id.etFullname);
        etAge = findViewById(R.id.etAge);
        etGender = findViewById(R.id.etGender);
        etClass = findViewById(R.id.etClass);
        etEmail = findViewById(R.id.etEmail);
        btnSave = findViewById(R.id.btnSave);

        etId.setText(getIntent().getStringExtra("id"));
        etFullname.setText(getIntent().getStringExtra("fullname"));
        etGender.setText(getIntent().getStringExtra("gender"));
        etAge.setText(getIntent().getStringExtra("age"));
        etClass.setText(getIntent().getStringExtra("sClass"));
        etEmail.setText(getIntent().getStringExtra("email"));

        String id = getIntent().getStringExtra("id");
        firebaseDatabase = FirebaseDatabase.getInstance("https://qlsv-mobile-default-rtdb.asia-southeast1.firebasedatabase.app/");
        databaseReference = firebaseDatabase.getReference("students/" + id);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> updates = new HashMap<>();
                updates.put("id", etId.getText().toString());
                updates.put("fullname", etFullname.getText().toString());
                updates.put("gender", etGender.getText().toString());
                updates.put("age", etAge.getText().toString());
                updates.put("sClass", etClass.getText().toString());
                updates.put("email", etEmail.getText().toString());

                databaseReference.updateChildren(updates, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        Toast.makeText(EditStudentProfile.this, "Updated successfully!!!", Toast.LENGTH_SHORT).show();
                    }
                });
                Intent intent = new Intent(EditStudentProfile.this, StudentManagement.class);
                startActivity(intent);
            }
        });
    }
}