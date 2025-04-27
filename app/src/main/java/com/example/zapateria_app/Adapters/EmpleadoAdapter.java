package com.example.zapateria_app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zapateria_app.Models.Empleado;
import com.example.zapateria_app.R;

import java.util.List;

public class EmpleadoAdapter extends RecyclerView.Adapter<EmpleadoAdapter.EmpleadoViewHolder> {

    private List<Empleado> empleados;
    private final Context context;
    private OnEmpleadoClickListener listener;

    public EmpleadoAdapter(List<Empleado> empleados, Context context, OnEmpleadoClickListener listener) {
        this.empleados = empleados;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EmpleadoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_empleado, parent, false);
        return new EmpleadoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmpleadoViewHolder holder, int position) {
        Empleado empleado = empleados.get(position);
        holder.bind(empleado);

        holder.btnEditar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditarClick(empleado); // Corregido: usar empleado en lugar de cliente
            }
        });

        holder.btnBorrar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBorrarClick(empleado); // Corregido: usar empleado en lugar de cliente
            }
        });
    }

    @Override
    public int getItemCount() {
        return empleados != null ? empleados.size() : 0;
    }

    public void updateData(List<Empleado> nuevosEmpleados) {
        this.empleados = nuevosEmpleados;
        notifyDataSetChanged();
    }

    public interface OnEmpleadoClickListener {
        void onEditarClick(Empleado empleado);
        void onBorrarClick(Empleado empleado);
    }

    public static class EmpleadoViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNombre;
        private final TextView tvPuesto;
        private final ImageButton btnEditar;
        private final ImageButton btnBorrar;

        public EmpleadoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvEmpleadoNombre);
            tvPuesto = itemView.findViewById(R.id.tvEmpleadoPuesto);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnBorrar = itemView.findViewById(R.id.btnBorrar);
        }

        public void bind(Empleado empleado) {
            tvNombre.setText(empleado.getNombre());
            tvPuesto.setText(empleado.getPuesto());
        }
    }
}