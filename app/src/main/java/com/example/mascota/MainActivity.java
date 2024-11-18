package com.example.mascota;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // Declaración de variables
    private FirebaseFirestore db; // Instancia de Firestore
    private EditText etCodigo, etNombreMascota, etNombreDueno, etDireccion; // Campos del formulario

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Configuración de Edge-to-Edge
        setContentView(R.layout.activity_main);

        // Configuración para ajustar márgenes con WindowInsets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.txtNombreMascota), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Enlazar los campos del formulario con los EditText del layout
        etCodigo = findViewById(R.id.txtCodigoChip);
        etNombreMascota = findViewById(R.id.txtNombreMascota);
        etNombreDueno = findViewById(R.id.txtNombreDuenio);
        etDireccion = findViewById(R.id.txtDireccion);
    }

    // Método para enviar los datos a Firestore
    public void enviarDatosFirestore(View view) {
        // Obtener los datos ingresados por el usuario
        String codigo = etCodigo.getText().toString().trim();
        String nombreMascota = etNombreMascota.getText().toString().trim();
        String nombreDueno = etNombreDueno.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();

        // Validar que no haya campos vacíos
        if (codigo.isEmpty() || nombreMascota.isEmpty() || nombreDueno.isEmpty() || direccion.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear un mapa con los datos
        Map<String, Object> mascota = new HashMap<>();
        mascota.put("codigo", codigo);
        mascota.put("nombreMascota", nombreMascota);
        mascota.put("nombreDueno", nombreDueno);
        mascota.put("direccion", direccion);

        // Enviar los datos a Firestore
        db.collection("mascotas")
                .add(mascota)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Datos enviados correctamente", Toast.LENGTH_SHORT).show();
                    // Limpiar los campos del formulario
                    etCodigo.setText("");
                    etNombreMascota.setText("");
                    etNombreDueno.setText("");
                    etDireccion.setText("");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al enviar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
