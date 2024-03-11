package com.example.supermercado;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MenuRegistro extends AppCompatActivity {

    private EditText edtName, edtLastName, edtEmail, edtUsername, edtPassword;
    private Button btnRegister;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_registro);
        loadPreferences();

        edtName = findViewById(R.id.edtName);
        edtLastName = findViewById(R.id.edtLastName);
        edtEmail = findViewById(R.id.edtEmail);
        edtUsername = findViewById(R.id.edtRegUsername);
        edtPassword = findViewById(R.id.edtRegPassword);
        btnRegister = findViewById(R.id.btnRegister);
        databaseHelper = new DatabaseHelper(this);

        edtName.setHint(getString(R.string.edtName));
        edtLastName.setHint(getString(R.string.edtLastName));
        edtEmail.setHint(getString(R.string.edtEmail));
        edtUsername.setHint(getString(R.string.hint_username));
        edtPassword.setHint(getString(R.string.hint_password));
        btnRegister.setText(getString(R.string.btnRegister));

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edtName.getText().toString();
                String lastName = edtLastName.getText().toString();
                String email = edtEmail.getText().toString();
                String username = edtUsername.getText().toString();
                String password = edtPassword.getText().toString();

                if (!name.isEmpty() && !lastName.isEmpty() && !email.isEmpty() && !username.isEmpty() && !password.isEmpty()) {
                    databaseHelper.addUser(username, password);
                    Toast.makeText(MenuRegistro.this, R.string.registration_success, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(MenuRegistro.this, MainActivity.class);
                    startActivity(intent);

                    finish();
                } else {
                    Toast.makeText(MenuRegistro.this, R.string.fill_all_fields, Toast.LENGTH_SHORT).show();
                }

            }
        });
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

    private void loadSavedColor() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int savedColor = preferences.getInt("color", 0);

        if (savedColor != 0) {
            changeBackgroundColor(savedColor);
        }
    }

    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration configuration = getResources().getConfiguration();
        configuration.setLocale(locale);

        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());

    }

    private void changeBackgroundColor(int color) {
        View rootView = getWindow().getDecorView().getRootView();
        rootView.setBackgroundColor(color);
    }
}
