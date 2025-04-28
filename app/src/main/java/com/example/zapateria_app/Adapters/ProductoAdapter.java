package com.example.zapateria_app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zapateria_app.DAO.ProductoDAO;
import com.example.zapateria_app.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {

    private final List<ProductoDAO.ProductoConStock> productos;
    private final Context context;
    private OnCantidadChangeListener cantidadChangeListener;
    private final Map<Integer, Integer> cantidadesSeleccionadas = new HashMap<>();

    public interface OnCantidadChangeListener {
        void onCantidadChanged(int totalItems, double totalValue);
    }

    public int getCantidadAt(int position) {
        return getCantidadSegura(position);
    }

    public void resetCantidades() {
        for (int i = 0; i < productos.size(); i++) {
            cantidadesSeleccionadas.put(i, 0);
        }
        notifyDataSetChanged();
        actualizarTotales();
    }

    public ProductoAdapter(List<ProductoDAO.ProductoConStock> productos, Context context) {
        this.productos = productos;
        this.context = context;
        inicializarCantidades();
    }

    private void inicializarCantidades() {
        cantidadesSeleccionadas.clear();
        for (int i = 0; i < productos.size(); i++) {
            cantidadesSeleccionadas.put(i, 0);
        }
    }

    public void setOnCantidadChangeListener(OnCantidadChangeListener listener) {
        this.cantidadChangeListener = listener;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        ProductoDAO.ProductoConStock producto = productos.get(position);
        holder.bind(producto);

        // Mostrar costo promedio del producto
        holder.tvCosto.setText(String.format("Costo: $%.2f", producto.getCostoPromedio()));

        int cantidadActual = getCantidadSegura(position);
        holder.tvCantidad.setText(String.valueOf(cantidadActual));

        holder.btnAddItem.setOnClickListener(v -> {
            int nuevaCantidad = getCantidadSegura(position) + 1;
            if (nuevaCantidad <= producto.getStock()) {
                cantidadesSeleccionadas.put(position, nuevaCantidad);
                holder.tvCantidad.setText(String.valueOf(nuevaCantidad));
                actualizarTotales();
            } else {
                Toast.makeText(context, "Stock insuficiente", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnRemoveItem.setOnClickListener(v -> {
            int nuevaCantidad = getCantidadSegura(position) - 1;
            if (nuevaCantidad >= 0) {
                cantidadesSeleccionadas.put(position, nuevaCantidad);
                holder.tvCantidad.setText(String.valueOf(nuevaCantidad));
                actualizarTotales();
            }
        });
    }

    private int getCantidadSegura(int position) {
        Integer cantidad = cantidadesSeleccionadas.get(position);
        return cantidad != null ? cantidad : 0;
    }

    private void actualizarTotales() {
        int totalItems = 0;
        double totalValue = 0.0;

        for (int i = 0; i < productos.size(); i++) {
            int cantidad = getCantidadSegura(i);
            totalItems += cantidad;
            totalValue += cantidad * productos.get(i).getPrecio();
        }

        if (cantidadChangeListener != null) {
            cantidadChangeListener.onCantidadChanged(totalItems, totalValue);
        }
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }



    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNombre, tvMarca, tvTalla, tvPrecio, tvStock, tvProductCategory, tvCantidad, tvCosto;
        private final ImageButton btnAddItem, btnRemoveItem;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvProductName);
            tvMarca = itemView.findViewById(R.id.tvProductBrand);
            tvTalla = itemView.findViewById(R.id.tvProductSize);
            tvPrecio = itemView.findViewById(R.id.tvProductPrice);
            tvStock = itemView.findViewById(R.id.tvProductStock);
            tvProductCategory = itemView.findViewById(R.id.tvProductCategory);
            tvCantidad = itemView.findViewById(R.id.tvCantidad);
            tvCosto = itemView.findViewById(R.id.tvProductCost); // Nuevo TextView para costo
            btnAddItem = itemView.findViewById(R.id.btnAddItem);
            btnRemoveItem = itemView.findViewById(R.id.btnRemoveItem);
        }

        public void bind(ProductoDAO.ProductoConStock producto) {
            tvNombre.setText(producto.getNombre());
            tvMarca.setText(producto.getMarca());
            tvTalla.setText(String.format("Talla: %d", producto.getTalla()));
            tvPrecio.setText(String.format("$%.2f", producto.getPrecio()));
            tvStock.setText(String.format("Stock: %d", producto.getStock()));
            tvProductCategory.setText(producto.getNombreCategoria());
            tvCosto.setText(String.format("Costo: $%.2f", producto.getCostoPromedio())); // Mostrar costo promedio
        }
    }


    public Map<Integer, Integer> getCantidadesSeleccionadas() {
        return new HashMap<>(cantidadesSeleccionadas);
    }
}