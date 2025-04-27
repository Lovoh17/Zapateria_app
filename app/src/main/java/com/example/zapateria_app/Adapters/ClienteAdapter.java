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
import com.example.zapateria_app.Models.Cliente;
import com.example.zapateria_app.R;

import java.util.List;

public class ClienteAdapter extends RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder> {

    private List<Cliente> clientes;
    private final Context context;
    private final OnClienteClickListener listener;

    public interface OnClienteClickListener {
        void onEditarClick(Cliente cliente);
        void onBorrarClick(Cliente cliente);

        void onEditarClick(ClienteDAO.ClienteConVentas cliente);

        void onBorrarClick(ClienteDAO.ClienteConVentas cliente);
    }

    public ClienteAdapter(List<Cliente> clientes, Context context, OnClienteClickListener listener) {
        this.clientes = clientes;
        this.context = context;
        this.listener = listener;
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
        Cliente cliente = clientes.get(position);
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
        return clientes != null ? clientes.size() : 0;
    }

    public void actualizarClientes(List<Cliente> nuevosClientes) {
        this.clientes = nuevosClientes;
        notifyDataSetChanged();
    }

    public static class ClienteViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNombre;
        private final TextView tvTelefono;
        private final TextView tvCorreo;
        private final ImageButton btnEditar;
        private final ImageButton btnBorrar;

        public ClienteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvClienteNombre);
            tvTelefono = itemView.findViewById(R.id.tvClienteTelefono);
            tvCorreo = itemView.findViewById(R.id.tvClienteCorreo);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnBorrar = itemView.findViewById(R.id.btnBorrar);
        }

        public void bind(Cliente cliente) {
            tvNombre.setText(cliente.getNombre());
            tvTelefono.setText(cliente.getTelefono() != null ?
                    "Teléfono: " + cliente.getTelefono() : "");
            tvCorreo.setText(cliente.getCorreo() != null ?
                    "Correo: " + cliente.getCorreo() : "");
        }
    }
}