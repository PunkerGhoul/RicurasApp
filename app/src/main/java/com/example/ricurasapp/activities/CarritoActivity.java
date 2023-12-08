package com.example.ricurasapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ricurasapp.Comida;
import com.example.ricurasapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CarritoActivity extends AppCompatActivity {

    View headerView;
    private TextView textViewTitleUser;
    private TextView textViewTitleRol;
    private TextView csalir;
    private TextView textViewTotalPrice;
    private ListView listViewCarrito;
    private ArrayList<Comida> carrito = new ArrayList<>();
    private String nombresApellidos = "";
    private int rolId = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent i = new Intent(CarritoActivity.this, MenuActivity.class);
            if (!carrito.isEmpty()) {
                i = new Intent(CarritoActivity.this, ProductosActivity.class);
            }
            i.putExtra("nombres_apellidos", nombresApellidos);
            Bundle args = new Bundle();
            args.putSerializable("carrito", (Serializable) carrito);
            i.putExtra("BUNDLE", args);
            startActivity(i);
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);
        headerView = findViewById(R.id.header); // ID del elemento raíz de la cabecera
        textViewTitleUser = (TextView) headerView.findViewById(R.id.nusu);
        textViewTitleRol = (TextView) headerView.findViewById(R.id.nrol);
        csalir = (TextView) headerView.findViewById(R.id.csalir);
        csalir.setVisibility(View.GONE);
        nombresApellidos = getIntent().getStringExtra("nombres_apellidos") != null ? getIntent().getStringExtra("nombres_apellidos").toString() : "";
        rolId = getIntent().getIntExtra("rol", 0);
        String rol = rolId != 1 ? "Cliente" : "Empleado";
        textViewTitleUser.setText(textViewTitleUser.getText().toString() + " " + nombresApellidos);
        textViewTitleRol.setText(textViewTitleRol.getText().toString() + " " + rol);
        listViewCarrito = (ListView) findViewById(R.id.listViewCarrito);
        if (carrito != null) {
            textViewTotalPrice = (TextView) findViewById(R.id.textViewTotalPrice);
            Bundle args = getIntent().getBundleExtra("BUNDLE");
            if (args != null) {
                carrito = (ArrayList<Comida>) args.getSerializable("carrito");
                listViewCarrito.setAdapter(new CarritoActivity.MyListAdapter(this, R.layout.list_item_carrito, carrito));
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    List<Double> pricesList = carrito.stream().map((comida) -> comida.getPrecio() * comida.getCantidad()).collect(Collectors.toList());
                    textViewTotalPrice.append(String.valueOf(pricesList.stream().mapToDouble(f -> f).sum()));
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    private class MyListAdapter extends ArrayAdapter<Comida> {
        private final int layout;
        private List<Comida> mObjects;

        private MyListAdapter(Context context, int resource, List<Comida> objects) {
            super(context, resource, objects);
            mObjects = objects;
            layout = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            CarritoActivity.ViewHolder mainViewholder = null;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                CarritoActivity.ViewHolder viewHolder = new CarritoActivity.ViewHolder();
                viewHolder.textViewNameItem = convertView.findViewById(R.id.textViewNameItem);
                viewHolder.textViewPriceLabel = convertView.findViewById(R.id.textViewPrice);
                viewHolder.textViewPriceValue = convertView.findViewById(R.id.textViewPriceValue);
                viewHolder.editTextNumber = convertView.findViewById(R.id.editTextNumber);
                viewHolder.textViewCarritoPrecioLote = convertView.findViewById(R.id.textViewCarritoPrecioLote);
                viewHolder.floatingActionButtonDeleteProduct = convertView.findViewById(R.id.floatingActionButtonDeleteProduct);
                convertView.setTag(viewHolder);
            }
            mainViewholder = (CarritoActivity.ViewHolder) convertView.getTag();
            CarritoActivity.ViewHolder finalMainViewholder = mainViewholder;
            mainViewholder.textViewNameItem.setText(getItem(position).getNombre());
            mainViewholder.textViewPriceValue.setText(String.valueOf(getItem(position).getPrecio()));
            mainViewholder.editTextNumber.setText(String.valueOf(getItem(position).getCantidad()));
            double totalPriceProd = getItem(position).getPrecio() * getItem(position).getCantidad();
            mainViewholder.textViewCarritoPrecioLote.setText(String.valueOf(totalPriceProd));
            mainViewholder.floatingActionButtonDeleteProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    carrito.remove(position);
                    notifyDataSetChanged();
                }
            });
            return convertView;
        }
    }

    public static class ViewHolder {
        TextView textViewNameItem;
        TextView textViewPriceLabel;
        TextView textViewPriceValue;
        EditText editTextNumber;
        TextView textViewCarritoPrecioLote;
        FloatingActionButton floatingActionButtonDeleteProduct;
    }
}