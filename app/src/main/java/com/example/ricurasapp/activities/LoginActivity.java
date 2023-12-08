package com.example.ricurasapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.ricurasapp.Conexion;
import com.example.ricurasapp.R;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private final String PROTOCOL = Conexion.PROTOCOL;
    private final String IP = Conexion.IP;
    private final String SITIO = Conexion.SITIO;
    private String url = PROTOCOL + IP + "/" + SITIO + "/";

    private EditText editTextTextPersonName;
    private EditText editTextTextPassword;
    private Button buttonLogin;
    private TextView textViewRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editTextTextPersonName = (EditText) findViewById(R.id.editTextTextPersonName);
        editTextTextPassword = (EditText) findViewById(R.id.editTextTextPassword);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        textViewRegister = (TextView) findViewById(R.id.textViewRegister);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombreUsuario = editTextTextPersonName.getText().toString();
                String contrasena = editTextTextPassword.getText().toString();
                login(nombreUsuario, contrasena);
            }
        });
        textViewRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent j = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(j);
            }
        });
    }

    protected void login(String nombreUsuario, String contrasena) {
        url = url + "login.php?nombre_usuario=" + nombreUsuario + "&contrasena=" + contrasena;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, response -> {
            JSONObject jsonObject;
            for (int i = 0; i < response.length(); i++) {
                try {
                    jsonObject = response.getJSONObject(i);
                    int rolId = Integer.parseInt(jsonObject.getString("rol_id"));
                    if (rolId != 0) {
                        Intent j = new Intent(LoginActivity.this, MenuActivity.class);
                        j.putExtra("id", jsonObject.getString("id"));
                        j.putExtra("nombres", jsonObject.getString("nombres"));
                        j.putExtra("apellidos", jsonObject.getString("apellidos"));
                        j.putExtra("nombre_usuario", jsonObject.getString("nombre_usuario"));
                        j.putExtra("nombres_apellidos", jsonObject.getString("nombres") + " " + jsonObject.getString("apellidos"));
                        j.putExtra("rol", rolId);
                        startActivity(j);
                        Toast.makeText(getApplicationContext(), "INICIANDO SESIÓN", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "CREDENCIALES INCORRECTAS", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, error -> {
            Toast.makeText(getApplicationContext(), "ERROR DE CONEXION",
                    Toast.LENGTH_SHORT).show();
            System.out.println(error.toString());
        }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
        requestQueue.start();
    }
}