package com.example.supermercado;

import java.util.List;

public class Supermercado {
    private String nombre;
    private String localizacion;
    private List<Producto> listaProductos;

    public Supermercado(String nombre, String localizacion, List<Producto> listaProductos) {
        this.nombre = nombre;
        this.localizacion = localizacion;
        this.listaProductos = listaProductos;
    }

    public String getNombre() {
        return nombre;
    }

    public String getLocalizacion() {
        return localizacion;
    }

    public List<Producto> getListaProductos() {
        return listaProductos;
    }
}

