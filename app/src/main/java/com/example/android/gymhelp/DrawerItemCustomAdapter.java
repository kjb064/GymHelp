package com.example.android.gymhelp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class DrawerItemCustomAdapter extends ArrayAdapter<DataModel> {

    private Context mContext;
    private int layoutResourceId;
    private List<DataModel> data;

    public DrawerItemCustomAdapter(Context mContext, int layoutResourceId, List<DataModel> data) {

        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItem = convertView;

        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        listItem = inflater.inflate(layoutResourceId, parent, false);

        ImageView imageViewIcon = listItem.findViewById(R.id.imageViewIcon);
        TextView textViewName = listItem.findViewById(R.id.program_name);

        DataModel folder = data.get(position);

        if (folder.icon > 0) {
            imageViewIcon.setImageResource(folder.icon);
        }

        textViewName.setText(folder.name);
        return listItem;
    }
}
