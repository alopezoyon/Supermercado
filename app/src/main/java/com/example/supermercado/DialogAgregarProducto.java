package com.example.supermercado;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class DialogAgregarProducto extends Dialog {

    private OnProductoAddedListener listener;

    public DialogAgregarProducto(Context context, ProductosSupermercadoActivity listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_agregar_producto);

        final EditText edtNombreProducto = findViewById(R.id.edtNombreProducto);
        final EditText edtPrecioProducto = findViewById(R.id.edtPrecioProducto);
        Button btnAgregar = findViewById(R.id.btnAgregar);
        Button btnCancelar = findViewById(R.id.btnCancelar);

        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombreProducto = edtNombreProducto.getText().toString();
                double precioProducto = Double.parseDouble(edtPrecioProducto.getText().toString());

                if (listener != null) {
                    listener.onProductoAdded(nombreProducto, precioProducto);
                }

                dismiss();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public interface OnProductoAddedListener {
        void onProductoAdded(String nombre, double precio);
    }
}
