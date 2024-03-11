package com.example.supermercado;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
    private boolean preferencesLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!preferencesLoaded) {
            loadPreferences();
            preferencesLoaded = true;
        }

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

        setSupportActionBar(findViewById(R.id.toolbar));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_idioma, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_change_language) {
            showLanguageDialog();
            return true;
        }
        else {
            return true;
        }
    }


    private void showLanguageDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
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

    private void showAttemptsDialog(int attempts) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_title_failure);
        builder.setMessage(getString(R.string.dialog_message_invalid_credentials) + attempts);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void saveLanguagePreference(String languageCode) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("language", languageCode);
        editor.apply();
        Log.d("MainActivity", "Se ha cambiado la pref del idioma a " + languageCode);
    }

    private void loadPreferences() {
        Log.d("MainActivity","Se han cargado las preferencias");
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

        saveLanguagePreference(languageCode);

        if (preferencesLoaded) {
            recreate();
        }
    }

    private void loadSavedColor() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int savedColor = preferences.getInt("color", 0);

        Log.d("MainActivity", "loadSavedColor: Loaded color from preferences - " + savedColor);

        if (savedColor != 0) {
            changeBackgroundColor(savedColor);
        }
    }

    private void changeBackgroundColor(int color) {
        View rootView = getWindow().getDecorView().getRootView();
        rootView.setBackgroundColor(color);
    }

}
