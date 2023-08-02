package com.example.towncenterstore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.towncenterstore.R;

import java.util.ArrayList;

public class CitiesAdapter extends ArrayAdapter<String> {
    public CitiesAdapter(@NonNull Context context, ArrayList<String> citiesArrayList) {
        super(context, 0 , citiesArrayList);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return iniView(position,convertView,parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return iniView(position,convertView,parent);
    }

    private View iniView(int position, View convertView, ViewGroup parent){
        if (convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_item,parent,false);
        }
        TextView name = convertView.findViewById(R.id.tvCityName);

        if (getItem(position)!= null) {
            name.setText(getItem(position));
        }
        return convertView;
    }
}
