package com.example.ricurasapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ricurasapp.Conexion;
import com.example.ricurasapp.R;

import org.json.JSONException;
import org.json.JSONObject;

public class UsuarioActivity extends AppCompatActivity {

    private final String PROTOCOL = Conexion.PROTOCOL;
    private final String IP = Conexion.IP;
    private final String SITIO = Conexion.SITIO;
    private String url = PROTOCOL + IP + "/" + SITIO + "/";
    private EditText editTextNombres;
    private EditText editTextApellidos;
    private EditText editTextNombreUsuario;
    private EditText editTextContrasena;
    private TextView textViewSpinnerRol;
    private Spinner spinnerRol;
    private Button buttonGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);

        // Inicializar variables buscando por su ID
        editTextNombres = findViewById(R.id.editTextNombres);
        editTextNombres.setText(getIntent().getStringExtra("nombres"));
        editTextApellidos = findViewById(R.id.editTextApellidos);
        editTextApellidos.setText(getIntent().getStringExtra("apellidos"));
        editTextNombreUsuario = findViewById(R.id.editTextNombreUsuario);
        editTextNombreUsuario.setText(getIntent().getStringExtra("nombre_usuario"));
        editTextContrasena = findViewById(R.id.editTextContrasena);
        textViewSpinnerRol = findViewById(R.id.textViewSpinnerRol);
        spinnerRol = findViewById(R.id.spinnerRol);
        if (getIntent().getIntExtra("rol", 0) != 1) {
            textViewSpinnerRol.setVisibility(View.GONE);
            spinnerRol.setVisibility(View.GONE);
        }
        buttonGuardar = findViewById(R.id.buttonGuardar);

        // Agregar el listener al botón "Guardar"
        buttonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarUsuario();
            }
        });
    }

    private void actualizarUsuario() {
        // Obtener los datos ingresados por el usuario
        String nombres = editTextNombres.getText().toString();
        String apellidos = editTextApellidos.getText().toString();
        String nombreUsuario = editTextNombreUsuario.getText().toString();
        String contrasena = editTextContrasena.getText().toString();
        long rol = spinnerRol.getSelectedItemId();
        int rolIdUsuario = getIntent().getIntExtra("rol", 0);
        int rolId = 0;
        if (rolIdUsuario == 0) {
            rolId = rol == 0 ? 1 : 2;
        } else {
            rolId = rolIdUsuario;
        }

        // Crear el objeto JSON con los datos del usuario
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id", getIntent().getStringExtra("id"));
            jsonBody.put("nombres", nombres);
            jsonBody.put("apellidos", apellidos);
            jsonBody.put("nombre_usuario", nombreUsuario);
            jsonBody.put("contrasena", contrasena);
            jsonBody.put("rol_id", rolId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        url = url + "modificar_usuario.php";

        // Realizar la petición POST usando Volley
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Mostrar mensaje de éxito
                        System.out.println(response);
                        Toast.makeText(UsuarioActivity.this, "Usuario actualizado correctamente", Toast.LENGTH_SHORT).show();
                        // Finalizar la actividad
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Procesar el error de la respuesta del servidor
                        Toast.makeText(UsuarioActivity.this, "Error al actualizar el usuario", Toast.LENGTH_SHORT).show();
                    }
                });

        // Agregar la solicitud a la cola de solicitudes de Volley
        requestQueue.add(request);
    }
}
