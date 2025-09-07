package com.example.teleahorcado;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JuegoActivity extends AppCompatActivity {

    // Variables de la UI
    private TextView textViewTematica, textViewPalabra;
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
            // Redes
            {"ROUTER", "SWITCH", "GATEWAY", "FIREWALL", "ETHERNET", "WIFI", "TCP", "UDP", "HTTP", "FTP"},
            // Ciberseguridad
            {"PHISHING", "MALWARE", "ANTIVIRUS", "ENCRYPTION", "HACKER", "VIRUS", "TROJAN", "SPYWARE", "FIREWALL", "PASSWORD"},
            // Fibra Óptica
            {"FIBRA", "LASER", "OPTICO", "CONNECTOR", "SPLICE", "ATTENUATION", "DISPERSION", "WAVELENGTH", "BANDWIDTH", "CABLE"}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);

        // Configurar toolbar
        setupToolbar();

        // Obtener datos del intent
        obtenerDatosIntent();

        // Inicializar vistas
        initViews();

        // Inicializar juego
        inicializarJuego();

        // Configurar eventos
        setupEvents();

        //Iniciar el timer
        iniciarTimer();
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

        imageViewAntena = findViewById(R.id.imageViewAntena);
        imageViewCabeza = findViewById(R.id.imageViewCabeza);
        imageViewTorso = findViewById(R.id.imageViewTorso);
        imageViewBrazoDerecho = findViewById(R.id.imageViewBrazoDerecho);
        imageViewBrazoIzquierdo = findViewById(R.id.imageViewBrazoIzquierdo);
        imageViewPiernaDerecha = findViewById(R.id.imageViewPiernaDerecha);
        imageViewPiernaIzquierda = findViewById(R.id.imageViewPiernaIzquierda);

        gridLetras = findViewById(R.id.gridLetras);
        buttonNuevoJuego = findViewById(R.id.buttonNuevoJuego);

        // Configurar temática
        textViewTematica.setText(tematica);
    }

    private void inicializarJuego() {
        // Resetear variables
        errores = 0;
        juegoTerminado = false;
        letrasIncorrectas = new ArrayList<>();

        // Ocultar partes del ahorcado
        ocultarPartesAhorcado();

        // Seleccionar palabra aleatoria
        seleccionarPalabraAleatoria();

        // Crear botones de letras
        crearBotonesLetras();
    }

    private void seleccionarPalabraAleatoria() {
        int indiceTematica = 0;
        switch (tematica) {
            case "Redes":
                indiceTematica = 0;
                break;
            case "Ciberseguridad":
                indiceTematica = 1;
                break;
            case "Fibra Óptica":
                indiceTematica = 2;
                break;
        }

        Random random = new Random();
        String[] palabras = palabrasPorTematica[indiceTematica];
        palabraActual = palabras[random.nextInt(palabras.length)];

        // Crear palabra con guiones
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

            if (!palabraMostrada.toString().contains("_")) {
                ganarJuego();
            }
        } else {
            boton.setBackgroundColor(Color.RED);
            letrasIncorrectas.add(letraChar);
            errores++;
            mostrarParteAhorcado();

            // El ahorcado completo tiene 6 partes: cabeza, torso, brazo der, brazo izq, pierna der, pierna izq
            if (errores >= 6) {
                perderJuego();
            }
        }

        textViewPalabra.setText(palabraMostrada.toString().trim());
    }

    private void mostrarParteAhorcado() {
        switch (errores) {
            case 1:
                imageViewCabeza.setVisibility(View.VISIBLE);
                break;
            case 2:
                imageViewTorso.setVisibility(View.VISIBLE);
                break;
            case 3:
                imageViewBrazoDerecho.setVisibility(View.VISIBLE);
                break;
            case 4:
                imageViewBrazoIzquierdo.setVisibility(View.VISIBLE);
                break;
            case 5:
                imageViewPiernaIzquierda.setVisibility(View.VISIBLE);
                break;
            case 6:
                imageViewPiernaDerecha.setVisibility(View.VISIBLE);
                break;
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
        Toast.makeText(this, "¡Felicidades " + nombreUsuario + "! ¡Ganaste!", Toast.LENGTH_LONG).show();
        buttonNuevoJuego.setVisibility(View.VISIBLE);
    }

    private void perderJuego() {
        juegoTerminado = true;
        textViewPalabra.setText(palabraActual);
        Toast.makeText(this, "¡Perdiste! La palabra era: " + palabraActual, Toast.LENGTH_LONG).show();
        buttonNuevoJuego.setVisibility(View.VISIBLE);
    }

    private void setupEvents() {
        buttonNuevoJuego.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonNuevoJuego.setVisibility(View.GONE);
                inicializarJuego();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
}