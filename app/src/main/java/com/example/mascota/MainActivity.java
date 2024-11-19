package com.example.mascota;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // Declaración de variables
    private FirebaseFirestore db; // Instancia de Firestore
    private EditText etCodigo, etNombreMascota, etNombreDueno, etDireccion; // Campos del formulario
    private ListView listaMascotas; // ListView para mostrar los datos

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

        // Vincular el ListView
        listaMascotas = findViewById(R.id.lista);
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

    // Método para cargar los datos desde Firestore
    public void CargarLista(View view) {
        // Lista para almacenar los datos obtenidos
        ArrayList<String> listaDatos = new ArrayList<>();

        // Consultar Firestore
        db.collection("mascotas")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Recorrer los documentos obtenidos
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String codigo = document.getString("codigo");
                        String nombreMascota = document.getString("nombreMascota");
                        String nombreDueno = document.getString("nombreDueno");
                        String direccion = document.getString("direccion");

                        // Formatear los datos y agregarlos a la lista
                        listaDatos.add("Código: " + codigo + "\nMascota: " + nombreMascota +
                                "\nDueño: " + nombreDueno + "\nDirección: " + direccion);
                    }

                    // Crear un adaptador y vincularlo al ListView
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            this, android.R.layout.simple_list_item_1, listaDatos
                    );
                    listaMascotas.setAdapter(adapter);

                    // Mostrar un mensaje si no hay datos
                    if (listaDatos.isEmpty()) {
                        Toast.makeText(this, "No hay datos para mostrar", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}

