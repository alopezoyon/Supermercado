package com.example.supermercado;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class DialogAgregarSupermercado extends Dialog {

    private OnSupermercadoAddedListener listener;

    public DialogAgregarSupermercado(@NonNull Context context, OnSupermercadoAddedListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_agregar_supermercado);

        EditText edtNombre = findViewById(R.id.edtNombreSupermercado);
        EditText edtLocalizacion = findViewById(R.id.edtLocalizacionSupermercado);
        Button btnAgregar = findViewById(R.id.btnAgregarSupermercado);

        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombre = edtNombre.getText().toString().trim();
                String localizacion = edtLocalizacion.getText().toString().trim();

                if (!nombre.isEmpty() && !localizacion.isEmpty()) {
                    listener.onSupermercadoAdded(nombre, localizacion);

                    dismiss();
                } else {
                    Toast.makeText(getContext(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public interface OnSupermercadoAddedListener {
        void onSupermercadoAdded(String nombre, String localizacion);

        void onSupermercadoClick(int position);
    }
}
