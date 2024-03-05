package com.example.supermercado;

import android.content.Intent;
import android.content.res.Configuration;
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

public class MenuPrincipal extends AppCompatActivity implements
        DialogAgregarSupermercado.OnSupermercadoAddedListener,
        SupermercadosAdapter.OnSupermercadoClickListener,
        ProductosFragment.listenerDelFragment {

    private SupermercadosAdapter supermercadosAdapter;
    private DatabaseHelper databaseHelper;
    private List<Supermercado> listaSupermercados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        String username = getIntent().getStringExtra("USERNAME_EXTRA");
        Log.d("MenuPrincipal", "Estoy en modo " + getResources().getConfiguration().orientation);

        TextView txtWelcome = findViewById(R.id.txtWelcome);

        if (txtWelcome != null) {
            txtWelcome.setText(R.string.welcome_message + username + "!");
        } else {
            Log.d("MenuPrincipal", "txtWelcome is null");
        }

        databaseHelper = new DatabaseHelper(this);
        listaSupermercados = new ArrayList<>();

        cargarSupermercadosDesdeDB();

        RecyclerView recyclerView = findViewById(R.id.recyclerViewSupermercados);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        supermercadosAdapter = new SupermercadosAdapter(listaSupermercados, this);
        recyclerView.setAdapter(supermercadosAdapter);

        Button btnAgregarSupermercado = findViewById(R.id.btnAgregarSupermercado);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            btnAgregarSupermercado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogAgregarSupermercado dialog = new DialogAgregarSupermercado(MenuPrincipal.this, MenuPrincipal.this);
                    dialog.show();
                }
            });
        }
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
        Log.d("MenuPrincipal", "Este no funciona");
    }

    @Override
    public void onSupermercadoClick(int position, Supermercado supermercado) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d("MenuPrincipal","Has clickado");
            ProductosFragment productosFragment = new ProductosFragment();
            Bundle args = new Bundle();
            args.putString("nombreSupermercado", supermercado.getNombre());
            productosFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.productoContainer, productosFragment)
                    .commit();
        } else {
            Intent intent = new Intent(MenuPrincipal.this, ProductosSupermercadoActivity.class);
            intent.putExtra("NOMBRE_SUPERMERCADO", supermercado.getNombre());
            intent.putExtra("LOCALIZACION_SUPERMERCADO", supermercado.getLocalizacion());
            startActivity(intent);
        }
    }

    @Override
    public void seleccionarElemento(String elemento) {
        Log.d("MenuPrincipal", "Has seleccionado un elemento " + elemento);
    }
}
