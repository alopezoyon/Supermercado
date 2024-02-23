package com.example.supermercado;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MenuPrincipal extends AppCompatActivity implements DialogAgregarSupermercado.OnSupermercadoAddedListener, DialogAgregarProducto.OnProductoAddedListener, SupermercadosAdapter.OnSupermercadoClickListener {
    private SupermercadosAdapter supermercadosAdapter;
    private DatabaseHelper databaseHelper;
    private List<Supermercado> listaSupermercados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        String username = getIntent().getStringExtra("USERNAME_EXTRA");

        TextView txtWelcome = findViewById(R.id.txtWelcome);
        txtWelcome.setText("¡Bienvenido, " + username + "!");

        databaseHelper = new DatabaseHelper(this);
        listaSupermercados = new ArrayList<>();

        cargarSupermercadosDesdeDB();

        RecyclerView recyclerView = findViewById(R.id.recyclerViewSupermercados);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        supermercadosAdapter = new SupermercadosAdapter(listaSupermercados, this);
        recyclerView.setAdapter(supermercadosAdapter);

        Button btnAgregarSupermercado = findViewById(R.id.btnAgregarSupermercado);
        btnAgregarSupermercado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogAgregarSupermercado dialog = new DialogAgregarSupermercado(MenuPrincipal.this, MenuPrincipal.this);
                dialog.show();
            }
        });
    }

    private void cargarSupermercadosDesdeDB() {
        Log.d("CargarSupermercados", "Iniciando carga de supermercados desde la base de datos");

        listaSupermercados.clear();
        listaSupermercados.addAll(databaseHelper.getSupermercados());

        Log.d("CargarSupermercados", "Supermercados cargados desde la base de datos: " + listaSupermercados.size());
    }

    @Override
    public void onSupermercadoAdded(String nombre, String localizacion) {
        databaseHelper.addSupermercado(nombre, localizacion);

        cargarSupermercadosDesdeDB();

        supermercadosAdapter.notifyDataSetChanged();
    }

    @Override
    public void onProductoAdded(String nombre, double precio) {
        // Puedes implementar la lógica para agregar productos a un supermercado aquí si es necesario.
    }

    @Override
    public void onSupermercadoClick(int position) {
        Supermercado supermercado = listaSupermercados.get(position);

        Intent intent = new Intent(MenuPrincipal.this, ProductosSupermercadoActivity.class);
        intent.putExtra("NOMBRE_SUPERMERCADO", supermercado.getNombre());
        intent.putExtra("LOCALIZACION_SUPERMERCADO", supermercado.getLocalizacion());
        startActivity(intent);
    }
}


