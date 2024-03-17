package com.example.supermercado;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class VerNotaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_nota);

        TextView textViewNota = findViewById(R.id.textViewNota);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("NOMBRE_SUPERMERCADO")) {
            String nombreSupermercado = intent.getStringExtra("NOMBRE_SUPERMERCADO");
            mostrarNotasPorSupermercado(nombreSupermercado, textViewNota);
        }
    }

    private void mostrarNotasPorSupermercado(String nombreSupermercado, TextView textViewNota) {
        StringBuilder contenidoNotas = new StringBuilder();

        File folder = new File(getFilesDir(), "Notas_" + nombreSupermercado);
        if (!folder.exists()) {
            textViewNota.setText("No hay notas para este supermercado.");
            return;
        }

        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                contenidoNotas.append(leerContenidoNota(file)).append("\n");
            }
        }

        textViewNota.setText(contenidoNotas.toString());
    }

    private String leerContenidoNota(File file) {
        StringBuilder contenido = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                contenido.append(line).append("\n");
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contenido.toString();
    }
}

