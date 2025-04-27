package com.example.zapateria_app.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zapateria_app.Adapters.ProductoAdapter;
import com.example.zapateria_app.DAO.ProductoDAO;
import com.example.zapateria_app.Dialogs.ConfirmarCompraDialog;
import com.example.zapateria_app.R;
import com.example.zapateria_app.database.databaseZapateria;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ProductoFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ProductoAdapter adapter;
    private List<ProductoDAO.ProductoConStock> listaProductos = new ArrayList<>();
    private Executor executor = Executors.newSingleThreadExecutor();
    private TextView tvTotalItems, tvTotalValue;
    private Button btnComprar;
    private double totalValue = 0.0;
    private int totalItems = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_producto, container, false);

        // Inicializar vistas
        recyclerView = view.findViewById(R.id.rvProductos);
        progressBar = view.findViewById(R.id.progressBar);
        tvTotalItems = view.findViewById(R.id.tvTotalItems);
        tvTotalValue = view.findViewById(R.id.tvTotalValue);
        btnComprar = view.findViewById(R.id.btnComprar);

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProductoAdapter(listaProductos, getContext());
        recyclerView.setAdapter(adapter);

        // Configurar el listener para cambios en las cantidades
        adapter.setOnCantidadChangeListener((totalItems, totalValue) -> {
            this.totalValue = totalValue;
            this.totalItems = totalItems;
            tvTotalItems.setText(String.valueOf(totalItems));
            tvTotalValue.setText(String.format("$%.2f", totalValue));

            // Habilitar/deshabilitar botón de compra según si hay productos
            btnComprar.setEnabled(totalItems > 0);
        });

        // Configurar el botón de compra
        btnComprar.setOnClickListener(v -> {
            if (totalItems > 0) {
                abrirDialogoCompra();
            } else {
                Toast.makeText(getContext(), "Agregue productos al carrito primero", Toast.LENGTH_SHORT).show();
            }
        });

        // Inicialmente deshabilitar el botón de compra
        btnComprar.setEnabled(false);

        // Cargar datos
        cargarProductos();

        return view;
    }

    private void abrirDialogoCompra() {
        // Obtener productos seleccionados con sus cantidades
        List<ProductoDAO.ProductoConStock> productosSeleccionados = new ArrayList<>();
        List<Integer> cantidades = new ArrayList<>();

        Map<Integer, Integer> cantidadesMap = adapter.getCantidadesSeleccionadas();

        for (Map.Entry<Integer, Integer> entry : cantidadesMap.entrySet()) {
            int position = entry.getKey();
            int cantidad = entry.getValue();
            if (cantidad > 0) {
                productosSeleccionados.add(listaProductos.get(position));
                cantidades.add(cantidad);
            }
        }

        ConfirmarCompraDialog dialogFragment = ConfirmarCompraDialog.newInstance(
                productosSeleccionados,
                cantidades,
                totalValue
        );

        dialogFragment.setCompraConfirmadaListener(idVenta -> {
            Toast.makeText(getContext(), "Compra realizada con éxito", Toast.LENGTH_SHORT).show();
            cargarProductos();
            adapter.resetCantidades();
            totalValue = 0.0;
            totalItems = 0;
            tvTotalItems.setText("0");
            tvTotalValue.setText("$0.00");
            btnComprar.setEnabled(false);
        });

        dialogFragment.show(getParentFragmentManager(), "CompraDialog");
    }

    private void cargarProductos() {
        progressBar.setVisibility(View.VISIBLE);

        executor.execute(() -> {
            try {
                // Obtener instancia de la base de datos
                databaseZapateria db = databaseZapateria.getInstance(getContext());
                ProductoDAO productoDAO = db.productoDAO();

                // Obtener productos de la base de datos
                List<ProductoDAO.ProductoConStock> productos = productoDAO.getAllProductosConStock();

                // Actualizar UI en el hilo principal
                requireActivity().runOnUiThread(() -> {
                    listaProductos.clear();
                    listaProductos.addAll(productos);
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);

                    if (productos.isEmpty()) {
                        Toast.makeText(getContext(), "No hay productos en el inventario", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error al cargar productos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}