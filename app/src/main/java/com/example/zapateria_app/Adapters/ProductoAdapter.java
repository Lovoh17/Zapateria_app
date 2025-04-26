package com.example.zapateria_app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zapateria_app.DAO.ProductoDAO;
import com.example.zapateria_app.R;

import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {

    private final List<ProductoDAO.ProductoConStock> productos;
    private final Context context;

    public ProductoAdapter(List<ProductoDAO.ProductoConStock> productos, Context context) {
        this.productos = productos;
        this.context = context; 
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
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public class ProductoViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNombre;
        private final TextView tvMarca;
        private final TextView tvTalla;
        private final TextView tvPrecio;
        private final TextView tvStock;
        private final TextView tvProductCategory;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvProductName);
            tvMarca = itemView.findViewById(R.id.tvProductBrand);
            tvTalla = itemView.findViewById(R.id.tvProductSize);
            tvPrecio = itemView.findViewById(R.id.tvProductPrice);
            tvStock = itemView.findViewById(R.id.tvProductStock);
            tvProductCategory = itemView.findViewById(R.id.tvProductCategory);
        }

        public void bind(ProductoDAO.ProductoConStock producto) {
            tvNombre.setText(producto.getNombre());
            tvMarca.setText(producto.getMarca());
            tvTalla.setText(String.format("Talla: %d", producto.getTalla()));
            tvPrecio.setText(String.format("$%.2f", producto.getPrecio()));
            tvProductCategory.setText(producto.getNombreCategoria() + "ds");

            int stock = producto.getStock();
            String stockText = "Stock: " + (stock > 0 ? stock : 0);
            tvStock.setText(stockText);

        }
    }
}