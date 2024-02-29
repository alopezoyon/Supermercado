package com.example.supermercado;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

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

        Button btnChangeLanguage = findViewById(R.id.btnChangeLanguage);
        btnChangeLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLanguageDialog();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edtUsername.getText().toString();
                String password = edtPassword.getText().toString();

                if (databaseHelper.isValidCredentials(username, password)) {
                    Toast.makeText(MainActivity.this, R.string.login_success, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(MainActivity.this, MenuPrincipal.class);
                    intent.putExtra("USERNAME_EXTRA", username);
                    startActivity(intent);
                } else {
                    loginAttempts--;

                    if (loginAttempts > 0) {
                        showAttemptsDialog(loginAttempts);
                    } else {
                        Toast.makeText(MainActivity.this, R.string.intentos_agotados, Toast.LENGTH_SHORT).show();
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
        builder.setTitle(R.string.dialog_title_failure);
        builder.setMessage(getString(R.string.dialog_message_invalid_credentials) + attempts);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showLanguageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_language)
                .setItems(R.array.language_options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            setLocale("es");
                            break;
                        case 1:
                            setLocale("en");
                            break;
                        case 2:
                            setLocale("fr");
                            break;
                    }
                });

        builder.create().show();
    }

    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration configuration = getResources().getConfiguration();
        configuration.setLocale(locale);

        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());

        recreate();
    }


}


