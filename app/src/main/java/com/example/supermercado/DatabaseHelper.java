package com.example.supermercado;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "your_database_name";
    private static final int DATABASE_VERSION = 4;
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String TABLE_PRODUCTOS = "productos";
    private static final String COLUMN_PRODUCTO_NOMBRE = "nombre";
    private static final String COLUMN_PRODUCTO_PRECIO = "precio";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUserTableQuery = "CREATE TABLE " + TABLE_USERS +
                " (" + COLUMN_USERNAME + " TEXT PRIMARY KEY, " +
                COLUMN_PASSWORD + " TEXT)";
        db.execSQL(createUserTableQuery);

        String createProductosTableQuery = "CREATE TABLE " + TABLE_PRODUCTOS +
                " (" + COLUMN_PRODUCTO_NOMBRE + " TEXT PRIMARY KEY, " +
                COLUMN_PRODUCTO_PRECIO + " TEXT)";
        db.execSQL(createProductosTableQuery);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*
        if (oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTOS);
            onCreate(db);
        }

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
        // Asegúrate de manejar las contraseñas de manera segura (hashing, etc.) en una aplicación real
        String insertQuery = "INSERT INTO " + TABLE_USERS + " VALUES('" + username + "', '" + password + "')";
        db.execSQL(insertQuery);
        db.close();
    }

    public List<Producto> getProductos() {
        List<Producto> listaProductos = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_PRODUCTOS + ";";

        Cursor cursor = db.rawQuery(selectQuery, null);

        while (cursor.moveToNext()) {
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCTO_NOMBRE));
            double precio = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCTO_PRECIO));

            listaProductos.add(new Producto(nombre, precio));
        }

        cursor.close();
        db.close();

        return listaProductos;
    }


    public void addProducto(String nombre, double precio) {
        SQLiteDatabase db = this.getWritableDatabase();
        String insertQuery = "INSERT INTO " + TABLE_PRODUCTOS + " VALUES('" + nombre + "', " + precio + ")";
        db.execSQL(insertQuery);
        db.close();
    }

    public void deleteProducto(String nombre) {
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + TABLE_PRODUCTOS + " WHERE " + COLUMN_PRODUCTO_NOMBRE + " = '" + nombre + "'";
        db.execSQL(deleteQuery);
        db.close();
    }

    public void updatePrecioProducto(String nombre, double nuevoPrecio) {
        SQLiteDatabase db = this.getWritableDatabase();
        String updateQuery = "UPDATE " + TABLE_PRODUCTOS + " SET " + COLUMN_PRODUCTO_PRECIO + " = " + nuevoPrecio +
                " WHERE " + COLUMN_PRODUCTO_NOMBRE + " = '" + nombre + "'";
        db.execSQL(updateQuery);
        db.close();
    }
}
