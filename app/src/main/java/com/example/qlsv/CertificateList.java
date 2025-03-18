package com.example.qlsv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;

public class CertificateList extends AppCompatActivity {
    private ListView listView;
    private List<Certificate> certificates;
    private CertificateAdapter certificateAdapter;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate_list);
        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        listView = findViewById(R.id.listView);
        certificates = new ArrayList<>();
        certificateAdapter = new CertificateAdapter(this, certificates);
        listView.setAdapter(certificateAdapter);

        String id = getIntent().getStringExtra("id");
        databaseReference = FirebaseDatabase.getInstance("https://qlsv-mobile-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("students/" + id);
        databaseReference.child("certificates").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                certificates.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Certificate certificate = snapshot.getValue(Certificate.class);
                    certificates.add(certificate);
                }

                certificateAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Certificate selectedCertificate = certificateAdapter.getItem(position);
                if (selectedCertificate != null) {
                    String name = selectedCertificate.getName();
                    Intent intent = new Intent(CertificateList.this, CertificateInfor.class);
                    intent.putExtra("sId", getIntent().getStringExtra("id"));
                    intent.putExtra("name", name);
                    intent.putExtra("role", getIntent().getStringExtra("role"));
                    startActivity(intent);
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (getIntent().getStringExtra("role").equalsIgnoreCase("employee")) {
                    StyleableToast.makeText(CertificateList.this, "You do not have permission", R.style.toast_custom).show();
                } else {
                    showDeleteConfirmationDialog(position);
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_add) {
            if (getIntent().getStringExtra("role").equalsIgnoreCase("employee")) {
                StyleableToast.makeText(CertificateList.this, "You do not have permission", R.style.toast_custom).show();
            } else {
                Intent intent = new Intent(CertificateList.this, AddCertificate.class);
                intent.putExtra("id", getIntent().getStringExtra("id"));
                startActivity(intent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete?");
        builder.setMessage("Do you want to delete this certificate?");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteCertificate(position);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteCertificate(int position) {
        String id = getIntent().getStringExtra("id");
        DatabaseReference certificatesRef = FirebaseDatabase.getInstance().getReference().child("students/" + id).child("certificates");
        certificatesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    ArrayList<String> certificates = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        certificates.add(snapshot.getKey());
                    }

                    if (position >= 0 && position < certificates.size()) {
                        String certificateToDelete = certificates.get(position);
                        certificatesRef.child(certificateToDelete).removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(CertificateList.this, "Deleted successfully!!!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(CertificateList.this, "Error deleting certificate", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(CertificateList.this, "Invalid position", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CertificateList.this, "Error accessing database", Toast.LENGTH_SHORT).show();
            }
        });
    }
}