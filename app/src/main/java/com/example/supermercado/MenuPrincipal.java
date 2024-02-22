package com.example.supermercado;

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

public class MenuPrincipal extends AppCompatActivity implements DialogAgregarProducto.OnProductoAddedListener {
    private ProductosAdapter productosAdapter;
    private DatabaseHelper databaseHelper;
    private List<Producto> listaProductos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        String username = getIntent().getStringExtra("USERNAME_EXTRA");

        TextView txtWelcome = findViewById(R.id.txtWelcome);
        txtWelcome.setText("¡Bienvenido, " + username + "!");

        databaseHelper = new DatabaseHelper(this);
        listaProductos = new ArrayList<>();

        cargarProductosDesdeDB();

        RecyclerView recyclerView = findViewById(R.id.recyclerViewProductos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productosAdapter = new ProductosAdapter(listaProductos);
        recyclerView.setAdapter(productosAdapter);

        Button btnAgregarProducto = findViewById(R.id.btnAgregarProducto);
        btnAgregarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogAgregarProducto dialog = new DialogAgregarProducto(MenuPrincipal.this, MenuPrincipal.this);
                dialog.show();
            }
        });
    }

    private void cargarProductosDesdeDB() {
        Log.d("CargarProductos", "Iniciando carga de productos desde la base de datos");

        listaProductos.clear();
        listaProductos.addAll(databaseHelper.getProductos());

        Log.d("CargarProductos", "Productos cargados desde la base de datos: " + listaProductos.size());
    }


    @Override
    public void onProductoAdded(String nombre, double precio) {
        databaseHelper.addProducto(nombre, precio);

        cargarProductosDesdeDB();

        productosAdapter.notifyDataSetChanged();
    }
}