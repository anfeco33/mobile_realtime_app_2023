package com.example.qlsv;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.Calendar;

import io.github.muddz.styleabletoast.StyleableToast;

public class AddCertificate extends AppCompatActivity {
    private EditText etCertificateName, etDate, etDuration;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_certificate);
        etCertificateName = findViewById(R.id.etCertificateName);
        etDate = findViewById(R.id.etDate);
        etDuration = findViewById(R.id.etDuration);
        btnSave = findViewById(R.id.btnSave);

        String id = getIntent().getStringExtra("id");
        DatabaseReference certificateRef = FirebaseDatabase.getInstance("https://qlsv-mobile-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("students/" + id).child("certificates");

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddCertificate.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDayOfMonth) {

                                String selectedDate = selectedDayOfMonth + "/" + (selectedMonth + 1) + "/" + selectedYear;
                                etDate.setText(selectedDate);
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });

        etDuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddCertificate.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDayOfMonth) {

                                String selectedDate = selectedDayOfMonth + "/" + (selectedMonth + 1) + "/" + selectedYear;
                                etDuration.setText(selectedDate);
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etCertificateName.getText().toString();
                String date = etDate.getText().toString();
                String duration = etDuration.getText().toString();

                if(TextUtils.isEmpty(name) && TextUtils.isEmpty(date) && TextUtils.isEmpty(duration)) {
                    StyleableToast.makeText(AddCertificate.this, "Please fill in all information!", R.style.toast_custom).show();
                } else {
                    Certificate certificate = new Certificate(name, date, duration);
                    certificateRef.child(name).setValue(certificate);
                    StyleableToast.makeText(AddCertificate.this, "Add certificate successfully!!!", R.style.toast_custom).show();
                    Intent intent = new Intent(AddCertificate.this, CertificateList.class);
                    startActivity(intent);
                }
            }
        });
    }
}