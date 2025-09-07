package com.example.teleahorcado;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SeleccionTematicaActivity extends AppCompatActivity {

    private TextView textViewBienvenida;
    private Button buttonRedes, buttonCiberseguridad, buttonFibraOptica;
    private String nombreUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccion_tematica);

        // Configurar toolbar con botón de regreso
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("APPSIoT- Lab 1");

        // Obtener nombre del usuario
        nombreUsuario = getIntent().getStringExtra("nombre_usuario");

        initViews();
        setupEvents();
    }

    private void initViews() {
        textViewBienvenida = findViewById(R.id.textViewBienvenida);
        buttonRedes = findViewById(R.id.buttonRedes);
        buttonCiberseguridad = findViewById(R.id.buttonCiberseguridad);
        buttonFibraOptica = findViewById(R.id.buttonFibraOptica);

        // Mostrar saludo personalizado
        textViewBienvenida.setText("Bienvenido\n" + nombreUsuario);
    }

    private void setupEvents() {
        buttonRedes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarJuego("Redes");
            }
        });

        buttonCiberseguridad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarJuego("Ciberseguridad");
            }
        });

        buttonFibraOptica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarJuego("Fibra Óptica");
            }
        });
    }

    private void iniciarJuego(String tematica) {
        Intent intent = new Intent(this, JuegoActivity.class);
        intent.putExtra("nombre_usuario", nombreUsuario);
        intent.putExtra("tematica", tematica);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}