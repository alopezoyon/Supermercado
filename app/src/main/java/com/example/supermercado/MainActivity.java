package com.example.supermercado;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

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
        loadPreferences();

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        databaseHelper = new DatabaseHelper(this);


        edtUsername.setHint(getString(R.string.hint_username));
        edtPassword.setHint(getString(R.string.hint_password));
        btnLogin.setText(getString(R.string.btnLogin));
        btnRegister.setText(getString(R.string.btnRegister));



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
                        //AQUÍ HAY QUE CAMBIAR ALGO
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

    private void loadPreferences() {
        loadSavedLanguage();
        loadSavedColor();
    }

    private void loadSavedLanguage() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String savedLanguage = preferences.getString("language", "");

        if (!savedLanguage.isEmpty()) {
            setLocale(savedLanguage);
        }
    }

    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration configuration = getResources().getConfiguration();
        configuration.setLocale(locale);

        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());

    }

    private void loadSavedColor() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int savedColor = preferences.getInt("color", 0);

        if (savedColor != 0) {
            changeBackgroundColor(savedColor);
        }
    }

    private void changeBackgroundColor(int color) {
        View rootView = getWindow().getDecorView().getRootView();
        rootView.setBackgroundColor(color);
    }

}
