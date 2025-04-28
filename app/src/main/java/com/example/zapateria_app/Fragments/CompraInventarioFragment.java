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
import com.example.zapateria_app.Models.InventarioActual;
import com.example.zapateria_app.Models.MovimientoInventario;
import com.example.zapateria_app.R;
import com.example.zapateria_app.database.databaseZapateria;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
        adapter.setOnCompraChangeListener(new ProductoCompraAdapter.OnCompraChangeListener() {
            @Override
            public void onCompraChanged(int totalItems, double totalValue) {
                CompraInventarioFragment.this.totalValue = totalValue;
                CompraInventarioFragment.this.totalItems = totalItems;
                btnComprar.setEnabled(totalItems > 0);
            }

            @Override
            public void onNuevoCostoChanged(int position, double nuevoCosto) {
                // Actualizar el costo en la lista de productos
                if (position >= 0 && position < listaProductos.size()) {
                    listaProductos.get(position).setCostoPromedio(nuevoCosto);
                }
            }
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
        Map<Integer, Double> nuevosCostos = adapter.getNuevosCostos();
        obtenerProductosSeleccionados(productosSeleccionados, cantidades);

        new AlertDialog.Builder(getContext())
                .setTitle("Confirmar Compra de Inventario")
                .setMessage("¿Desea registrar la entrada de estos productos al inventario?")
                .setPositiveButton("Confirmar", (dialog, which) -> procesarCompra(productosSeleccionados, cantidades, nuevosCostos))
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

    private void procesarCompra(List<ProductoDAO.ProductoConStock> productos, List<Integer> cantidades, Map<Integer, Double> nuevosCostos) {
        progressBar.setVisibility(View.VISIBLE);

        // Obtener fecha actual
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        String fechaActual = dateFormat.format(new Date());

        executor.execute(() -> {
            databaseZapateria db = databaseZapateria.getInstance(getContext());

            db.runInTransaction(() -> {
                try {
                    for (int i = 0; i < productos.size(); i++) {
                        ProductoDAO.ProductoConStock producto = productos.get(i);
                        int cantidad = cantidades.get(i);
                        double nuevoCosto = nuevosCostos.getOrDefault(producto.getId(), producto.getCostoPromedio());

                        if (cantidad <= 0) continue;

                        // 1. Actualizar stock en inventario
                        db.productoDAO().aumentarStock(producto.getId(), cantidad);

                        // 2. Registrar movimiento de inventario
                        MovimientoInventario movimiento = new MovimientoInventario(
                                producto.getId(),
                                "ENTRADA", // Tipo ENTRADA para compras
                                cantidad,
                                nuevoCosto, // Usar el nuevo costo ingresado
                                fechaActual
                        );
                        db.movimientoInventarioDAO().insertMovimiento(movimiento);

                        // 3. Actualizar costo promedio
                        actualizarCostoPromedio(producto.getId(), cantidad, nuevoCosto);
                    }

                    requireActivity().runOnUiThread(() -> {
                        mostrarResultadoExitoso();
                        reiniciarSeleccion();
                    });
                } catch (Exception e) {
                    requireActivity().runOnUiThread(() -> mostrarError(e));
                    throw e;
                }
            });
        });
    }

    private void actualizarCostoPromedio(int productoId, int cantidad, double nuevoCosto) {
        executor.execute(() -> {
            databaseZapateria db = databaseZapateria.getInstance(getContext());
            InventarioActual inventario = db.inventarioActualDAO().getInventarioByProductoId(productoId);

            if (inventario != null) {
                double stockActual = inventario.getStock();
                double costoActual = inventario.getCostoPromedio();

                // 1. Calcular nuevo costo promedio ponderado
                double nuevoCostoPromedio =
                        ((stockActual * costoActual) + (cantidad * nuevoCosto)) /
                                (stockActual + cantidad);

                // 2. Calcular nuevo precio con 20% de ganancia
                double nuevoPrecio = nuevoCostoPromedio * 1.20; // Añadir 20% al costo

                // 3. Actualizar ambos valores en la base de datos
                db.runInTransaction(() -> {
                    // Actualizar costo promedio
                    db.inventarioActualDAO().actualizarCostoPromedio(productoId, nuevoCostoPromedio);

                    // Actualizar precio del producto con el margen de ganancia
                    db.productoDAO().actualizarPrecioProducto(productoId, nuevoPrecio);
                });

                // Opcional: Notificar a la UI si es necesario
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(),
                            "Precio actualizado con 20% de ganancia",
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
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