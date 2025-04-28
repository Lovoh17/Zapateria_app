package com.example.zapateria_app.Adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

    public interface OnCompraChangeListener {
        void onCompraChanged(int totalItems, double totalValue);
    }

    public ProductoCompraAdapter(List<ProductoDAO.ProductoConStock> productos, Context context) {
        this.productos = productos;
        this.context = context;
        inicializarCantidades();
    }

    private void inicializarCantidades() {
        cantidadesSeleccionadas.clear();
        subtotales.clear();
        for (int i = 0; i < productos.size(); i++) {
            cantidadesSeleccionadas.put(i, 0);
            subtotales.put(i, 0.0);
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
    public void onBindViewHolder(@NonNull ProductoCompraViewHolder holder, int position) {
        ProductoDAO.ProductoConStock producto = productos.get(position);
        holder.bind(producto);

        holder.etCantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int cantidad = s.toString().isEmpty() ? 0 : Integer.parseInt(s.toString());
                    cantidadesSeleccionadas.put(position, cantidad);
                    calcularSubtotal(position, cantidad, producto.getPrecio());
                    actualizarTotales();
                } catch (NumberFormatException e) {
                    holder.etCantidad.setText("0");
                }
            }
        });

        holder.btnAplicar.setOnClickListener(v -> {
            String cantidadStr = holder.etCantidad.getText().toString();
            if (!cantidadStr.isEmpty()) {
                int cantidad = Integer.parseInt(cantidadStr);
                if (cantidad > producto.getStock()) {
                    Toast.makeText(context, "No hay suficiente stock", Toast.LENGTH_SHORT).show();
                    holder.etCantidad.setText(String.valueOf(producto.getStock()));
                }
            }
        });
    }

    private void calcularSubtotal(int position, int cantidad, double precio) {
        double subtotal = cantidad * precio;
        subtotales.put(position, subtotal);
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

    public static class ProductoCompraViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNombre, tvMarca, tvCategoria, tvPrecio, tvStock, tvSubtotal;
        private final EditText etCantidad;
        private final Button btnAplicar;

        public ProductoCompraViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvProductName);
            tvMarca = itemView.findViewById(R.id.tvProductBrand);
            tvCategoria = itemView.findViewById(R.id.tvProductCategory);
            tvPrecio = itemView.findViewById(R.id.tvProductPrice);
            tvStock = itemView.findViewById(R.id.tvProductStock);
            tvSubtotal = itemView.findViewById(R.id.tvSubtotal);
            etCantidad = itemView.findViewById(R.id.etCantidadCompra);
            btnAplicar = itemView.findViewById(R.id.btnAplicarCantidad);
        }

        public void bind(ProductoDAO.ProductoConStock producto) {
            tvNombre.setText(producto.getNombre());
            tvMarca.setText(producto.getMarca());
            tvCategoria.setText(producto.getNombreCategoria());
            tvPrecio.setText(String.format("$%.2f", producto.getPrecio()));
            tvStock.setText(String.format("Stock: %d", producto.getStock()));
            tvSubtotal.setText("Subtotal: $0.00");
            etCantidad.setText("0");
        }
    }
}