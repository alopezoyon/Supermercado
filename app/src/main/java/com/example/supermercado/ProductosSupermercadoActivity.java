package com.example.supermercado;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProductosSupermercadoActivity extends AppCompatActivity implements DialogAgregarProducto.OnProductoAddedListener {
    private ProductosAdapter productosAdapter;
    private DatabaseHelper databaseHelper;
    private List<Producto> listaProductos;
    private String nombreSupermercado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos_supermercado);

        nombreSupermercado = getIntent().getStringExtra("NOMBRE_SUPERMERCADO");
        String localizacionSupermercado = getIntent().getStringExtra("LOCALIZACION_SUPERMERCADO");

        TextView txtSupermercado = findViewById(R.id.txtSupermercado);
        txtSupermercado.setText("Productos de " + nombreSupermercado);

        databaseHelper = new DatabaseHelper(this);
        listaProductos = new ArrayList<>();

        cargarProductosDesdeDB(nombreSupermercado);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewProductosSupermercado);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productosAdapter = new ProductosAdapter(listaProductos);
        recyclerView.setAdapter(productosAdapter);

        Button btnAddProduct = findViewById(R.id.btnAddProduct);
        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddProductDialog();
            }
        });

        Button btnIr = findViewById(R.id.btnCalculateDistance);
        btnIr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGoogleMapsForSupermarket(localizacionSupermercado);
            }
        });
    }

    private void openGoogleMapsForSupermarket(String localizacionSupermercado) {
        String supermercadoUri = Uri.encode(localizacionSupermercado);
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + supermercadoUri);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(ProductosSupermercadoActivity.this, "Google Maps no está instalado en tu dispositivo", Toast.LENGTH_SHORT).show();
        }
    }


    private void cargarProductosDesdeDB(String nombreSupermercado) {
        listaProductos.clear();
        listaProductos.addAll(databaseHelper.getProductosPorSupermercado(nombreSupermercado));
    }

    private void openAddProductDialog() {
        DialogAgregarProducto dialog = new DialogAgregarProducto(this, this);
        dialog.show();
    }

    @Override
    public void onProductoAdded(String nombre, double precio) {
        databaseHelper.addProductoASupermercado(nombreSupermercado, nombre);
        cargarProductosDesdeDB(nombreSupermercado);
        productosAdapter.notifyDataSetChanged();
    }
}