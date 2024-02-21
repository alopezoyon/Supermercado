package com.example.supermercado;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MenuPrincipal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        // Recuperar el nombre de usuario del extra del Intent
        String username = getIntent().getStringExtra("USERNAME_EXTRA");

        // Mostrar el nombre de usuario en un TextView
        TextView txtWelcome = findViewById(R.id.txtWelcome);
        txtWelcome.setText("¡Bienvenido, " + username + "!");
    }
}
