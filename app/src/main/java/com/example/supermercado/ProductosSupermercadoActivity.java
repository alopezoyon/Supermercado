package com.example.supermercado;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

//Esta clase implementa la actividad de mostrar los productos de supermercado determinado.
//En esta actividad tenenemos la posibilidad de añadir un producto, incluyendo su nombre y precio.
//Además, podemos pulsar el botón "Ir", que abre Google Maps con la ubicación guardada en "localización" del supermercado
public class ProductosSupermercadoActivity extends AppCompatActivity implements DialogAgregarProducto.OnProductoAddedListener {
    private ProductosAdapter productosAdapter;
    private DatabaseHelper databaseHelper;
    private List<Producto> listaProductos;
    private String nombreSupermercado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos_supermercado);
        loadPreferences();

        nombreSupermercado = getIntent().getStringExtra("NOMBRE_SUPERMERCADO");
        String localizacionSupermercado = getIntent().getStringExtra("LOCALIZACION_SUPERMERCADO");

        TextView txtSupermercado = findViewById(R.id.txtSupermercado);
        txtSupermercado.setText(getString(R.string.producto_de) + " " + nombreSupermercado);

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
                DialogAgregarProducto dialog = new DialogAgregarProducto(ProductosSupermercadoActivity.this, ProductosSupermercadoActivity.this);
                dialog.show();
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

    //Método para abrir Google Maps con la localización guardada
    private void openGoogleMapsForSupermarket(String localizacionSupermercado) {
        try {
            String supermercadoUri = Uri.encode(localizacionSupermercado);
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + supermercadoUri);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(ProductosSupermercadoActivity.this, R.string.error_maps, Toast.LENGTH_SHORT).show();
        }
    }

    //Mismos métodos para cargar las preferencias

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


    //Método para cargar los productos del supermercado
    private void cargarProductosDesdeDB(String nombreSupermercado) {

        listaProductos.clear();
        listaProductos.addAll(databaseHelper.getProductosPorSupermercado(nombreSupermercado));

    }

    //Método utilizado en el caso de haber añadido un producto
    @Override
    public void onProductoAdded(String nombre, double precio) {
        if (!databaseHelper.productoExiste(nombreSupermercado,nombre)){
            databaseHelper.addProductoASupermercado(nombreSupermercado, nombre, precio);
            cargarProductosDesdeDB(nombreSupermercado);
            productosAdapter.notifyDataSetChanged();
        }
        else {
            Toast.makeText(this, "El producto ya existe en el supermercado", Toast.LENGTH_SHORT).show();
        }
    }
}