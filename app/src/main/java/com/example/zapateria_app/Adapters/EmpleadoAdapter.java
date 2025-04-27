package com.example.zapateria_app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zapateria_app.DAO.EmpleadoDAO;
import com.example.zapateria_app.Models.Empleado;
import com.example.zapateria_app.R;

import java.util.List;

public class EmpleadoAdapter extends RecyclerView.Adapter<EmpleadoAdapter.EmpleadoViewHolder> {

    private List<Empleado> empleados;
    private final Context context;



    public EmpleadoAdapter(List<Empleado> empleados, Context context) {
        this.empleados = empleados;
        this.context = context;
    }

    @NonNull
    @Override
    public EmpleadoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_empleado, parent, false); // Cambiar a layout correcto
        return new EmpleadoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmpleadoViewHolder holder, int position) {
        Empleado empleado = empleados.get(position);
        holder.bind(empleado);
    }

    @Override
    public int getItemCount() {
        return empleados != null ? empleados.size() : 0;
    }

    public void updateData(List<Empleado> nuevosEmpleados) {
        this.empleados = nuevosEmpleados;
        notifyDataSetChanged();
    }

    public class EmpleadoViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNombre;
        private final TextView tvPuesto;


        public EmpleadoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvEmpleadoNombre);
            tvPuesto = itemView.findViewById(R.id.tvEmpleadoPuesto);
        }

        public void bind(Empleado empleado) {
            tvNombre.setText(empleado.getNombre());
            tvPuesto.setText(empleado.getPuesto());
        }
    }
}