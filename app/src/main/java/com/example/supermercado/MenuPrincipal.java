package com.example.supermercado;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MenuPrincipal extends AppCompatActivity implements DialogAgregarProducto.OnProductoAddedListener {

    private List<Producto> listaProductos;
    private ProductosAdapter productosAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        String username = getIntent().getStringExtra("USERNAME_EXTRA");

        TextView txtWelcome = findViewById(R.id.txtWelcome);
        txtWelcome.setText("¡Bienvenido, " + username + "!");

        listaProductos = new ArrayList<>();

        listaProductos.add(new Producto("Producto 1", 10.99));
        listaProductos.add(new Producto("Producto 2", 20.49));
        listaProductos.add(new Producto("Producto 3", 15.75));

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

    @Override
    public void onProductoAdded(String nombre, double precio) {
        Producto nuevoProducto = new Producto(nombre, precio);
        listaProductos.add(nuevoProducto);

        productosAdapter.notifyDataSetChanged();
    }
}

