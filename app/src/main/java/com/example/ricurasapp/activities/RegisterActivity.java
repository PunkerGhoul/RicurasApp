package com.example.ricurasapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ricurasapp.Conexion;
import com.example.ricurasapp.R;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private final String PROTOCOL = Conexion.PROTOCOL;
    private final String IP = Conexion.IP;
    private final String SITIO = Conexion.SITIO;
    private String url = PROTOCOL + IP + "/" + SITIO + "/";
    private EditText editTextUsername;
    private EditText editTextFirstName;
    private EditText editTextLastName;
    private Spinner spinnerRole;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private Button buttonRegister;
    private Button buttonCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        spinnerRole = findViewById(R.id.spinnerRole);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        buttonRegister = findViewById(R.id.buttonRegister);
        buttonCancel = findViewById(R.id.buttonCancel);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = editTextUsername.getText().toString();
                String firstName = editTextFirstName.getText().toString();
                String lastName = editTextLastName.getText().toString();
                String password = editTextPassword.getText().toString();
                String confirmPassword = editTextConfirmPassword.getText().toString();
                int rolId = spinnerRole.getSelectedItemId() == 0 ? 1 : 2;

                if (firstName.isEmpty() || lastName.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    // Alguno de los campos está vacío
                    Toast.makeText(RegisterActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                } else {
                    // Todos los campos están llenos
                    if (password.equals(confirmPassword)) {
                        // Construir el objeto JSON con los datos
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("nombre_usuario", userName);
                            jsonObject.put("nombres", firstName);
                            jsonObject.put("apellidos", lastName);
                            jsonObject.put("contrasena", password);
                            jsonObject.put("rol_id", rolId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        url = url + "crear_usuario.php";

                        // Crear la solicitud POST
                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        // Manejar la respuesta del servidor (registro exitoso)
                                        Toast.makeText(RegisterActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // Manejar el error de la solicitud
                                        Toast.makeText(RegisterActivity.this, "Error en la solicitud", Toast.LENGTH_SHORT).show();
                                    }
                                });

                        // Agregar la solicitud a la cola de solicitudes de Volley
                        Volley.newRequestQueue(RegisterActivity.this).add(request);
                    } else {
                        // Las contraseñas no coinciden
                        Toast.makeText(RegisterActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}
