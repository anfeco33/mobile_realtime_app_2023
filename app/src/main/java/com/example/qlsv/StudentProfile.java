package com.example.qlsv;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import io.github.muddz.styleabletoast.StyleableToast;

public class StudentProfile extends AppCompatActivity {
    private TextView tvId, tvFullname, tvGender, tvAge, tvClass, tvEmail;
    private Button btnCertificateList, btnEdit, btnRemove;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        tvId = findViewById(R.id.tvId);
        tvFullname = findViewById(R.id.tvFullname);
        tvGender = findViewById(R.id.tvGender);
        tvAge = findViewById(R.id.tvAge);
        tvClass = findViewById(R.id.tvClass);
        tvEmail = findViewById(R.id.tvEmail);
        btnCertificateList = findViewById(R.id.btnCertificateList);
        btnEdit = findViewById(R.id.btnEdit);
        btnRemove = findViewById(R.id.btnRemove);

        databaseReference = FirebaseDatabase.getInstance("https://qlsv-mobile-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("students");
        String studentId = getIntent().getStringExtra("studentId");
        Query query = databaseReference.orderByChild("id").equalTo(studentId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String gender = snapshot.child("gender").getValue(String.class);
                    String age = snapshot.child("age").getValue(String.class);
                    String studentClass = snapshot.child("studentClass").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);

                    tvId.setText(studentId);
                    tvFullname.setText(name);
                    tvGender.setText(gender);
                    tvAge.setText(age);
                    tvClass.setText(studentClass);
                    tvEmail.setText(email);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(StudentProfile.this, "Failed to fetch student profile", Toast.LENGTH_SHORT).show();
            }
        });

        btnCertificateList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = tvId.getText().toString();
                Intent intent = new Intent(StudentProfile.this, CertificateList.class);
                intent.putExtra("id", id);
                intent.putExtra("role", getIntent().getStringExtra("role"));
                startActivity(intent);
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getIntent().getStringExtra("role").equalsIgnoreCase("employee")) {
                    StyleableToast.makeText(StudentProfile.this, "You do not have permission", R.style.toast_custom).show();
                } else {
                    String id = tvId.getText().toString();
                    String fullname = tvFullname.getText().toString();
                    String gender = tvGender.getText().toString();
                    String age = tvAge.getText().toString();
                    String sClass = tvClass.getText().toString();
                    String email = tvEmail.getText().toString();

                    Intent intent1 = new Intent(StudentProfile.this, EditStudentProfile.class);
                    intent1.putExtra("id", id);
                    intent1.putExtra("fullname", fullname);
                    intent1.putExtra("gender", gender);
                    intent1.putExtra("age", age);
                    intent1.putExtra("sClass", sClass);
                    intent1.putExtra("email", email);
                    startActivity(intent1);
                }
            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getIntent().getStringExtra("role").equalsIgnoreCase("employee")) {
                    StyleableToast.makeText(StudentProfile.this, "You do not have permission", R.style.toast_custom).show();
                } else {
                    // Confirmation before deletion
                    AlertDialog.Builder builder = new AlertDialog.Builder(StudentProfile.this);
                    builder.setTitle("Are you sure?");
                    builder.setMessage("The user and his information will be permanently deleted.");
                    builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Yes
                            String id = tvId.getText().toString();
                            databaseReference.orderByChild("id").equalTo(id).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                                        data.getRef().removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                                }
                            });

                            Intent intent = new Intent(StudentProfile.this, StudentManagement.class);
                            startActivity(intent);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Close dialog
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
            }
        });
    }
}