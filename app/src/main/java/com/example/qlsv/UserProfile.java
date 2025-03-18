package com.example.qlsv;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import io.github.muddz.styleabletoast.StyleableToast;

public class UserProfile extends AppCompatActivity {
    private TextView tvFullName, tvAge, tvEmail, tvPassword, tvPhone, tvStatus, tvRole, tvLoginHistory;
    private Button btnEdit, btnRemove;
    Helper helper;
    private StringBuilder loginHistoryBuilder;

    private DatabaseReference databaseReference;
    //private CircleImageView ciProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        tvFullName = findViewById(R.id.tvFullName);
        tvAge = findViewById(R.id.tvAge);
        tvEmail = findViewById(R.id.tvEmail);
        tvPassword = findViewById(R.id.tvPassword);
        tvPhone = findViewById(R.id.tvPhone);
        tvStatus = findViewById(R.id.tvStatus);
        tvRole = findViewById(R.id.tvRole);
        btnEdit = findViewById(R.id.btnEdit);
        btnRemove = findViewById(R.id.btnRemove);
        tvLoginHistory = findViewById(R.id.tvLoginHistory);
        loginHistoryBuilder = new StringBuilder();

        helper = new Helper();

        databaseReference = FirebaseDatabase.getInstance("https://qlsv-mobile-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users");
        String phone = getIntent().getStringExtra("phone");
        Query query = databaseReference.orderByChild("phone").equalTo(phone);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String age = snapshot.child("age").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String password = snapshot.child("password").getValue(String.class);
                    String fullname = snapshot.child("fullname").getValue(String.class);
                    String status = snapshot.child("status").getValue(String.class);
                    String role = snapshot.child("role").getValue(String.class);

                    if (snapshot.hasChild("loginHistory")) {
                        Map<String, String> loginHistory = (Map<String, String>) snapshot.child("loginHistory").getValue();
                        helper.setLoginHistory(loginHistory);
                        loginHistoryBuilder.setLength(0); // clear previous content

                        // formatter for the timestamp
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                        formatter.setTimeZone(TimeZone.getDefault());

                        for (Map.Entry<String, String> entry : loginHistory.entrySet()) {
                            // converted timestamp
                            long timestamp = Long.parseLong(entry.getValue());
                            String formattedDate = formatter.format(new Date(timestamp));

                            loginHistoryBuilder.append(formattedDate);
                        }

                        tvLoginHistory.setText(loginHistoryBuilder.toString());
                    } else {
                        tvLoginHistory.setText("User Never Accessed");
                    }

                    tvFullName.setText(fullname);
                    tvAge.setText(age);
                    tvEmail.setText(email);
                    tvPassword.setText(password);
                    tvPhone.setText(phone);
                    tvStatus.setText(status);
                    tvRole.setText(role);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserProfile.this, "Failed to fetch student profile", Toast.LENGTH_SHORT).show();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getIntent().getStringExtra("role").equalsIgnoreCase("employee")) {
                    StyleableToast.makeText(UserProfile.this, "You do not have permission", R.style.toast_custom).show();
                } else {
                    String age = tvAge.getText().toString();
                    String email = tvEmail.getText().toString();
                    String password = tvPassword.getText().toString();
                    String phone = tvPhone.getText().toString();
                    String fullname = tvFullName.getText().toString();
                    String status = tvStatus.getText().toString();
                    String role = tvRole.getText().toString();

                    Intent intent1 = new Intent(UserProfile.this, EditUserProfile.class);
                    intent1.putExtra("fullname", fullname);
                    intent1.putExtra("age", age);
                    intent1.putExtra("email", email);
                    intent1.putExtra("password", password);
                    intent1.putExtra("phone", phone);
                    intent1.putExtra("status", status);
                    intent1.putExtra("role", role);
                    startActivity(intent1);
                }
            }
        });
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getIntent().getStringExtra("role").equalsIgnoreCase("employee")) {
                    StyleableToast.makeText(UserProfile.this, "You do not have permission", R.style.toast_custom).show();
                } else {
                    // Confirmation before deletion
                    AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
                    builder.setTitle("Are you sure?");
                    builder.setMessage("The user and his information will be permanently deleted.");
                    builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Yes
                            String phone = tvPhone.getText().toString();
                            databaseReference.orderByChild("phone").equalTo(phone).addValueEventListener(new ValueEventListener() {
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

                            Intent intent = new Intent(UserProfile.this, UserManagerment.class);
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