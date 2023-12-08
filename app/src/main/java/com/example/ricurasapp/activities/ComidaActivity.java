package com.example.ricurasapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.ricurasapp.Comida;
import com.example.ricurasapp.Conexion;
import com.example.ricurasapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ComidaActivity extends AppCompatActivity {

    View headerView;
    private TextView textViewTitleUser;
    private TextView textViewTitleRol;
    private TextView csalir;
    RequestQueue requestQueue;
    private final String PROTOCOL = Conexion.PROTOCOL;
    private final String IP = Conexion.IP;
    private final String SITIO = Conexion.SITIO;
    private String url = PROTOCOL + IP + "/" + SITIO + "/";
    private TextView textViewComidaTitle;
    private ListView listViewComidas;
    private ArrayList<Comida> data = new ArrayList<>();
    Button buttonAddCarrito;
    private ArrayList<Comida> carrito = new ArrayList<>();
    private int rolId = 0;
    private String nombresApellidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comida);
        headerView = findViewById(R.id.headerComida); // ID del elemento raíz de la cabecera
        textViewTitleUser = (TextView) headerView.findViewById(R.id.nusu);
        textViewTitleRol = (TextView) headerView.findViewById(R.id.nrol);
        csalir = (TextView) headerView.findViewById(R.id.csalir);
        csalir.setVisibility(View.GONE);
        nombresApellidos = getIntent().getStringExtra("nombres_apellidos") != null ? getIntent().getStringExtra("nombres_apellidos").toString() : "";
        rolId = getIntent().getIntExtra("rol", 0);
        String rol = rolId != 1 ? "Cliente" : "Empleado";
        textViewTitleUser.setText(textViewTitleUser.getText().toString() + " " + nombresApellidos);
        textViewTitleRol.setText(textViewTitleRol.getText().toString() + " " + rol);
        buttonAddCarrito = (Button) findViewById(R.id.buttonActionProducts);
        textViewComidaTitle = (TextView) findViewById(R.id.textViewComidaTitle);
        textViewComidaTitle.setText(getIntent().getStringExtra("titulo").toString());
        listViewComidas = (ListView) findViewById(R.id.listViewComidas);
        String filter = getIntent().getStringExtra("titulo").toString().equals("Comidas Rápidas") ? "rapidas" : "";
        if (rolId == 1) {
            buttonAddCarrito.setText("Añadir producto");
        } else {
            buttonAddCarrito.setText("Añadir al carrito");
        }
        listarComidas(url + "consultar_comidas.php?filter=" + filter);
        buttonAddCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rolId == 1) {
                    addProduct(filter);
                } else {
                    addToCarrito();
                }
            }
        });
    }

    private void listarComidas(String url) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, response -> {
            JSONObject jsonObject;
            for (int i = 0; i < response.length(); i++) {
                try {
                    jsonObject = response.getJSONObject(i);
                    int id = Integer.parseInt(jsonObject.getString("id"));
                    String nombre = jsonObject.getString("nombre");
                    String imagenB64 = jsonObject.getString("imagen");
                    // Decodificar la imagen Base64 a un arreglo de bytes
                    byte[] imagen = Base64.decode(imagenB64, Base64.DEFAULT);
                    String descripcion = jsonObject.getString("descripcion");
                    double precio = Double.parseDouble(jsonObject.getString("precio"));
                    data.add(new Comida(id, nombre, imagen, descripcion, precio, 0));
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
            listViewComidas.setAdapter(new MyListAdapter(this, R.layout.list_item_comida, data));
        }, error -> Toast.makeText(getApplicationContext(), "ERROR DE CONEXION", Toast.LENGTH_SHORT).show());
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private void addProduct(String tipoComida) {
        Intent i = new Intent(ComidaActivity.this, ProductFormActivity.class);
        i.putExtra("tipoComida", tipoComida);
        startActivity(i);
    }

    private void addToCarrito() {
        Bundle argsP = getIntent().getBundleExtra("BUNDLE");
        if (argsP != null) {
            carrito = (ArrayList<Comida>) argsP.getSerializable("carrito");
        }
        for (Comida comida : data) {
            if (comida.getCantidad() > 0) {
                carrito.add(comida);
            }
        }
        Intent i = new Intent(ComidaActivity.this, CarritoActivity.class);
        Bundle args = new Bundle();
        args.putSerializable("carrito", (Serializable) carrito);
        i.putExtra("BUNDLE", args);
        startActivity(i);
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
            ViewHolder mainViewholder = null;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.textViewNameItem = convertView.findViewById(R.id.textViewNameItem);
                viewHolder.floatingActionButtonInfo = convertView.findViewById(R.id.floatingActionButtonInfo);
                viewHolder.textViewPriceLabel = convertView.findViewById(R.id.textViewPrice);
                viewHolder.textViewPriceValue = convertView.findViewById(R.id.textViewPriceValue);
                viewHolder.editTextNumber = convertView.findViewById(R.id.editTextNumber);
                viewHolder.floatingActionButtonSubs = convertView.findViewById(R.id.floatingActionButtonSubs);
                viewHolder.floatingActionButtonAdds = convertView.findViewById(R.id.floatingActionButtonAdds);
                if (rolId == 1) {
                    viewHolder.editTextNumber.setVisibility(View.GONE);
                    viewHolder.floatingActionButtonSubs.setVisibility(View.GONE);
                    viewHolder.floatingActionButtonAdds.setVisibility(View.GONE);
                }
                convertView.setTag(viewHolder);
            }
            mainViewholder = (ViewHolder) convertView.getTag();
            ViewHolder finalMainViewholder = mainViewholder;
            mainViewholder.floatingActionButtonInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(ComidaActivity.this, ComidaViewActivity.class);
                    i.putExtra("nombres_apellidos", nombresApellidos);
                    i.putExtra("rol", rolId);
                    i.putExtra("id", data.get(position).getId());
                    i.putExtra("nombre", data.get(position).getNombre());
                    i.putExtra("imagen", data.get(position).getImagen());
                    i.putExtra("descripcion", data.get(position).getDescripcion());
                    i.putExtra("precio", data.get(position).getPrecio());
                    startActivity(i);
                }
            });
            mainViewholder.floatingActionButtonSubs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int num = Integer.parseInt(String.valueOf(finalMainViewholder.editTextNumber.getText()));
                        if (num > 0) {
                            num -= 1;
                            finalMainViewholder.editTextNumber.setText(String.valueOf(num));
                            data.get(position).setCantidad(num);
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(ComidaActivity.this, "El número no es válido", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            mainViewholder.floatingActionButtonAdds.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int num = Integer.parseInt(String.valueOf(finalMainViewholder.editTextNumber.getText()));
                        num += 1;
                        finalMainViewholder.editTextNumber.setText(String.valueOf(num));
                        data.get(position).setCantidad(num);
                    } catch (NumberFormatException e) {
                        Toast.makeText(ComidaActivity.this, "El número no es válido", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            mainViewholder.textViewNameItem.setText(getItem(position).getNombre());
            mainViewholder.textViewPriceValue.setText(String.valueOf(getItem(position).getPrecio()));
            return convertView;
        }
    }

    public static class ViewHolder {
        TextView textViewNameItem;
        TextView textViewPriceLabel;
        TextView textViewPriceValue;
        EditText editTextNumber;
        FloatingActionButton floatingActionButtonSubs;
        FloatingActionButton floatingActionButtonAdds;
        FloatingActionButton floatingActionButtonInfo;
    }
}