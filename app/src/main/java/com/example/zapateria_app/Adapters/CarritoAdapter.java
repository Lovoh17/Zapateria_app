package com.example.zapateria_app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zapateria_app.DAO.ProductoDAO;
import com.example.zapateria_app.R;

import java.util.List;
import java.util.Map;

public class CarritoAdapter extends RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder> {

    private final List<ProductoDAO.ProductoConStock> productos;
    private final Map<Integer, Integer> cantidades;
    private final Context context;

    public CarritoAdapter(List<ProductoDAO.ProductoConStock> productos,
                          Map<Integer, Integer> cantidades,
                          Context context) {
        this.productos = productos;
        this.cantidades = cantidades;
        this.context = context;
    }

    @NonNull
    @Override
    public CarritoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_carrito, parent, false);
        return new CarritoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarritoViewHolder holder, int position) {
        ProductoDAO.ProductoConStock producto = productos.get(position);
        int cantidad = cantidades.get(position);
        double subtotal = cantidad * producto.getPrecio();

        holder.tvNombre.setText(producto.getNombre());
        holder.tvCantidad.setText(String.valueOf(cantidad));
        holder.tvPrecioUnitario.setText(String.format("$%.2f", producto.getPrecio()));
        holder.tvSubtotal.setText(String.format("$%.2f", subtotal));
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public static class CarritoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvCantidad, tvPrecioUnitario, tvSubtotal;

        public CarritoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreProducto);
            tvCantidad = itemView.findViewById(R.id.tvCantidad);
            tvPrecioUnitario = itemView.findViewById(R.id.tvPrecioUnitario);
            tvSubtotal = itemView.findViewById(R.id.tvSubtotal);
        }
    }
}