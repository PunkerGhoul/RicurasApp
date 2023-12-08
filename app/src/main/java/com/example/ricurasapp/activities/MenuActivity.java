package com.example.ricurasapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ricurasapp.AdministradorListAdapter;
import com.example.ricurasapp.R;

public class MenuActivity extends AppCompatActivity {

    View headerView;
    Intent i;
    private TextView textViewTitleUser;
    private TextView textViewTitleRol;
    private TextView csalir;
    private ListView listViewMenu;
    private String lista_opciones[];
    private Integer[] imgid;
    private int rolId = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            alertOneButton();
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        headerView = findViewById(R.id.header); // ID del elemento raíz de la cabecera
        textViewTitleUser = (TextView) headerView.findViewById(R.id.nusu);
        textViewTitleRol = (TextView) headerView.findViewById(R.id.nrol);
        csalir = (TextView) headerView.findViewById(R.id.csalir);
        csalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertOneButton();
            }
        });
        final String nombresApellidos = getIntent().getStringExtra("nombres_apellidos") != null ? getIntent().getStringExtra("nombres_apellidos").toString() : "";
        rolId = getIntent().getIntExtra("rol", 0);
        String rol = rolId != 1 ? "Cliente" : "Empleado";
        textViewTitleUser.setText(textViewTitleUser.getText().toString() + " " + nombresApellidos);
        textViewTitleRol.setText(textViewTitleRol.getText().toString() + " " + rol);
        if (rol.equals("Cliente")) {
            lista_opciones = new String[]{"Productos", "Carrito", "Generar Factura", "Perfil"};
            imgid = new Integer[]{R.drawable.productos, R.drawable.carrito_compras, R.drawable.factura, R.drawable.perfil_usuario};
        } else {
            lista_opciones = new String[]{"Productos", "Perfil"};
            imgid = new Integer[]{R.drawable.productos, R.drawable.perfil_usuario};
        }
        AdministradorListAdapter adapter = new AdministradorListAdapter(this, lista_opciones, imgid);
        listViewMenu = (ListView) findViewById(R.id.listViewMenu);
        listViewMenu.setAdapter((ListAdapter) adapter);
        listViewMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = lista_opciones[+position];
                Toast.makeText(getApplicationContext(), selectedItem, Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Ha seleccionado " + selectedItem, Toast.LENGTH_SHORT).show();
                if (position == 0) {
                    i = new Intent(MenuActivity.this, ProductosActivity.class);
                } else if (position == 1) {
                    if (rolId == 1) {
                        i = new Intent(MenuActivity.this, UsuarioActivity.class);
                    } else {
                        i = new Intent(MenuActivity.this, CarritoActivity.class);
                    }
                } else if (position == 2) {
                    i = new Intent(MenuActivity.this, CarritoActivity.class);
                } else if (position == 3) {
                    i = new Intent(MenuActivity.this, UsuarioActivity.class);
                }
                Bundle args = getIntent().getBundleExtra("BUNDLE");
                if (args != null) {
                    i.putExtra("BUNDLE", args);
                }
                i.putExtra("nombres_apellidos", nombresApellidos);
                i.putExtra("id", getIntent().getStringExtra("id"));
                i.putExtra("nombre_usuario", getIntent().getStringExtra("nombre_usuario"));
                i.putExtra("nombres", getIntent().getStringExtra("nombres"));
                i.putExtra("apellidos", getIntent().getStringExtra("apellidos"));
                i.putExtra("rol", rolId);
                startActivity(i);
            }
        });
    }

    //Método que permite confirmar el cierre o no de sesión
    public void alertOneButton() {
        new AlertDialog.Builder(MenuActivity.this).setTitle("Cerrar Sesión").setMessage("Está seguro de salir...?").setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @TargetApi(11)
            public void onClick(DialogInterface dialog, int id) {
                //Intent intent = new Intent(Intent.ACTION_MAIN);
                Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
                //intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                finish();
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @TargetApi(11)
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        }).show();
    }
}