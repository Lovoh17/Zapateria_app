package com.example.zapateria_app.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zapateria_app.Adapters.ProductoCompraAdapter;
import com.example.zapateria_app.DAO.ProductoDAO;
import com.example.zapateria_app.Dialogs.RegistrarProductoDialog;
import com.example.zapateria_app.R;
import com.example.zapateria_app.database.databaseZapateria;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CompraInventarioFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ProductoCompraAdapter adapter;
    private List<ProductoDAO.ProductoConStock> listaProductos = new ArrayList<>();
    private Executor executor = Executors.newSingleThreadExecutor();
    private Button btnComprar, btnRegistrarProducto;
    private double totalValue = 0.0;
    private int totalItems = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compras, container, false);

        inicializarVistas(view);
        configurarRecyclerView();
        configurarListeners();
        cargarProductos();

        return view;
    }

    private void inicializarVistas(View view) {
        recyclerView = view.findViewById(R.id.rvProductosCompra);
        progressBar = view.findViewById(R.id.progressBar);
        btnComprar = view.findViewById(R.id.btnComprar);
        btnRegistrarProducto = view.findViewById(R.id.btnRegistrarProducto);
        btnComprar.setEnabled(false);
    }

    private void configurarRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProductoCompraAdapter(listaProductos, getContext());
        recyclerView.setAdapter(adapter);
    }

    private void configurarListeners() {
        adapter.setOnCompraChangeListener((totalItems, totalValue) -> {
            this.totalValue = totalValue;
            this.totalItems = totalItems;
            btnComprar.setEnabled(totalItems > 0);
        });

        btnComprar.setOnClickListener(v -> validarYConfirmarCompra());
        btnRegistrarProducto.setOnClickListener(v -> mostrarDialogoRegistroProducto());
    }

    private void validarYConfirmarCompra() {
        if (totalItems > 0) {
            mostrarConfirmacionCompra();
        } else {
            Toast.makeText(getContext(), "Seleccione productos para comprar", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarConfirmacionCompra() {
        List<ProductoDAO.ProductoConStock> productosSeleccionados = new ArrayList<>();
        List<Integer> cantidades = new ArrayList<>();
        obtenerProductosSeleccionados(productosSeleccionados, cantidades);

        new AlertDialog.Builder(getContext())
                .setTitle("Confirmar Compra de Inventario")
                .setMessage("¿Desea registrar la entrada de estos productos al inventario?")
                .setPositiveButton("Confirmar", (dialog, which) -> procesarCompra(productosSeleccionados, cantidades))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void obtenerProductosSeleccionados(List<ProductoDAO.ProductoConStock> productos, List<Integer> cantidades) {
        Map<Integer, Integer> cantidadesMap = adapter.getCantidadesSeleccionadas();
        for (Map.Entry<Integer, Integer> entry : cantidadesMap.entrySet()) {
            int position = entry.getKey();
            int cantidad = entry.getValue();
            if (cantidad > 0) {
                productos.add(listaProductos.get(position));
                cantidades.add(cantidad);
            }
        }
    }

    private void procesarCompra(List<ProductoDAO.ProductoConStock> productos, List<Integer> cantidades) {
        progressBar.setVisibility(View.VISIBLE);

        executor.execute(() -> {
            try {
                databaseZapateria db = databaseZapateria.getInstance(getContext());
                ProductoDAO productoDAO = db.productoDAO();

                for (int i = 0; i < productos.size(); i++) {
                    ProductoDAO.ProductoConStock producto = productos.get(i);
                    int cantidad = cantidades.get(i);

                    // Actualizar stock en inventario
                    productoDAO.aumentarStock(producto.getId(), cantidad);

                    // Actualizar costo promedio si es necesario
                    actualizarCostoPromedio(productoDAO, producto, cantidad);
                }

                requireActivity().runOnUiThread(() -> {
                    mostrarResultadoExitoso();
                    reiniciarSeleccion();
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> mostrarError(e));
            }
        });
    }

    private void actualizarCostoPromedio(ProductoDAO productoDAO, ProductoDAO.ProductoConStock producto, int cantidad) {
        // Opcional: Lógica para actualizar costo promedio
        //productoDAO.actualizarCostoPromedio(producto.getId(), producto.getCostoPromedio(), cantidad);
    }

    private void mostrarResultadoExitoso() {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(getContext(), "Inventario actualizado correctamente", Toast.LENGTH_SHORT).show();
    }

    private void reiniciarSeleccion() {
        cargarProductos();
        totalValue = 0.0;
        totalItems = 0;
        btnComprar.setEnabled(false);
    }

    private void mostrarError(Exception e) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(getContext(), "Error al actualizar inventario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void mostrarDialogoRegistroProducto() {
        RegistrarProductoDialog dialog = RegistrarProductoDialog.newInstance();
        dialog.setProductoRegistradoListener(() -> {
            cargarProductos();
            Toast.makeText(getContext(), "Producto registrado exitosamente", Toast.LENGTH_SHORT).show();
        });
        dialog.show(getParentFragmentManager(), "RegistrarProductoDialog");
    }

    private void cargarProductos() {
        progressBar.setVisibility(View.VISIBLE);

        executor.execute(() -> {
            try {
                databaseZapateria db = databaseZapateria.getInstance(getContext());
                ProductoDAO productoDAO = db.productoDAO();
                List<ProductoDAO.ProductoConStock> productos = productoDAO.getAllProductosConStock();

                requireActivity().runOnUiThread(() -> {
                    actualizarListaProductos(productos);
                    progressBar.setVisibility(View.GONE);
                    verificarListaVacia(productos);
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> mostrarErrorCarga(e));
            }
        });
    }

    private void actualizarListaProductos(List<ProductoDAO.ProductoConStock> productos) {
        listaProductos.clear();
        listaProductos.addAll(productos);
        adapter.notifyDataSetChanged();
    }

    private void verificarListaVacia(List<ProductoDAO.ProductoConStock> productos) {
        if (productos.isEmpty()) {
            Toast.makeText(getContext(), "No hay productos disponibles", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarErrorCarga(Exception e) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(getContext(), "Error al cargar productos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
}