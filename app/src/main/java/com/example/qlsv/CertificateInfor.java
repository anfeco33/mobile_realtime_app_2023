package com.example.qlsv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

public class CertificateInfor extends AppCompatActivity {
    private TextView tvCertificateName, tvDate, tvDuration;
    private Button btnUpdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate_infor);
        tvCertificateName = findViewById(R.id.tvCertificateName);
        tvDate = findViewById(R.id.tvDate);
        tvDuration = findViewById(R.id.tvDuration);
        btnUpdate = findViewById(R.id.btnUpdate);

        String sId = getIntent().getStringExtra("sId");
        String name = getIntent().getStringExtra("name");
        DatabaseReference certificateRef = FirebaseDatabase.getInstance("https://qlsv-mobile-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("students/" + sId).child("certificates");
        Query query = certificateRef.orderByChild("name").equalTo(name);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String date = snapshot.child("date").getValue(String.class);
                    String duration = snapshot.child("duration").getValue(String.class);

                    tvCertificateName.setText(name);
                    tvDate.setText(date);
                    tvDuration.setText(duration);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CertificateInfor.this, "Failed to fetch student profile", Toast.LENGTH_SHORT).show();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getIntent().getStringExtra("role").equalsIgnoreCase("employee")) {
                    StyleableToast.makeText(CertificateInfor.this, "You do not have permission", R.style.toast_custom).show();
                } else {
                    String sId = getIntent().getStringExtra("sId");
                    String name = tvCertificateName.getText().toString();
                    String date = tvDate.getText().toString();
                    String duration = tvDuration.getText().toString();

                    Intent intent = new Intent(CertificateInfor.this, EditCertificate.class);
                    intent.putExtra("sId", sId);
                    intent.putExtra("name", name);
                    intent.putExtra("date", date);
                    intent.putExtra("duration", duration);
                    startActivity(intent);
                }
            }
        });
    }
}