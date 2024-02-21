package com.example.supermercado;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private Button btnLogin, btnRegister;
    private int loginAttempts = 3;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        databaseHelper = new DatabaseHelper(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edtUsername.getText().toString();
                String password = edtPassword.getText().toString();

                if (databaseHelper.isValidCredentials(username, password)) {
                    Toast.makeText(MainActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                    // Iniciar la actividad después de un inicio de sesión exitoso
                    Intent intent = new Intent(MainActivity.this, MenuPrincipal.class);
                    startActivity(intent);
                } else {
                    loginAttempts--;

                    if (loginAttempts > 0) {
                        showAttemptsDialog(loginAttempts);
                    } else {
                        Toast.makeText(MainActivity.this, "Número de intentos agotado", Toast.LENGTH_SHORT).show();
                        // Puedes manejar otras acciones aquí, como bloquear el usuario o cerrar la aplicación
                    }
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MenuRegistro.class);
                startActivity(intent);
            }
        });
    }

    private void showAttemptsDialog(int attempts) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Intento fallido");
        builder.setMessage("Credenciales no válidas. Intentos restantes: " + attempts);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}


