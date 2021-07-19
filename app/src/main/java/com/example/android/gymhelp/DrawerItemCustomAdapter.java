package com.example.android.gymhelp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class DrawerItemCustomAdapter extends ArrayAdapter<String> {

    private final Context mContext;
    private final int layoutResourceId;
    private final List<String> data;

    public DrawerItemCustomAdapter(Context mContext, int layoutResourceId, List<String> data) {
        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
        }

        TextView textViewName = convertView.findViewById(R.id.program_name);
        textViewName.setText(data.get(position));
        return convertView;
    }
}