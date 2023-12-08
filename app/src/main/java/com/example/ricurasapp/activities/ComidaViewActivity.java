package com.example.ricurasapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ricurasapp.Conexion;
import com.example.ricurasapp.R;

import java.util.Arrays;

public class ComidaViewActivity extends AppCompatActivity {

    Intent i;
    View headerView;
    private TextView textViewTitleUser;
    private TextView textViewTitleRol;
    private TextView csalir;
    ImageView imageViewComidaView;
    TextView textViewComidaViewTitle;
    TextView textViewComidaViewDescripcion;
    Button buttonModificarComida;
    Button buttonEliminarComida;
    private int rolId = 0;
    RequestQueue requestQueue;
    private final String PROTOCOL = Conexion.PROTOCOL;
    private final String IP = Conexion.IP;
    private final String SITIO = Conexion.SITIO;
    private String url = PROTOCOL + IP + "/" + SITIO + "/";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comida_view);
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
        imageViewComidaView = (ImageView) findViewById(R.id.imageViewComidaView);
        textViewComidaViewTitle = (TextView) findViewById(R.id.textViewComidaViewTitle);
        textViewComidaViewDescripcion = (TextView) findViewById(R.id.textViewComidaViewDescripcion);
        buttonModificarComida = (Button) findViewById(R.id.buttonModificarComida);
        buttonEliminarComida = (Button) findViewById(R.id.buttonEliminarComida);
        byte[] imagenBytes = getIntent().getByteArrayExtra("imagen");
        String comidaNombre = getIntent().getStringExtra("nombre").toString();
        String comidaDescripcion = getIntent().getStringExtra("descripcion").toString();
        double comidaPrecio = getIntent().getDoubleExtra("precio", 0.0);
        if (rolId != 1) {
            buttonModificarComida.setVisibility(View.GONE);
            buttonEliminarComida.setVisibility(View.GONE);
        } else {
            System.out.println(getIntent().getIntExtra("id", -1));
            int id = getIntent().getIntExtra("id", -1);
            if (id >= 0) {
                buttonModificarComida.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        modificarComida(url, id, comidaNombre, comidaDescripcion, comidaPrecio, imagenBytes);
                    }
                });
                buttonEliminarComida.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        eliminarComida(url, id);
                    }
                });
            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        }

        if (imagenBytes.length > 0) {
            // Decodificar el arreglo de bytes en un objeto Bitmap
            Bitmap bitmap = BitmapFactory.decodeByteArray(imagenBytes, 0, imagenBytes.length);
            // Asignar el Bitmap a la ImageView
            imageViewComidaView.setImageBitmap(bitmap);
        } else {
            imageViewComidaView.setImageResource(R.drawable.ic_launcher_background);
        }
        textViewComidaViewTitle.setText(comidaNombre);
        textViewComidaViewDescripcion.setText(comidaDescripcion);
    }

    private void modificarComida(String url, int id, String comidaNombre, String comidaDescripcion, double comidaPrecio, byte[] imagenBytes) {
        url = url + "modificar_comida.php?id=" + id;
        i = new Intent(this, ProductFormActivity.class);
        i.putExtra("url", url);
        i.putExtra("id_comida", id);
        i.putExtra("nombre_comida", comidaNombre);
        i.putExtra("descripcion_comida", comidaDescripcion);
        i.putExtra("precio_comida", comidaPrecio);
        i.putExtra("imagen_comida", imagenBytes);
        startActivity(i);
    }

    private void eliminarComida(String url, int id) {
        // Construir la URL completa con el ID del producto a eliminar
        String urlEliminar = url + "eliminar_comida.php?id=" + id;

        // Mostrar un cuadro de diálogo de confirmación
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmación").setMessage("¿Estás seguro de que deseas eliminar esta comida?").setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Realizar la solicitud de eliminación
                realizarSolicitudEliminacion(urlEliminar);
            }
        }).setNegativeButton("Cancelar", null).show();
    }

    private void realizarSolicitudEliminacion(String urlEliminar) {
        // Crear una solicitud GET utilizando Volley
        StringRequest request = new StringRequest(Request.Method.GET, urlEliminar, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Manejar la respuesta del servidor
                Toast.makeText(ComidaViewActivity.this, "Comida eliminada", Toast.LENGTH_SHORT).show();
                // Aquí puedes realizar alguna acción adicional después de eliminar el producto, si es necesario
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Manejar el error de la solicitud
                Toast.makeText(ComidaViewActivity.this, "Error al eliminar el producto", Toast.LENGTH_SHORT).show();
            }
        });

        // Agregar la solicitud a la cola de Volley
        Volley.newRequestQueue(this).add(request);
    }


}