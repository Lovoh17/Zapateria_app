package com.example.zapateria_app.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zapateria_app.POJO.DetalleVentaCompleto;
import com.example.zapateria_app.R;

import java.util.List;

public class DetalleVentaAdapter extends RecyclerView.Adapter<DetalleVentaAdapter.DetalleVentaViewHolder> {

    private List<DetalleVentaCompleto> detalles;

    public DetalleVentaAdapter(List<DetalleVentaCompleto> detalles) {
        this.detalles = detalles;
    }

    @NonNull
    @Override
    public DetalleVentaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_detalleventa, parent, false);
        return new DetalleVentaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetalleVentaViewHolder holder, int position) {
        DetalleVentaCompleto detalle = detalles.get(position);

        // Información de la venta
        holder.tvFechaVenta.setText(String.format("Fecha: %s", detalle.getFechaVenta()));
        holder.tvTotalVenta.setText(String.format("Total: $%.2f", detalle.getTotalVenta()));

        // Información del producto
        holder.tvProductoNombre.setText(String.format("%s - %s",
                detalle.getNombreProducto(), detalle.getMarcaProducto()));
        holder.tvCantidad.setText(String.format("Cantidad: %d", detalle.getCantidad()));
        holder.tvPrecioUnitario.setText(String.format("P/U: $%.2f", detalle.getPrecioUnitario()));
        holder.tvSubtotal.setText(String.format("Subtotal: $%.2f", detalle.getSubtotal()));

        // Información de cliente y empleado
        holder.tvCliente.setText(String.format("Cliente: %s (%s)",
                detalle.getNombreCliente(), detalle.getTelefonoCliente()));
        holder.tvEmpleado.setText(String.format("Vendedor: %s (%s)",
                detalle.getNombreEmpleado(), detalle.getPuestoEmpleado()));
    }

    @Override
    public int getItemCount() {
        return detalles.size();
    }

    public void updateData(List<DetalleVentaCompleto> nuevosDetalles) {
        this.detalles = nuevosDetalles;
        notifyDataSetChanged();
    }

    static class DetalleVentaViewHolder extends RecyclerView.ViewHolder {
        TextView tvFechaVenta, tvTotalVenta;
        TextView tvProductoNombre, tvCantidad, tvPrecioUnitario, tvSubtotal;
        TextView tvCliente, tvEmpleado;

        public DetalleVentaViewHolder(@NonNull View itemView) {
            super(itemView);
            // Información de venta
            tvFechaVenta = itemView.findViewById(R.id.tvFechaVenta);
            tvTotalVenta = itemView.findViewById(R.id.tvTotalVenta);

            // Información de producto
            tvProductoNombre = itemView.findViewById(R.id.tvProductoNombre);
            tvCantidad = itemView.findViewById(R.id.tvCantidad);
            tvPrecioUnitario = itemView.findViewById(R.id.tvPrecioUnitario);
            tvSubtotal = itemView.findViewById(R.id.tvSubtotal);

            // Información de cliente y empleado
            tvCliente = itemView.findViewById(R.id.tvCliente);
            tvEmpleado = itemView.findViewById(R.id.tvEmpleado);
        }
    }
}