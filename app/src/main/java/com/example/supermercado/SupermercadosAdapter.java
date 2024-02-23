package com.example.supermercado;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SupermercadosAdapter extends RecyclerView.Adapter<SupermercadosAdapter.SupermercadoViewHolder> {

    private List<Supermercado> supermercados;
    private Context context;
    private OnSupermercadoClickListener listener;

    public SupermercadosAdapter(List<Supermercado> supermercados, OnSupermercadoClickListener listener) {
        this.supermercados = supermercados;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SupermercadoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_supermercados_adapter, parent, false);
        return new SupermercadoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SupermercadoViewHolder holder, int position) {
        Supermercado supermercado = supermercados.get(position);

        holder.txtNombre.setText(supermercado.getNombre());
        holder.txtLocalizacion.setText(supermercado.getLocalizacion());
    }

    @Override
    public int getItemCount() {
        return supermercados.size();
    }

    public class SupermercadoViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre;
        TextView txtLocalizacion;

        public SupermercadoViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombreSupermercado);
            txtLocalizacion = itemView.findViewById(R.id.txtLocalizacionSupermercado);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onSupermercadoClick(position);
                        }
                    }
                }
            });
        }
    }

    public interface OnSupermercadoClickListener {
        void onSupermercadoClick(int position);
    }
}
