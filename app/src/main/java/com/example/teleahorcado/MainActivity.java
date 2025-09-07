package com.example.teleahorcado;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private EditText editTextNombre;
    private Button buttonJugar;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "TeleAhorcadoPrefs";
    private static final String KEY_NOMBRE = "nombre_usuario";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("TeleAhorcado");

        // Inicializar SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Inicializar vistas
        initViews();

        // Configurar eventos
        setupEvents();
    }

    private void initViews() {
        editTextNombre = findViewById(R.id.editTextNombre);
        buttonJugar = findViewById(R.id.buttonJugar);
    }

    private void setupEvents() {
        buttonJugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irASeleccionTematica();
            }
        });
    }

    private void irASeleccionTematica() {
        String nombre = editTextNombre.getText().toString().trim();

        if (nombre.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa tu nombre", Toast.LENGTH_SHORT).show();
            return;
        }

        // Guardar nombre temporalmente
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_NOMBRE, nombre);
        editor.apply();

        Intent intent = new Intent(this, SeleccionTematicaActivity.class);
        intent.putExtra("nombre_usuario", nombre);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_estadisticas) {
            String nombre = sharedPreferences.getString(KEY_NOMBRE, "");
            Intent intent = new Intent(this, EstadisticasActivity.class);
            intent.putExtra("nombre_usuario", nombre);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_cambiar_color) {
            cambiarColorTitulo();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void cambiarColorTitulo() {
        // Implementar cambio de color del título si es necesario
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Limpiar datos al regresar a la pantalla principal
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_NOMBRE);
        editor.apply();

        editTextNombre.setText("");
    }
}