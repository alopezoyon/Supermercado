package com.example.supermercado;

import static androidx.core.graphics.drawable.DrawableCompat.applyTheme;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
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
        getMenuInflater().inflate(R.menu.menu_productos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_change_language) {
            showLanguageDialog();
            return true;
        } else if (itemId == R.id.menu_change_color) {
            showColorDialog();
            return true;
        }
        else {
            return true;
        }
    }

    private void showColorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_color);

        String[] colorOptions = getResources().getStringArray(R.array.colorOptions);

        if (colorOptions != null && colorOptions.length > 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, colorOptions);

            builder.setAdapter(adapter, (dialog, which) -> {
                String selectedStyle = colorOptions[which];
                applySelectedStyle(selectedStyle);
            });
        } else {
            builder.setMessage("No hay estilos disponibles.");
        }

        builder.create().show();
    }

    private void applySelectedStyle(String selectedStyle) {
        int styleResId;

        switch (selectedStyle) {
            case "Estilo 1":
                styleResId = R.style.Estilo1;
                break;
            case "Estilo 2":
                styleResId = R.style.Estilo2;
                break;
            case "Estilo 3":
                styleResId = R.style.Estilo3;
                break;
            default:
                styleResId = R.style.AppTheme;
                break;
        }

        applyTheme(styleResId);
        applyButtonAndBackgroundStyles(styleResId);
        saveColorPreference(styleResId);
    }

    private void applyTheme(int styleResId) {
        setTheme(styleResId);
    }

    private void applyButtonAndBackgroundStyles(int styleResId) {
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnRegister = findViewById(R.id.btnRegister);

        TypedArray styledAttributes = getTheme().obtainStyledAttributes(styleResId, new int[] {
                androidx.constraintlayout.widget.R.attr.buttonStyle
        });

        int buttonStyleResId = styledAttributes.getResourceId(0, 0);
        styledAttributes.recycle();

        if (buttonStyleResId != 0) {
            btnLogin.setTextAppearance(this, buttonStyleResId);
            btnRegister.setTextAppearance(this, buttonStyleResId);
            btnLogin.setBackgroundResource(buttonStyleResId);
            btnRegister.setBackgroundResource(buttonStyleResId);
            View rootView = getWindow().getDecorView().getRootView();
            rootView.setBackgroundResource(styleResId);
        }
    }


    private void loadSavedColor() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int savedColor = preferences.getInt("color", 0);

        if (savedColor != 0) {
            applySelectedStyle(String.valueOf(savedColor));
        }
    }

    private void saveColorPreference(int color) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt("color", color);
        editor.apply();
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

        saveLanguagePreference(languageCode);

        if (preferencesLoaded) {
            recreate();
        }
    }

}
