package com.example.ricurasapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AdministradorListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] itemname;
    private Integer[] integers = new Integer[0];

    public AdministradorListAdapter(Activity context, String[] itemname, Integer[] integers) {
        super(context, R.layout.administrador_list_adapter, itemname);
        this.context = context;
        this.itemname = itemname;
        this.integers = integers;
    }

    public AdministradorListAdapter(Activity context, String[] itemname) {
        super(context, R.layout.administrador_list_adapter, itemname);
        this.context = context;
        this.itemname = itemname;
    }

    public View getView(int posicion, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.administrador_list_adapter, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.nombre_item);
        txtTitle.setText(itemname[posicion]);
        if (integers.length != 0) {
            ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
            imageView.setImageResource(integers[posicion]);
        }
        return rowView;
    }
}