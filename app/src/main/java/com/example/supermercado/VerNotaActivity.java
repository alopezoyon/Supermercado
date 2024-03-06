package com.example.supermercado;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class VerNotaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_nota);

        Intent intent = getIntent();
        if (intent != null) {
            String fileName = intent.getStringExtra("ARCHIVO_NOTA");

            String notaContenido = leerContenidoNota(fileName);
            TextView textView = findViewById(R.id.textViewNota);
            textView.setText(notaContenido);
        }
    }

    private String leerContenidoNota(String fileName) {
        StringBuilder contenido = new StringBuilder();
        try {
            InputStream inputStream = openFileInput(fileName);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
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
