package com.example.qlsv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CertificateAdapter extends ArrayAdapter<Certificate> {
    private Context context;
    private List<Certificate> certificates;

    public CertificateAdapter(Context context, List<Certificate> certificates) {
        super(context, 0, certificates);
        this.context = context;
        this.certificates = certificates;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_student, parent, false);
        }

        TextView tvName = convertView.findViewById(R.id.tvName);

        Certificate certificate = certificates.get(position);
        tvName.setText(certificate.getName());

        return convertView;
    }
}
