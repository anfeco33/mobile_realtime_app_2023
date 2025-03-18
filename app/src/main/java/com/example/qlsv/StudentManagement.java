package com.example.qlsv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.ChildEvent;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;

public class StudentManagement extends AppCompatActivity {
    private EditText etSearch;
    private Spinner sortSpinner;
    private String[] sortOptions = {"Id", "Class", "Age"};
    private ListView studentListView;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    private List<Student> students;
    private StudentAdapter studentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_management);

        etSearch = findViewById(R.id.etSearch);
        sortSpinner = findViewById(R.id.sortSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sortOptions);
        sortSpinner.setAdapter(adapter);
        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        studentListView = findViewById(R.id.studentListView);
        students = new ArrayList<>();
        studentAdapter = new StudentAdapter(this, students);
        studentListView.setAdapter(studentAdapter);

        databaseReference = FirebaseDatabase.getInstance("https://qlsv-mobile-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("students");

        studentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Student selectedStudent = studentAdapter.getItem(position);
                if (selectedStudent != null) {
                    //String name = selectedStudent.getName();
                    String studentId = selectedStudent.getId();
                    Intent intent = new Intent(StudentManagement.this, StudentProfile.class);
                    intent.putExtra("studentId", studentId);
                    intent.putExtra("role", getIntent().getStringExtra("role"));
                    startActivity(intent);
                }
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String keyword = s.toString();
                students.clear();
                Query query = databaseReference;
                if (!TextUtils.isEmpty(keyword)) {
                    query = query.orderByChild("name").startAt(keyword).endAt(keyword + "\uf8ff");
                }
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Student student = dataSnapshot.getValue(Student.class);
                            students.add(student);
                        }
                        studentAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(StudentManagement.this, "Search canceled", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = parent.getItemAtPosition(position).toString();
                if (selectedOption.equals("Id")) {
                    students.clear();
                    Query query = databaseReference.orderByChild("id");
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Student student = dataSnapshot.getValue(Student.class);
                                students.add(student);
                            }
                            studentAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else if (selectedOption.equals("Class")) {
                    students.clear();
                    Query query = databaseReference.orderByChild("studentClass");
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Student student = dataSnapshot.getValue(Student.class);
                                students.add(student);
                            }
                            studentAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else if (selectedOption.equals("Age")) {
                    students.clear();
                    Query query = databaseReference.orderByChild("age");
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Student student = dataSnapshot.getValue(Student.class);
                                students.add(student);
                            }
                            studentAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
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

        String role = getIntent().getStringExtra("role");

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (id == R.id.menu_add || id == R.id.itemImport || id == R.id.itemExport) {
            // Check if role is not null and if it equals "employee"
            if (role != null && role.equalsIgnoreCase("employee")) {
                StyleableToast.makeText(StudentManagement.this, "You do not have permission", R.style.toast_custom).show();
            } else {
                // Handle the action
                Intent intent = null;
                if (id == R.id.menu_add) {
                    intent = new Intent(StudentManagement.this, AddStudent.class);
                } else if (id == R.id.itemImport) {
                    intent = new Intent(StudentManagement.this, ImportCSVFile.class);
                } else if (id == R.id.itemExport) {
                    exportStudentsToFile();
                    return true;
                }

                if (intent != null) {
                    startActivity(intent);
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void exportStudentsToFile() {
        databaseReference = FirebaseDatabase.getInstance("https://qlsv-mobile-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("students");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Student> students = new ArrayList<>();
                for (DataSnapshot studentSnapshot : snapshot.getChildren()) {
                    Student student = studentSnapshot.getValue(Student.class);
                    if (student != null) {
                        students.add(student);
                        Log.d("ExportCSV", "Student Added: " + student.getName()); // Confirm each student is added
                    }
                }
                if (!students.isEmpty()) {
                    // Convert the list to a CSV string only if we have students
                    String csvContent = convertToCSV(students);
                    // Write values to a file
                    writeFileToStorage(csvContent, "students.csv");
                } else {
                    Log.d("ExportCSV", "No students found to export.");
                    Toast.makeText(StudentManagement.this, "No students found to export", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to read value.", error.toException());
            }
        });
    }


    private void writeFileToStorage(String content, String fileName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // >= Android 10 and, use app-specific directory without permissions/ cannot find the file
            File file = new File(getExternalFilesDir(null), fileName);
            try (FileOutputStream fos = new FileOutputStream(file);
                 OutputStreamWriter osw = new OutputStreamWriter(fos)) {
                osw.write(content);
                Toast.makeText(this, "CSV Exported to " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                Log.d("ExportCSV", "CSV Exported to " + file.getAbsolutePath());
            } catch (IOException e) {
                Log.e("IOException", e.toString());
                Toast.makeText(this, "Export failed", Toast.LENGTH_SHORT).show();
            }
        } else {
            // below Android 10, access Downloads directory
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
            } else {
                File downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File file = new File(downloadsDirectory, fileName);
                try (FileOutputStream fos = new FileOutputStream(file);
                     OutputStreamWriter osw = new OutputStreamWriter(fos)) {
                    osw.write(content);
                    Toast.makeText(this, "CSV Exported to " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    Log.e("IOException", e.toString());
                    Toast.makeText(this, "Export failed", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private String convertToCSV(List<Student> students) {
        StringBuilder csvBuilder = new StringBuilder();

        csvBuilder.append("ID,Name,Age,Email,Gender,Class\n");
        for (Student student : students) {
            csvBuilder.append(student.getId()).append(",");
            csvBuilder.append(student.getName()).append(",");
            csvBuilder.append(student.getAge()).append(",");
            csvBuilder.append(student.getEmail()).append(",");
            csvBuilder.append(student.getGender()).append(",");
            csvBuilder.append(student.getStudentClass()).append("\n");
            Log.d("ExportCSV", "CSV Line: " + csvBuilder.toString());
        }
        return csvBuilder.toString();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                exportStudentsToFile();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}