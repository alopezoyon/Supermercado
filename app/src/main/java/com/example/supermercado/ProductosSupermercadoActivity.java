package com.example.supermercado;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        txtSupermercado.setText(R.string.producto_de + nombreSupermercado);

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

        setSupportActionBar(findViewById(R.id.toolbar));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_productos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_note) {
            mostrarDialogoAgregarNota();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void mostrarDialogoAgregarNota() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Agregar Nota");
        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nota = input.getText().toString();
                if (!nota.isEmpty()) {
                    guardarNotaEnArchivo(nota);
                    mostrarNotificacion();
                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void guardarNotaEnArchivo(String nota) {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "nota_" + timeStamp + ".txt";

            File dir = new File(Environment.getExternalStorageDirectory(), "NotasSupermercado");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(dir, fileName);
            FileWriter writer = new FileWriter(file);
            writer.append(nota);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar la nota en el archivo.", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Notas";
            String channelDescription = "Canal para mostrar notificaciones de notas";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("canal_notas", channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "canal_notas")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Nota Guardada")
                .setContentText("Tu nota ha sido guardada correctamente.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(1, builder.build());
    }

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


    private void cargarProductosDesdeDB(String nombreSupermercado) {

        listaProductos.clear();
        listaProductos.addAll(databaseHelper.getProductosPorSupermercado(nombreSupermercado));

    }

    @Override
    public void onProductoAdded(String nombre, double precio) {
        databaseHelper.addProductoASupermercado(nombreSupermercado, nombre, precio);
        cargarProductosDesdeDB(nombreSupermercado);
        productosAdapter.notifyDataSetChanged();
    }
}