package com.example.supermercado;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MenuPrincipal extends AppCompatActivity implements
        DialogAgregarSupermercado.OnSupermercadoAddedListener,
        SupermercadosAdapter.OnSupermercadoClickListener,
        ProductosFragment.listenerDelFragment{

    private SupermercadosAdapter supermercadosAdapter;
    private DatabaseHelper databaseHelper;
    private List<Supermercado> listaSupermercados;
    private boolean esModoHorizontal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        esModoHorizontal = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        String username = getIntent().getStringExtra("USERNAME_EXTRA");

        TextView txtWelcome = findViewById(R.id.txtWelcome);
        txtWelcome.setText(R.string.welcome_message + username + "!");

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
        listaSupermercados.clear();
        listaSupermercados.addAll(databaseHelper.getSupermercados());
    }

    @Override
    public void onSupermercadoAdded(String nombre, String localizacion) {
        databaseHelper.addSupermercado(nombre, localizacion);

        cargarSupermercadosDesdeDB();

        supermercadosAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSupermercadoClick(int position) {
        Supermercado supermercado = listaSupermercados.get(position);

        if (esModoHorizontal) {
            cargarProductosFragment(supermercado);
            Log.d("MenuPrincipal", "Es horizontal");
        } else {
            Intent intent = new Intent(MenuPrincipal.this, ProductosSupermercadoActivity.class);
            intent.putExtra("NOMBRE_SUPERMERCADO", supermercado.getNombre());
            intent.putExtra("LOCALIZACION_SUPERMERCADO", supermercado.getLocalizacion());
            startActivity(intent);
        }
    }

    private void cargarProductosFragment(Supermercado supermercado) {
        findViewById(R.id.supermercadoContainer).setVisibility(View.GONE);
        findViewById(R.id.productoContainer).setVisibility(View.VISIBLE);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        ProductosFragment productosFragment = (ProductosFragment) fragmentManager.findFragmentByTag("ProductosFragment");

        if (productosFragment == null) {
            productosFragment = new ProductosFragment();
            fragmentTransaction.replace(R.id.productoContainer, productosFragment, "ProductosFragment");
        }

        productosFragment.setNombreSupermercado(supermercado.getNombre());
        Log.d("MenuPrincipal", "Selected Supermarket in Fragment: " + supermercado.getNombre());

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void seleccionarElemento(String elemento) {
    }
}