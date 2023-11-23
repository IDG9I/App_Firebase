package com.example.app_firebase;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_firebase.Luchador;
import com.example.app_firebase.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private EditText editTextIdentifier, editTextName;
    private Button btnRegistrar, btnModificar, btnEliminar, btnBuscar;
    private TextView textViewListado;
    private RecyclerView recyclerView;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtener referencias a los elementos de la interfaz de usuario
        editTextIdentifier = findViewById(R.id.editTextText2);
        editTextName = findViewById(R.id.editTextText3);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnModificar = findViewById(R.id.btnModificar);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnBuscar = (Button) findViewById(R.id.btnBuscar);
        textViewListado = findViewById(R.id.textView5);

        recyclerView = findViewById(R.id.recyclerView);

        // Obtener referencia a la base de datos de Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("luchadores");

        // Configurar el evento de clic para el botón de registro
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarLuchador();
            }
        });

        // Configurar el evento de clic para el botón de modificación
        btnModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modificarLuchador();
            }
        });

        // Configurar el evento de clic para el botón de eliminación
        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eliminarLuchador();
            }
        });

        // Configurar el evento de clic para el botón de búsqueda
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscarLuchador();
            }
        });

        // Mostrar el listado de luchadores al iniciar la actividad
        mostrarListadoLuchadores();
    }

    private void registrarLuchador() {
        // Obtener los valores ingresados por el usuario
        String identifier = editTextIdentifier.getText().toString().trim();
        String name = editTextName.getText().toString().trim();

        // Verificar si se ingresaron valores válidos
        if (!identifier.isEmpty() && !name.isEmpty()) {
            // Crear un objeto Luchador
            Luchador luchador = new Luchador(identifier, name);

            // Guardar el luchador en la base de datos de Firebase
            databaseReference.child(identifier).setValue(luchador);

            // Limpiar los campos de entrada
            editTextIdentifier.setText("");
            editTextName.setText("");

            // Mostrar un mensaje de éxito
            Toast.makeText(this, "Luchador registrado con éxito", Toast.LENGTH_SHORT).show();
        } else {
            // Mostrar un mensaje de error si los campos están vacíos
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void modificarLuchador() {
        // Obtener los valores ingresados por el usuario
        String identifier = editTextIdentifier.getText().toString().trim();
        String newName = editTextName.getText().toString().trim();

        // Verificar si se ingresaron valores válidos
        if (!identifier.isEmpty() && !newName.isEmpty()) {
            // Actualizar el nombre del luchador en la base de datos de Firebase
            databaseReference.child(identifier).child("name").setValue(newName);

            // Limpiar los campos de entrada
            editTextIdentifier.setText("");
            editTextName.setText("");

            // Mostrar un mensaje de éxito
            Toast.makeText(this, "Luchador modificado con éxito", Toast.LENGTH_SHORT).show();
        } else {
            // Mostrar un mensaje de error si los campos están vacíos
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void eliminarLuchador() {
        // Obtener el identificador del luchador a eliminar
        String identifier = editTextIdentifier.getText().toString().trim();

        // Verificar si se ingresó un identificador válido
        if (!identifier.isEmpty()) {
            // Eliminar el luchador de la base de datos de Firebase
            databaseReference.child(identifier).removeValue();

            // Limpiar los campos de entrada
            editTextIdentifier.setText("");
            editTextName.setText("");

            // Mostrar un mensaje de éxito
            Toast.makeText(this, "Luchador eliminado con éxito", Toast.LENGTH_SHORT).show();
        } else {
            // Mostrar un mensaje de error si el campo está vacío
            Toast.makeText(this, "Por favor, ingresa el identificador del luchador a eliminar", Toast.LENGTH_SHORT).show();
        }
    }

    private void buscarLuchador() {
        // Obtener el identificador ingresado por el usuario
        String identifier = editTextIdentifier.getText().toString().trim();

        // Verificar si se ingresó un identificador válido
        if (!identifier.isEmpty()) {
            // Consultar Firebase para obtener el luchador por su identificador
            databaseReference.child(identifier).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // El luchador existe, obtener sus datos
                        Luchador luchador = dataSnapshot.getValue(Luchador.class);

                        // Mostrar los datos en los campos de texto
                        editTextName.setText(luchador.getName());

                        // Mostrar un mensaje de éxito
                        Toast.makeText(MainActivity.this, "Luchador encontrado", Toast.LENGTH_SHORT).show();
                    } else {
                        // El luchador no existe
                        editTextName.setText("");

                        // Mostrar un mensaje de error
                        Toast.makeText(MainActivity.this, "Luchador no encontrado", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Manejar errores de lectura de datos
                    Toast.makeText(MainActivity.this, "Error al buscar luchador: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Mostrar un mensaje de error si el campo está vacío
            Toast.makeText(this, "Por favor, ingresa el identificador del luchador a buscar", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarListadoLuchadores() {
        // Leer datos de la base de datos de Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                StringBuilder listado = new StringBuilder();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Luchador luchador = snapshot.getValue(Luchador.class);
                    listado.append("ID: ").append(luchador.getIdentifier()).append(", Nombre: ").append(luchador.getName()).append("\n");
                }

                // Mostrar el listado en el TextView
                textViewListado.setText(listado.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores de lectura de datos
                Toast.makeText(MainActivity.this, "Error al leer datos: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}