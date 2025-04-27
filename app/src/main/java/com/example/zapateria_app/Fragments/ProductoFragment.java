package com.example.zapateria_app.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zapateria_app.Adapters.ProductoAdapter;
import com.example.zapateria_app.DAO.ProductoDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.example.zapateria_app.R;
import com.example.zapateria_app.database.databaseZapateria;

public class ProductoFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ProductoAdapter adapter;
    private List<ProductoDAO.ProductoConStock> listaProductos = new ArrayList<>();
    private Executor executor = Executors.newSingleThreadExecutor();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout
        View view = inflater.inflate(R.layout.fragment_producto, container, false);

        // Inicializar vistas
        recyclerView = view.findViewById(R.id.rvProductos);
        progressBar = view.findViewById(R.id.progressBar);

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProductoAdapter(listaProductos,getContext());
        recyclerView.setAdapter(adapter);

        // Cargar datos
        cargarProductos();

        return view;
    }

    private void cargarProductos() {
        progressBar.setVisibility(View.VISIBLE);

        executor.execute(() -> {
            try {
                // Obtener instancia de la base de datos
                    databaseZapateria db =databaseZapateria.getInstance(getContext());
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