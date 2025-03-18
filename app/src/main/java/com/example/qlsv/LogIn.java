package com.example.qlsv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import io.github.muddz.styleabletoast.StyleableToast;

public class LogIn extends AppCompatActivity {
    private EditText etPhoneForLogin;
    private EditText etPasswordForLogin;
    private Button btnLogin;
    private Helper helper;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://qlsv-mobile-default-rtdb.asia-southeast1.firebasedatabase.app/");
    private DatabaseReference dbR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        etPhoneForLogin = findViewById(R.id.etPhoneForLogin);
        etPasswordForLogin = findViewById(R.id.etPasswordForLogin);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validatePhone() | !validatePassword()) {
                    return;
                } else {
                    checkUser();
                }
            }
        });
    }

    public Boolean validatePhone() {
        String validate = etPhoneForLogin.getText().toString();
        if (TextUtils.isEmpty(validate)) {
            etPhoneForLogin.setError("Please fill in your phone number!");
            return false;
        } else {
            etPhoneForLogin.setError(null);
            return true;
        }
    }

    public Boolean validatePassword()
    {
        String validate = etPasswordForLogin.getText().toString();
        if (validate.isEmpty()) {
            etPasswordForLogin.setError("Please fill in your password!");
            return false;
        } else {
            etPasswordForLogin.setError(null);
            return true;
        }
    }

    public void checkUser() {
        String phone = etPhoneForLogin.getText().toString().trim();
        String pass = etPasswordForLogin.getText().toString().trim();

        dbR = firebaseDatabase.getInstance().getReference("users");

        Query checkUserInDB = dbR.orderByChild("phone").equalTo(phone);

        checkUserInDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    etPhoneForLogin.setError(null);
                    String passwordInDB = snapshot.child(phone).child("password").getValue(String.class);
                    String role = snapshot.child(phone).child("role").getValue(String.class);
                    String status = snapshot.child(phone).child("status").getValue(String.class);
                    String email = snapshot.child(phone).child("email").getValue(String.class);
                    String fullname = snapshot.child(phone).child("fullname").getValue(String.class);
                    String age = snapshot.child(phone).child("age").getValue(String.class);

                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(phone);

                    if (passwordInDB != null && passwordInDB.equals(pass)) {
                        etPasswordForLogin.setError(null);

                        Intent intent;
                        if (role.equalsIgnoreCase("manager")) {
                            if (status.equalsIgnoreCase("Locked")) {
                                StyleableToast.makeText(LogIn.this, "Access denied! Your account is locked.", R.style.toast_custom).show();
                                return;
                            }
                            String loginTime = String.valueOf(System.currentTimeMillis());
                            userRef.child("loginHistory").child("Timestamp").setValue(loginTime);
                            intent = new Intent(LogIn.this, ManagerPage.class);
                            //startActivity(intent);
                        } else if (role.equalsIgnoreCase("admin")) {
                            String loginTime = String.valueOf(System.currentTimeMillis());
                            userRef.child("loginHistory").child("Timestamp").setValue(loginTime);
                            intent = new Intent(LogIn.this, MainActivity.class);
                            //startActivity(intent);
                        }
                        else if (role.equalsIgnoreCase("employee")) {
                            if (status.equalsIgnoreCase("Locked")) {
                                StyleableToast.makeText(LogIn.this, "Access denied! Your account is locked.", R.style.toast_custom).show();
                                return;
                            }
                            String loginTime = String.valueOf(System.currentTimeMillis());
                            userRef.child("loginHistory").child("Timestamp").setValue(loginTime);
                            intent = new Intent(LogIn.this, ManagerPage.class);
                            intent.putExtra("role", role);
                            //startActivity(intent);
                        }
                        else {
                            StyleableToast.makeText(LogIn.this, "Access denied!", R.style.toast_custom).show();
                            return;
                        }
                        intent.putExtra("phone", phone);
                        intent.putExtra("role", role);
                        intent.putExtra("email", email);
                        intent.putExtra("fullname", fullname);
                        intent.putExtra("age", age);

                        startActivity(intent);
                    } else {
                        etPasswordForLogin.setError("Error: Your password is incorrect!");
                        etPasswordForLogin.requestFocus();
                    }
                } else {
                    etPhoneForLogin.setError("Error: User does not exist!");
                    etPhoneForLogin.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}