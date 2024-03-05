package com.example.supermercado;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
    private listenerDelFragment elListener;

    public ProductosFragment() {
    }

    public interface listenerDelFragment {
        void seleccionarElemento(String elemento);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseHelper = new DatabaseHelper(requireContext());

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
        listaProductos = new ArrayList<>();
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

        ArrayAdapter<Producto> adapter = new ArrayAdapter<Producto>(
                requireContext(),
                android.R.layout.simple_list_item_2,
                android.R.id.text1,
                listaProductos
        ) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                Producto producto = (Producto) getItem(position);
                if (producto != null) {
                    String displayText = producto.getNombre() + " " + producto.getPrecio() + "€";
                    ((android.widget.TextView) view.findViewById(android.R.id.text1)).setText(displayText);
                }

                return view;
            }
        };

        setListAdapter(adapter);

        Log.d("ProductosFragment", "Selected Supermarket: " + nombreSupermercado);
        for (Producto producto : listaProductos) {
            Log.d("ProductosFragment", "Product: " + producto.getNombre() + ", Price: " + producto.getPrecio());
        }
    }
}
