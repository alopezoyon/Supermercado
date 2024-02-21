package com.example.supermercado;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class MenuRegistro extends AppCompatActivity {

    private EditText edtName, edtLastName, edtEmail, edtUsername, edtPassword;
    private Button btnRegister;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_registro);

        edtName = findViewById(R.id.edtName);
        edtLastName = findViewById(R.id.edtLastName);
        edtEmail = findViewById(R.id.edtEmail);
        edtUsername = findViewById(R.id.edtRegUsername);
        edtPassword = findViewById(R.id.edtRegPassword);
        btnRegister = findViewById(R.id.btnRegister);
        databaseHelper = new DatabaseHelper(this);

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
                    Toast.makeText(MenuRegistro.this, "Registro exitoso", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(MenuRegistro.this, MainActivity.class);
                    startActivity(intent);

                    finish();
                } else {
                    Toast.makeText(MenuRegistro.this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
