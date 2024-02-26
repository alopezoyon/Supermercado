package com.example.supermercado;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "supermercado_database";
    private static final int DATABASE_VERSION = 18;
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String TABLE_SUPERMERCADOS = "supermercados";
    private static final String COLUMN_SUPERMERCADO_NOMBRE = "nombre_super";
    private static final String COLUMN_SUPERMERCADO_LOCALIZACION = "localizacion";
    private static final String COLUMN_PRODUCTO_NOMBRE = "nombre_prod";
    private static final String COLUMN_PRODUCTO_PRECIO = "precio";
    private static final String TABLE_PRODUCTOS_SUPERMERCADO = "productos_supermercado";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUserTableQuery = "CREATE TABLE " + TABLE_USERS +
                " (" + COLUMN_USERNAME + " TEXT PRIMARY KEY, " +
                COLUMN_PASSWORD + " TEXT)";
        db.execSQL(createUserTableQuery);
        Log.d("DatabaseCreation", "Tabla de usuarios creada");

        String createSupermercadosTableQuery = "CREATE TABLE " + TABLE_SUPERMERCADOS +
                " (" + COLUMN_SUPERMERCADO_NOMBRE + " TEXT PRIMARY KEY, " +
                COLUMN_SUPERMERCADO_LOCALIZACION + " TEXT)";
        db.execSQL(createSupermercadosTableQuery);
        Log.d("DatabaseCreation", "Tabla de supermercados creada");

        String createProductosSupermercadoTableQuery = "CREATE TABLE " + TABLE_PRODUCTOS_SUPERMERCADO +
                " (" + COLUMN_SUPERMERCADO_NOMBRE + " TEXT, " +
                COLUMN_PRODUCTO_NOMBRE + " TEXT PRIMARY KEY, " +
                COLUMN_PRODUCTO_PRECIO + " REAL, " +
                " FOREIGN KEY(" + COLUMN_SUPERMERCADO_NOMBRE + ") REFERENCES " + TABLE_SUPERMERCADOS + "(" + COLUMN_SUPERMERCADO_NOMBRE + "))";
        db.execSQL(createProductosSupermercadoTableQuery);
        Log.d("DatabaseCreation", "Tabla de productos de supermercado creada");
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUPERMERCADOS);
        db.execSQL("DROP TABLE IF EXISTS " + "productos");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTOS_SUPERMERCADO);

        onCreate(db);

         */
    }


    public boolean isValidCredentials(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_USERS,
                new String[]{COLUMN_USERNAME, COLUMN_PASSWORD},
                COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?",
                new String[]{username, password},
                null,
                null,
                null
        );

        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return isValid;
    }

    public void addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        db.insert(TABLE_USERS, null, values);
        db.close();
    }

    public List<Supermercado> getSupermercados() {
        List<Supermercado> listaSupermercados = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SUPERMERCADOS, null);

        while (cursor.moveToNext()) {
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUPERMERCADO_NOMBRE));
            String localizacion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUPERMERCADO_LOCALIZACION));
            List<Producto> productos = getProductosPorSupermercado(nombre);

            listaSupermercados.add(new Supermercado(nombre, localizacion, productos));
        }

        cursor.close();
        db.close();
        return listaSupermercados;
    }

    public List<Producto> getProductosPorSupermercado(String nombreSupermercado) {
        List<Producto> listaProductos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTOS_SUPERMERCADO +
                " WHERE " + COLUMN_SUPERMERCADO_NOMBRE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{nombreSupermercado});

        while (cursor.moveToNext()) {
            String nombreProducto = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCTO_NOMBRE));
            double precio = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCTO_PRECIO));
            listaProductos.add(new Producto(nombreProducto, precio));
        }

        cursor.close();
        Log.d("GetProductosPorSupermercado", "La lista tiene "+ listaProductos.size() + " elementos");

        return listaProductos;
    }

    public void addSupermercado(String nombre, String localizacion) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SUPERMERCADO_NOMBRE, nombre);
        values.put(COLUMN_SUPERMERCADO_LOCALIZACION, localizacion);
        db.insert(TABLE_SUPERMERCADOS, null, values);
        db.close();
    }

    public void addProductoASupermercado(String nombreSupermercado, String nombreProducto, Double precio) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SUPERMERCADO_NOMBRE, nombreSupermercado);
        values.put(COLUMN_PRODUCTO_NOMBRE, nombreProducto);
        values.put(COLUMN_PRODUCTO_PRECIO, precio);
        db.insert(TABLE_PRODUCTOS_SUPERMERCADO, null, values);
        db.close();
    }

}
