package com.example.zapateria_app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zapateria_app.DAO.ClienteDAO;
import com.example.zapateria_app.R;

import java.util.List;

public class ClienteAdapter extends RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder> {

    private List<ClienteDAO.ClienteConVentas> clientes;
    private final Context context;
    private OnClienteClickListener listener;

    public interface OnClienteClickListener {
        void onEditarClick(ClienteDAO.ClienteConVentas cliente);
        void onBorrarClick(ClienteDAO.ClienteConVentas cliente);
    }
    public void setOnClienteClickListener(OnClienteClickListener listener) {
        this.listener = listener;
    }

    public ClienteAdapter(List<ClienteDAO.ClienteConVentas> clientes, Context context) {
        this.clientes = clientes;
        this.context = context;
        if (context instanceof OnClienteClickListener) {
            this.listener = (OnClienteClickListener) context;
        } else {
            throw new RuntimeException(context.toString() +
                    " debe implementar OnClienteClickListener");
        }
    }

    @NonNull
    @Override
    public ClienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cliente, parent, false);
        return new ClienteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClienteViewHolder holder, int position) {
        ClienteDAO.ClienteConVentas cliente = clientes.get(position);
        holder.bind(cliente);

        holder.btnEditar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditarClick(cliente);
            }
        });

        holder.btnBorrar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBorrarClick(cliente);
            }
        });
    }

    @Override
    public int getItemCount() {
        return clientes.size();
    }

    public void setClientes(List<ClienteDAO.ClienteConVentas> nuevosClientes) {
        this.clientes = nuevosClientes;
        notifyDataSetChanged();
    }

    public void updateData(List<ClienteDAO.ClienteConVentas> nuevosClientes) {
        this.clientes = nuevosClientes;
        notifyDataSetChanged();
    }

    public static class ClienteViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNombre;
        private final TextView tvTelefono;
        private final TextView tvCorreo;
        private final TextView tvVentas;
        private final ImageButton btnEditar;
        private final ImageButton btnBorrar;

        public ClienteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvClienteNombre);
            tvTelefono = itemView.findViewById(R.id.tvClienteTelefono);
            tvCorreo = itemView.findViewById(R.id.tvClienteCorreo);
            tvVentas = itemView.findViewById(R.id.tvClienteVentas);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnBorrar = itemView.findViewById(R.id.btnBorrar);
        }

        public void bind(ClienteDAO.ClienteConVentas cliente) {
            tvNombre.setText(cliente.getNombre());
            tvTelefono.setText(String.format("Teléfono: %s", cliente.getTelefono()));
            tvCorreo.setText(String.format("Correo: %s", cliente.getCorreo()));
            tvVentas.setText(String.format("Ventas: %d", cliente.getCantidadVentas()));
        }
    }
}