package com.example.supermercado;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        loadPreferences();

        String username = getIntent().getStringExtra("USERNAME_EXTRA");
        Log.d("MenuPrincipal", "Estoy en modo " + getResources().getConfiguration().orientation);

        TextView txtWelcome = findViewById(R.id.txtWelcome);

        if (txtWelcome != null) {
            txtWelcome.setText(getString(R.string.welcome_message) + username + "!");
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

    private void loadPreferences() {
        loadSavedLanguage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_nota, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_change_language) {
            mostrarDialogoAgregarNota();
            return true;
        }
        else {
            return true;
        }
    }

    private void guardarNotaEnArchivo(String nota) {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "nota_" + timeStamp + ".txt";

            OutputStreamWriter fichero = new OutputStreamWriter(openFileOutput(fileName, Context.MODE_PRIVATE));
            fichero.write(nota);
            fichero.close();
            mostrarNotificacion(fileName);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar la nota en el archivo.", Toast.LENGTH_SHORT).show();
        }
    }
    private void mostrarNotificacion(String filename) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new
                    String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 11);
        }

        NotificationManager elManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(this, "IdCanal");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel elCanal= new NotificationChannel("IdCanal", "NombreCanal",
                    NotificationManager.IMPORTANCE_DEFAULT);
            elCanal.setDescription("Descripción del canal");
            elCanal.enableLights(true);
            elCanal.setLightColor(Color.RED);
            elCanal.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            elCanal.enableVibration(true);
            elManager.createNotificationChannel(elCanal);
        }

        elBuilder.setSmallIcon(android.R.drawable.stat_sys_warning)
                .setContentTitle(getString(R.string.message_alert))
                .setContentText(R.string.noti_de + " " + R.string.app_name)
                .setVibrate(new long[]{0, 1000, 500, 1000})
                .setAutoCancel(true);

        Intent i = new Intent(this, VerNotaActivity.class);
        i.putExtra("ARCHIVO_NOTA", filename);
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(this,
                    0, i, PendingIntent.FLAG_UPDATE_CURRENT| PendingIntent.FLAG_IMMUTABLE); }
        else { pendingIntent = PendingIntent.getActivity(this,
                0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        elBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher_foreground))
                .setSmallIcon(android.R.drawable.stat_sys_warning)
                .setContentTitle(getString(R.string.message_alert))
                .setContentText(R.string.noti_de + " " + R.string.app_name)
                .setVibrate(new long[]{0, 1000, 500, 1000})
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        elManager.notify(1, elBuilder.build());
    }

    private void mostrarDialogoAgregarNota() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Agregar Nota");
        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nota = input.getText().toString();
                if (!nota.isEmpty()) {
                    guardarNotaEnArchivo(nota);
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

    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration configuration = getResources().getConfiguration();
        configuration.setLocale(locale);

        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());

    }

    private void loadSavedLanguage() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String savedLanguage = preferences.getString("language", "");

        if (!savedLanguage.isEmpty()) {
            setLocale(savedLanguage);
        }
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