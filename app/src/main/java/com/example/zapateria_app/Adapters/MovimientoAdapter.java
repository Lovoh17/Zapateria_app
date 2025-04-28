package com.example.zapateria_app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zapateria_app.POJO.MovimientoConProducto;
import com.example.zapateria_app.R;

import java.util.List;

import io.reactivex.annotations.NonNull;

public class MovimientoAdapter extends RecyclerView.Adapter<MovimientoAdapter.MovimientoViewHolder> {

    private List<MovimientoConProducto> movimientos;
    private Context context;

    public MovimientoAdapter(List<MovimientoConProducto> movimientos, Context context) {
        this.movimientos = movimientos;
        this.context = context;
    }

    @NonNull
    @Override
    public MovimientoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movimiento_producto, parent, false);
        return new MovimientoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovimientoViewHolder holder, int position) {
        MovimientoConProducto movimiento = movimientos.get(position);

        if (movimiento.getTipo().equalsIgnoreCase("ENTRADA")) {
            holder.tvTipoMovimiento.setTextColor(ContextCompat.getColor(context, R.color.success_soft_green));
        } else {
            holder.tvTipoMovimiento.setTextColor(ContextCompat.getColor(context, R.color.error_soft_red));
        }

        holder.tvTipoMovimiento.setText(movimiento.getTipo());
        holder.tvFechaMovimiento.setText(movimiento.getFecha());
        holder.tvNombreProducto.setText(movimiento.getNombreProducto());
        holder.tvMarcaProducto.setText(movimiento.getMarcaProducto());
        holder.tvCantidad.setText(String.format("Cantidad: %d", movimiento.getCantidad()));
        holder.tvCostoUnitario.setText(String.format("Costo: $%.2f", movimiento.getCostoUnitario()));

        // Calcular y mostrar total
        double total = movimiento.getCantidad() * movimiento.getCostoUnitario();
        holder.tvTotalMovimiento.setText(String.format("Total: $%.2f", total));
    }

    @Override
    public int getItemCount() {
        return movimientos.size();
    }

    public static class MovimientoViewHolder extends RecyclerView.ViewHolder {
        TextView tvTipoMovimiento, tvFechaMovimiento, tvNombreProducto,
                tvMarcaProducto, tvCantidad, tvCostoUnitario, tvTotalMovimiento;

        public MovimientoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTipoMovimiento = itemView.findViewById(R.id.tvTipoMovimiento);
            tvFechaMovimiento = itemView.findViewById(R.id.tvFechaMovimiento);
            tvNombreProducto = itemView.findViewById(R.id.tvNombreProducto);
            tvMarcaProducto = itemView.findViewById(R.id.tvMarcaProducto);
            tvCantidad = itemView.findViewById(R.id.tvCantidad);
            tvCostoUnitario = itemView.findViewById(R.id.tvCostoUnitario);
            tvTotalMovimiento = itemView.findViewById(R.id.tvTotalMovimiento);
        }
    }

    public void updateData(List<MovimientoConProducto> nuevosMovimientos) {
        movimientos = nuevosMovimientos;
        notifyDataSetChanged();
    }
}