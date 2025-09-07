package com.example.teleahorcado;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class EstadisticasActivity extends AppCompatActivity {

    private TextView textViewNombre, textViewPartidasJugadas, textViewPartidasGanadas,
            textViewPorcentajeExito, textViewTiempoPromedio;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "TeleAhorcadoStats";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        // Configurar toolbar
        setupToolbar();

        // Inicializar SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Inicializar vistas
        initViews();

        // Cargar estadísticas
        cargarEstadisticas();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Estadísticas");
    }

    private void initViews() {
        textViewNombre = findViewById(R.id.textViewNombre);
        textViewPartidasJugadas = findViewById(R.id.textViewPartidasJugadas);
        textViewPartidasGanadas = findViewById(R.id.textViewPartidasGanadas);
        textViewPorcentajeExito = findViewById(R.id.textViewPorcentajeExito);
        textViewTiempoPromedio = findViewById(R.id.textViewTiempoPromedio);
    }

    private void cargarEstadisticas() {
        // Obtener nombre del usuario actual (si está jugando)
        String nombreUsuario = getIntent().getStringExtra("nombre_usuario");
        if (nombreUsuario == null || nombreUsuario.isEmpty()) {
            nombreUsuario = "Invitado";
        }

        // Cargar estadísticas guardadas
        int partidasJugadas = sharedPreferences.getInt("partidas_jugadas", 0);
        int partidasGanadas = sharedPreferences.getInt("partidas_ganadas", 0);
        float tiempoPromedio = sharedPreferences.getFloat("tiempo_promedio", 0.0f);

        // Calcular porcentaje de éxito
        float porcentajeExito = 0.0f;
        if (partidasJugadas > 0) {
            porcentajeExito = ((float) partidasGanadas / partidasJugadas) * 100;
        }

        // Mostrar datos
        textViewNombre.setText("Usuario: " + nombreUsuario);
        textViewPartidasJugadas.setText("Partidas jugadas: " + partidasJugadas);
        textViewPartidasGanadas.setText("Partidas ganadas: " + partidasGanadas);
        textViewPorcentajeExito.setText(String.format("Porcentaje de éxito: %.1f%%", porcentajeExito));
        textViewTiempoPromedio.setText(String.format("Tiempo promedio: %.1fs", tiempoPromedio));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}