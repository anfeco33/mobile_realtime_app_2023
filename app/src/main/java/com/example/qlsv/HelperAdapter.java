package com.example.qlsv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class HelperAdapter extends ArrayAdapter<Helper> {
    private Context context;
    private List<Helper> helpers;

    public HelperAdapter(Context context, List<Helper> helpers) {
        super(context, 0, helpers);
        this.context = context;
        this.helpers = helpers;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_user, parent, false);
        }

        TextView tvUsername = convertView.findViewById(R.id.tvUsername);

        Helper helper = helpers.get(position);
        tvUsername.setText(helper.getFullname());

        return convertView;
    }
}
