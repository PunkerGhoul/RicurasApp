package com.example.ricurasapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ricurasapp.AdministradorListAdapter;
import com.example.ricurasapp.Comida;
import com.example.ricurasapp.R;

import java.io.Serializable;
import java.util.ArrayList;

public class ProductosActivity extends AppCompatActivity {

    View headerView;
    Intent i;
    private TextView textViewTitleUser;
    private TextView textViewTitleRol;
    private TextView csalir;
    private ListView listViewProductos;
    private String[] lista_opciones = {"Comidas Rápidas", "Comidas Típicas"};
    private ArrayList<Comida> carrito = new ArrayList<>();
    private int rolId = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Bundle args = new Bundle();
            if (!carrito.isEmpty()) {
                i = new Intent(ProductosActivity.this, MenuActivity.class);
                args.putSerializable("carrito", (Serializable) carrito);
                i.putExtra("BUNDLE", args);
                startActivity(i);
            } else {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                finish();
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);
        headerView = findViewById(R.id.header); // ID del elemento raíz de la cabecera
        textViewTitleUser = (TextView) headerView.findViewById(R.id.nusu);
        textViewTitleRol = (TextView) headerView.findViewById(R.id.nrol);
        csalir = (TextView) headerView.findViewById(R.id.csalir);
        csalir.setVisibility(View.GONE);
        final String nombresApellidos = getIntent().getStringExtra("nombres_apellidos") != null ? getIntent().getStringExtra("nombres_apellidos").toString() : "";
        rolId = getIntent().getIntExtra("rol", 0);
        String rol = rolId != 1 ? "Cliente" : "Empleado";
        textViewTitleUser.setText(textViewTitleUser.getText().toString() + " " + nombresApellidos);
        textViewTitleRol.setText(textViewTitleRol.getText().toString() + " " + rol);
        Bundle args = getIntent().getBundleExtra("BUNDLE");
        if (args != null) {
            carrito = (ArrayList<Comida>) args.getSerializable("carrito");
        }
        AdministradorListAdapter adapter = new AdministradorListAdapter(this, lista_opciones);
        listViewProductos = (ListView) findViewById(R.id.listViewProductos);
        listViewProductos.setAdapter((ListAdapter) adapter);
        listViewProductos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = lista_opciones[+position];
                Toast.makeText(getApplicationContext(), selectedItem, Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Ha seleccionado " + selectedItem, Toast.LENGTH_SHORT).show();
                i = new Intent(ProductosActivity.this, ComidaActivity.class);
                int rolId = getIntent().getIntExtra("rol", 0);
                i.putExtra("titulo", selectedItem);
                i.putExtra("nombres_apellidos", nombresApellidos);
                i.putExtra("rol", rolId);
                if (!carrito.isEmpty()) {
                    Bundle args = new Bundle();
                    args.putSerializable("carrito", (Serializable) carrito);
                    i.putExtra("BUNDLE", args);
                }
                startActivity(i);
            }
        });
    }
}