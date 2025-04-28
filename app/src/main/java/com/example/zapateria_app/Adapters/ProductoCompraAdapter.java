package com.example.zapateria_app.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zapateria_app.DAO.ProductoDAO;
import com.example.zapateria_app.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductoCompraAdapter extends RecyclerView.Adapter<ProductoCompraAdapter.ProductoCompraViewHolder> {

    private final List<ProductoDAO.ProductoConStock> productos;
    private final Context context;
    private OnCompraChangeListener compraChangeListener;
    private final Map<Integer, Integer> cantidadesSeleccionadas = new HashMap<>();
    private final Map<Integer, Double> subtotales = new HashMap<>();
    private final Map<Integer, Double> nuevosCostos = new HashMap<>();

    public interface OnCompraChangeListener {
        void onCompraChanged(int totalItems, double totalValue);
        void onNuevoCostoChanged(int position, double nuevoCosto);
    }

    public ProductoCompraAdapter(List<ProductoDAO.ProductoConStock> productos, Context context) {
        this.productos = productos;
        this.context = context;
        inicializarCantidades();
    }

    private void inicializarCantidades() {
        cantidadesSeleccionadas.clear();
        subtotales.clear();
        nuevosCostos.clear();
        for (int i = 0; i < productos.size(); i++) {
            cantidadesSeleccionadas.put(i, 0);
            subtotales.put(i, 0.0);
            nuevosCostos.put(i, productos.get(i).getCostoPromedio());
        }
    }

    public void setOnCompraChangeListener(OnCompraChangeListener listener) {
        this.compraChangeListener = listener;
    }

    @NonNull
    @Override
    public ProductoCompraViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_compra, parent, false);
        return new ProductoCompraViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoCompraViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ProductoDAO.ProductoConStock producto = productos.get(position);
        holder.bind(producto);

        // Configurar listeners para actualización en tiempo real
        holder.etCantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int cantidad = s.toString().isEmpty() ? 0 : Integer.parseInt(s.toString());
                    double nuevoCosto = nuevosCostos.getOrDefault(position, producto.getCostoPromedio());

                    cantidadesSeleccionadas.put(position, cantidad);
                    actualizarSubtotal(holder, cantidad, nuevoCosto);
                    actualizarTotales();
                } catch (NumberFormatException e) {
                    holder.etCantidad.setText("0");
                }
            }
        });

        holder.etNewCost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    double nuevoCosto = s.toString().isEmpty() ? 0 : Double.parseDouble(s.toString());
                    int cantidad = cantidadesSeleccionadas.getOrDefault(position, 0);

                    nuevosCostos.put(position, nuevoCosto);
                    holder.tvCosto.setText(String.format("Costo: $%.2f", nuevoCosto));

                    actualizarSubtotal(holder, cantidad, nuevoCosto);

                    if (compraChangeListener != null) {
                        compraChangeListener.onNuevoCostoChanged(position, nuevoCosto);
                    }
                    actualizarTotales();
                } catch (NumberFormatException e) {
                    holder.etNewCost.setText(String.format("%.2f", producto.getCostoPromedio()));
                }
            }
        });
    }

    private void actualizarSubtotal(ProductoCompraViewHolder holder, int cantidad, double costo) {
        double subtotal = cantidad * costo;
        subtotales.put(holder.getAdapterPosition(), subtotal);
        holder.tvSubtotal.setText(String.format("Subtotal: $%.2f", subtotal));
    }

    private void actualizarTotales() {
        int totalItems = 0;
        double totalValue = 0.0;

        for (int i = 0; i < productos.size(); i++) {
            Integer cantidad = cantidadesSeleccionadas.get(i);
            if (cantidad != null) {
                totalItems += cantidad;
            }
            Double subtotal = subtotales.get(i);
            if (subtotal != null) {
                totalValue += subtotal;
            }
        }

        if (compraChangeListener != null) {
            compraChangeListener.onCompraChanged(totalItems, totalValue);
        }
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public Map<Integer, Integer> getCantidadesSeleccionadas() {
        return new HashMap<>(cantidadesSeleccionadas);
    }

    public Map<Integer, Double> getNuevosCostos() {
        return new HashMap<>(nuevosCostos);
    }

    public static class ProductoCompraViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNombre, tvMarca, tvCategoria, tvPrecio, tvStock, tvSubtotal, tvCosto;
        private final EditText etCantidad, etNewCost;

        public ProductoCompraViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvProductName);
            tvMarca = itemView.findViewById(R.id.tvProductBrand);
            tvCategoria = itemView.findViewById(R.id.tvProductCategory);
            tvPrecio = itemView.findViewById(R.id.tvProductPrice);
            tvStock = itemView.findViewById(R.id.tvProductStock);
            tvSubtotal = itemView.findViewById(R.id.tvSubtotal);
            tvCosto = itemView.findViewById(R.id.tvProductCost);
            etCantidad = itemView.findViewById(R.id.etCantidadCompra);
            etNewCost = itemView.findViewById(R.id.etNewCost);
        }

        public void bind(ProductoDAO.ProductoConStock producto) {
            tvNombre.setText(producto.getNombre());
            tvMarca.setText(producto.getMarca());
            tvCategoria.setText(producto.getNombreCategoria());
            tvPrecio.setText(String.format("$%.2f", producto.getPrecio()));
            tvStock.setText(String.format("Stock: %d", producto.getStock()));
            tvCosto.setText(String.format("Costo: $%.2f", producto.getCostoPromedio()));
            tvSubtotal.setText("Subtotal: $0.00");
            etCantidad.setText("0");
            etNewCost.setText(String.format("%.2f", producto.getCostoPromedio()));
        }
    }
}