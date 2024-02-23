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

public class ProductosSupermercadoActivity extends AppCompatActivity implements DialogAgregarProducto.OnProductoAddedListener, LocationListener {
    private ProductosAdapter productosAdapter;
    private DatabaseHelper databaseHelper;
    private List<Producto> listaProductos;
    private String nombreSupermercado;
    private LocationManager locationManager;
    private String localizacionSupermercado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos_supermercado);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        nombreSupermercado = getIntent().getStringExtra("NOMBRE_SUPERMERCADO");
        localizacionSupermercado = getIntent().getStringExtra("LOCALIZACION_SUPERMERCADO");

        TextView txtSupermercado = findViewById(R.id.txtSupermercado);
        txtSupermercado.setText("Productos de " + nombreSupermercado);

        databaseHelper = new DatabaseHelper(this);
        listaProductos = new ArrayList<>();

        cargarProductosDesdeDB();

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
                if (isLocationEnabled()) {
                    requestSingleLocationUpdate();
                } else {
                    Toast.makeText(ProductosSupermercadoActivity.this, "Please enable location services", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            }
        });
    }

    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void requestSingleLocationUpdate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                return;
            }
        }

        try {
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
    }



    private void cargarProductosDesdeDB() {
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
        cargarProductosDesdeDB();
        productosAdapter.notifyDataSetChanged();
    }
}

