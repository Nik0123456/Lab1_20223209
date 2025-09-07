package com.example.teleahorcado;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class JuegoActivity extends AppCompatActivity {

    // Variables de la UI
    private TextView textViewTematica, textViewPalabra, textViewTiempo, textViewComodin;
    private ImageView imageViewAntena, imageViewCabeza, imageViewTorso,
            imageViewBrazoDerecho, imageViewBrazoIzquierdo,
            imageViewPiernaDerecha, imageViewPiernaIzquierda;
    private GridLayout gridLetras;
    private Button buttonNuevoJuego;

    // Variables del juego
    private String nombreUsuario, tematica;
    private String palabraActual;
    private StringBuilder palabraMostrada;
    private List<Character> letrasIncorrectas;
    private int errores = 0;
    private boolean juegoTerminado = false;

    // Variables del timer
    private Handler timerHandler;
    private Runnable timerRunnable;
    private long tiempoInicio;
    private int segundosTranscurridos = 0;

    // Variables del comodín
    private int comodinesDisponibles = 0;
    private int aciertosConsecutivos = 0;
    private SharedPreferences gamePrefs;

    // Palabras por temática
    private final String[][] palabrasPorTematica = {
            {"ROUTER", "SWITCH", "GATEWAY", "FIREWALL", "ETHERNET", "WIFI", "TCP", "UDP", "HTTP", "FTP"},
            {"PHISHING", "MALWARE", "ANTIVIRUS", "ENCRYPTION", "HACKER", "VIRUS", "TROJAN", "SPYWARE", "FIREWALL", "PASSWORD"},
            {"FIBRA", "LASER", "OPTICO", "CONNECTOR", "SPLICE", "ATTENUATION", "DISPERSION", "WAVELENGTH", "BANDWIDTH", "CABLE"}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);

        setupToolbar();
        obtenerDatosIntent();
        initViews();
        inicializarJuego();
        setupEvents();

        // Inicializar SharedPreferences
        gamePrefs = getSharedPreferences("TeleAhorcadoStats", MODE_PRIVATE);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("TeleAhorcado");
    }

    private void obtenerDatosIntent() {
        nombreUsuario = getIntent().getStringExtra("nombre_usuario");
        tematica = getIntent().getStringExtra("tematica");
    }

    private void initViews() {
        textViewTematica = findViewById(R.id.textViewTematica);
        textViewPalabra = findViewById(R.id.textViewPalabra);
        textViewTiempo = findViewById(R.id.textViewTiempo);
        textViewComodin = findViewById(R.id.textViewComodin);

        imageViewAntena = findViewById(R.id.imageViewAntena);
        imageViewCabeza = findViewById(R.id.imageViewCabeza);
        imageViewTorso = findViewById(R.id.imageViewTorso);
        imageViewBrazoDerecho = findViewById(R.id.imageViewBrazoDerecho);
        imageViewBrazoIzquierdo = findViewById(R.id.imageViewBrazoIzquierdo);
        imageViewPiernaDerecha = findViewById(R.id.imageViewPiernaDerecha);
        imageViewPiernaIzquierda = findViewById(R.id.imageViewPiernaIzquierda);

        gridLetras = findViewById(R.id.gridLetras);
        buttonNuevoJuego = findViewById(R.id.buttonNuevoJuego);

        textViewTematica.setText(tematica);
    }

    private void inicializarJuego() {
        errores = 0;
        juegoTerminado = false;
        letrasIncorrectas = new ArrayList<>();
        aciertosConsecutivos = 0;

        ocultarPartesAhorcado();
        seleccionarPalabraAleatoria();
        crearBotonesLetras();
        iniciarTimer();
        actualizarComodin();
    }

    private void iniciarTimer() {
        tiempoInicio = System.currentTimeMillis();
        segundosTranscurridos = 0;

        timerHandler = new Handler();
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                segundosTranscurridos = (int) ((System.currentTimeMillis() - tiempoInicio) / 1000);
                textViewTiempo.setText("Tiempo: " + segundosTranscurridos + "s");
                timerHandler.postDelayed(this, 1000);
            }
        };
        timerHandler.post(timerRunnable);
    }

    private void detenerTimer() {
        if (timerHandler != null && timerRunnable != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }
    }

    private void actualizarComodin() {
        comodinesDisponibles = aciertosConsecutivos / 4;
        textViewComodin.setText("⭐ " + comodinesDisponibles + "/" + aciertosConsecutivos);

        // Habilitar/deshabilitar clic en comodín
        textViewComodin.setClickable(comodinesDisponibles > 0);
        textViewComodin.setAlpha(comodinesDisponibles > 0 ? 1.0f : 0.5f);
    }

    private void seleccionarPalabraAleatoria() {
        int indiceTematica = 0;
        switch (tematica) {
            case "Redes": indiceTematica = 0; break;
            case "Ciberseguridad": indiceTematica = 1; break;
            case "Fibra Óptica": indiceTematica = 2; break;
        }

        Random random = new Random();
        String[] palabras = palabrasPorTematica[indiceTematica];
        palabraActual = palabras[random.nextInt(palabras.length)];

        palabraMostrada = new StringBuilder();
        for (int i = 0; i < palabraActual.length(); i++) {
            palabraMostrada.append("_ ");
        }

        textViewPalabra.setText(palabraMostrada.toString().trim());
    }

    private void crearBotonesLetras() {
        gridLetras.removeAllViews();

        String[] letras = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
                "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

        for (String letra : letras) {
            Button botonLetra = new Button(this);
            botonLetra.setText(letra);
            botonLetra.setTag(letra);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 120;
            params.height = 120;
            params.setMargins(4, 4, 4, 4);
            botonLetra.setLayoutParams(params);

            botonLetra.setBackgroundResource(R.drawable.button_letra_background);
            botonLetra.setTextColor(Color.BLACK);
            botonLetra.setTextSize(16);

            botonLetra.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    procesarLetra((String) v.getTag(), botonLetra);
                }
            });

            gridLetras.addView(botonLetra);
        }
    }

    private void procesarLetra(String letra, Button boton) {
        if (juegoTerminado) return;

        boton.setEnabled(false);

        char letraChar = letra.charAt(0);
        boolean letraEncontrada = false;

        for (int i = 0; i < palabraActual.length(); i++) {
            if (palabraActual.charAt(i) == letraChar) {
                int posicion = i * 2;
                palabraMostrada.setCharAt(posicion, letraChar);
                letraEncontrada = true;
            }
        }

        if (letraEncontrada) {
            boton.setBackgroundColor(Color.GREEN);
            aciertosConsecutivos++;
            actualizarComodin();

            if (!palabraMostrada.toString().contains("_")) {
                ganarJuego();
            }
        } else {
            boton.setBackgroundColor(Color.RED);
            letrasIncorrectas.add(letraChar);
            errores++;
            aciertosConsecutivos = 0; // Reiniciar contador de aciertos
            actualizarComodin();
            mostrarParteAhorcado();

            if (errores >= 6) {
                perderJuego();
            }
        }

        textViewPalabra.setText(palabraMostrada.toString().trim());
    }

    private void usarComodin() {
        if (comodinesDisponibles <= 0 || juegoTerminado) return;

        // Encontrar letras no reveladas
        List<Character> letrasNoReveladas = new ArrayList<>();
        for (int i = 0; i < palabraActual.length(); i++) {
            char letra = palabraActual.charAt(i);
            if (palabraMostrada.charAt(i * 2) == '_' && !letrasNoReveladas.contains(letra)) {
                letrasNoReveladas.add(letra);
            }
        }

        if (!letrasNoReveladas.isEmpty()) {
            // Seleccionar letra aleatoria
            Random random = new Random();
            char letraRevelada = letrasNoReveladas.get(random.nextInt(letrasNoReveladas.size()));

            // Revelar la letra
            for (int i = 0; i < palabraActual.length(); i++) {
                if (palabraActual.charAt(i) == letraRevelada) {
                    palabraMostrada.setCharAt(i * 2, letraRevelada);
                }
            }

            // Desactivar botón correspondiente y marcarlo como usado
            for (int i = 0; i < gridLetras.getChildCount(); i++) {
                Button btn = (Button) gridLetras.getChildAt(i);
                if (btn.getTag().equals(String.valueOf(letraRevelada))) {
                    btn.setEnabled(false);
                    btn.setBackgroundColor(Color.YELLOW); // Color especial para comodín
                    break;
                }
            }

            // Reducir comodines disponibles
            aciertosConsecutivos -= 4;
            actualizarComodin();

            textViewPalabra.setText(palabraMostrada.toString().trim());

            // Verificar si ganó
            if (!palabraMostrada.toString().contains("_")) {
                ganarJuego();
            }

            Toast.makeText(this, "¡Comodín usado! Letra revelada: " + letraRevelada, Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarParteAhorcado() {
        switch (errores) {
            case 1: imageViewCabeza.setVisibility(View.VISIBLE); break;
            case 2: imageViewTorso.setVisibility(View.VISIBLE); break;
            case 3: imageViewBrazoDerecho.setVisibility(View.VISIBLE); break;
            case 4: imageViewBrazoIzquierdo.setVisibility(View.VISIBLE); break;
            case 5: imageViewPiernaIzquierda.setVisibility(View.VISIBLE); break;
            case 6: imageViewPiernaDerecha.setVisibility(View.VISIBLE); break;
        }
    }

    private void ocultarPartesAhorcado() {
        imageViewCabeza.setVisibility(View.INVISIBLE);
        imageViewTorso.setVisibility(View.INVISIBLE);
        imageViewBrazoDerecho.setVisibility(View.INVISIBLE);
        imageViewBrazoIzquierdo.setVisibility(View.INVISIBLE);
        imageViewPiernaDerecha.setVisibility(View.INVISIBLE);
        imageViewPiernaIzquierda.setVisibility(View.INVISIBLE);
    }

    private void ganarJuego() {
        juegoTerminado = true;
        detenerTimer();

        textViewTiempo.setTextColor(Color.GREEN);
        textViewTiempo.setText("Ganó / Tiempo: " + segundosTranscurridos + "s");

        guardarEstadistica(true);
        buttonNuevoJuego.setVisibility(View.VISIBLE);

        Toast.makeText(this, "¡Felicidades " + nombreUsuario + "! ¡Ganaste!", Toast.LENGTH_LONG).show();
    }

    private void perderJuego() {
        juegoTerminado = true;
        detenerTimer();

        textViewPalabra.setText(palabraActual);
        textViewTiempo.setTextColor(Color.RED);
        textViewTiempo.setText("Perdió / Tiempo: " + segundosTranscurridos + "s");

        guardarEstadistica(false);
        buttonNuevoJuego.setVisibility(View.VISIBLE);

        Toast.makeText(this, "¡Perdiste! La palabra era: " + palabraActual, Toast.LENGTH_LONG).show();
    }

    private void guardarEstadistica(boolean gano) {
        // Guardar estadística del juego
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault());
        String fechaHora = sdf.format(new Date());

        SharedPreferences.Editor editor = gamePrefs.edit();

        // Incrementar contador de partidas
        int totalPartidas = gamePrefs.getInt("total_partidas", 0) + 1;
        editor.putInt("total_partidas", totalPartidas);

        // Guardar partida individual
        String resultado = gano ? "Ganó" : "Perdió";
        String partidaInfo = "Juego " + totalPartidas + ": " + resultado + " / Tiempo: " + segundosTranscurridos + "s";
        editor.putString("partida_" + totalPartidas, partidaInfo);
        editor.putString("fecha_" + totalPartidas, fechaHora);
        editor.putBoolean("gano_" + totalPartidas, gano);
        editor.putInt("tiempo_" + totalPartidas, segundosTranscurridos);

        editor.apply();
    }

    private void setupEvents() {
        buttonNuevoJuego.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonNuevoJuego.setVisibility(View.GONE);
                inicializarJuego();
            }
        });

        textViewComodin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usarComodin();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_juego, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            detenerTimer();
            finish();
            return true;
        } else if (id == R.id.action_estadisticas) {
            Intent intent = new Intent(this, EstadisticasActivity.class);
            intent.putExtra("nombre_usuario", nombreUsuario);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        detenerTimer();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // No detener el timer en pausa, mantener corriendo
    }
}