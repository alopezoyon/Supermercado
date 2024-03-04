package com.example.supermercado;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProductosFragment extends ListFragment {
    private DatabaseHelper databaseHelper;
    private List<Producto> listaProductos;
    private String nombreSupermercado;
    private listenerDelFragment elListener;

    public ProductosFragment() {
    }

    public interface listenerDelFragment {
        void seleccionarElemento(String elemento);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("nombreSupermercado", nombreSupermercado);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();

        if (args != null) {
            String nombreSupermercado = args.getString("nombreSupermercado");
            Log.d("ProductosFragment","Ahora sí tato");

            if (nombreSupermercado != null) {
                cargarProductos(nombreSupermercado);
            }
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_productos, container, false);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            elListener = (listenerDelFragment) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " debe implementar listenerDelFragment");
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String elemento = listaProductos.get(position).toString();
        elListener.seleccionarElemento(elemento);
    }

    public void cargarProductos(String nombreSupermercado) {
        listaProductos.clear();
        listaProductos.addAll(databaseHelper.getProductosPorSupermercado(nombreSupermercado));
        Log.d("ProductosFragment", "Selected Supermarket: " + nombreSupermercado);
        for (Producto producto : listaProductos) {
            Log.d("ProductosFragment", "Product: " + producto.getNombre() + ", Price: " + producto.getPrecio());
        }
    }
}