package com.example.ricurasapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

public class ProductFormActivity extends AppCompatActivity {

    private final String PROTOCOL = Conexion.PROTOCOL;
    private final String IP = Conexion.IP;
    private final String SITIO = Conexion.SITIO;
    private String url = PROTOCOL + IP + "/" + SITIO + "/";
    private TextView textViewTitleAddProduct;
    private EditText editTextNombre;
    private EditText editTextDescripcion;
    private EditText editTextPrice;
    private TextView textViewTipoComida;
    private Spinner spinnerTiposComidas;
    private Button buttonSelectImg;
    private ImageView imageView;
    private String tipoComida = "";
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_PERMISSION = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_form);
        textViewTipoComida = (TextView) findViewById(R.id.textViewTipoComida);
        spinnerTiposComidas = (Spinner) findViewById(R.id.spinnerTiposComidas);
        url = getIntent().getStringExtra("url") == null ? url + "subir_comidas.php" : getIntent().getStringExtra("url").toString();
        if (url.contains("modificar_comida.php")) {
            textViewTitleAddProduct = (TextView) findViewById(R.id.textViewTitleAddProduct);
            textViewTitleAddProduct.setText(textViewTitleAddProduct.getText().toString().replace("Añadir", "Modificar"));
        } else {
            textViewTipoComida.setVisibility(View.GONE);
            spinnerTiposComidas.setVisibility(View.GONE);
        }
        String nombreComida = getIntent().getStringExtra("nombre_comida") != null ? getIntent().getStringExtra("nombre_comida") : "";
        String descripcionComida = getIntent().getStringExtra("descripcion_comida") != null ? getIntent().getStringExtra("descripcion_comida") : "";
        byte[] imagenComida = getIntent().getByteArrayExtra("imagen_comida") != null ? getIntent().getByteArrayExtra("imagen_comida") : null;
        double precioComida = getIntent().getDoubleExtra("precio_comida", 0.0);
        tipoComida = getIntent().getStringExtra("tipoComida");
        editTextNombre = (EditText) findViewById(R.id.editTextNombre);
        if (!Objects.equals(nombreComida, "")) {
            editTextNombre.setText(nombreComida);
        }
        editTextDescripcion = (EditText) findViewById(R.id.editTextDescripcion);
        if (!Objects.equals(descripcionComida, "")) {
            editTextDescripcion.setText(descripcionComida);
        }
        editTextPrice = (EditText) findViewById(R.id.editTextPrice);
        editTextPrice.setText(String.valueOf(precioComida));
        buttonSelectImg = findViewById(R.id.buttonSelectImg);
        Button buttonGuardar = findViewById(R.id.buttonGuardar);
        buttonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = getIntent().getIntExtra("id_comida", -1);
                String nombre = editTextNombre.getText().toString();
                String descripcion = editTextDescripcion.getText().toString();
                double price = Double.parseDouble(editTextPrice.getText().toString());
                long tipoComidaSelect = spinnerTiposComidas.getSelectedItemId();
                int idTipoComida = tipoComidaSelect == 0 ? 1 : 2;
                guardarComida(url, id, nombre, descripcion, price, idTipoComida);
            }
        });
        imageView = findViewById(R.id.imageView);

        // Configurar el ActivityResultLauncher para la selección de imagen
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri selectedImageUri = result.getData().getData();

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonSelectImg.setOnClickListener(v -> selectImageFromGallery());

        // Mostrar la imagen pasada por el intent, si no es nula
        if (imagenComida != null && imagenComida.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imagenComida, 0, imagenComida.length);
            imageView.setImageBitmap(bitmap);
        }
    }

    private void selectImageFromGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Forzar la actualización de la caché de medios
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, MediaStore.Images.Media.EXTERNAL_CONTENT_URI));

            // Los permisos ya han sido otorgados, lanzar la actividad para seleccionar la imagen
            //Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.getContentUri("external"));
            imagePickerLauncher.launch(intent);
        } else {
            // Los permisos no han sido otorgados, solicitar al usuario que los acepte
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        }
    }

    // Método para manejar la respuesta de la solicitud de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Forzar la actualización de la caché de medios
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, MediaStore.Images.Media.EXTERNAL_CONTENT_URI));

                // El usuario ha aceptado los permisos, lanzar la actividad para seleccionar la imagen
                //Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.getContentUri("external"));
                imagePickerLauncher.launch(intent);
            } else {
                // El usuario ha rechazado los permisos, mostrar un mensaje o realizar alguna otra acción
                Toast.makeText(this, "Los permisos son necesarios para seleccionar una imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void guardarComida(String url, int id, String nombre, String descripcion, double precio, int tipoComidaId) {
        // Obtener la imagen seleccionada como un array de bytes
        byte[] imageBytes = convertImageToByteArray();

        // Convertir la imagen a Base64
        String imagenBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        // Crear un objeto JSON con los datos de la comida
        JSONObject jsonBody = new JSONObject();
        try {
            if (id > -1) {
                jsonBody.put("id_comida", id);
            }
            jsonBody.put("nombre", nombre);
            jsonBody.put("imagen", imagenBase64);
            jsonBody.put("descripcion", descripcion);
            jsonBody.put("precio", precio);
            jsonBody.put("tipo_comida_id", tipoComidaId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Crear una solicitud POST con el objeto JSON como cuerpo
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Manejar la respuesta del servidor
                //String message = response.getString("message");
                Toast.makeText(ProductFormActivity.this, "Comida Guardada", Toast.LENGTH_SHORT).show();
                // Aquí puedes realizar alguna acción adicional después de guardar la comida, si es necesario
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Manejar el error de la solicitud
                error.printStackTrace();
                Toast.makeText(ProductFormActivity.this, "Error al guardar la comida", Toast.LENGTH_SHORT).show();
            }
        });

        // Agregar la solicitud a la cola de Volley
        Volley.newRequestQueue(this).add(request);
    }

    private byte[] convertImageToByteArray() {
        // Obtener la imagen del ImageView
        Drawable drawable = imageView.getDrawable();
        Bitmap bitmap = null;
        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        }

        // Convertir la imagen a un array de bytes
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
