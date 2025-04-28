package com.example.zapateria_app.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zapateria_app.Adapters.CarritoAdapter;
import com.example.zapateria_app.DAO.ClienteDAO;
import com.example.zapateria_app.DAO.DetalleVentaDAO;
import com.example.zapateria_app.DAO.ProductoDAO;
import com.example.zapateria_app.DAO.VentaDAO;
import com.example.zapateria_app.Models.Cliente;
import com.example.zapateria_app.Models.DetalleVenta;
import com.example.zapateria_app.Models.InventarioActual;
import com.example.zapateria_app.Models.Venta;
import com.example.zapateria_app.database.databaseZapateria;
import com.example.zapateria_app.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ConfirmarCompraDialog extends DialogFragment {

    private List<ProductoDAO.ProductoConStock> productos;
    private List<Integer> cantidadesList;
    private Map<Integer, Integer> cantidadesMap;
    private double total;
    private CompraConfirmadaListener listener;
    private Executor executor = Executors.newSingleThreadExecutor();
    private Map<Integer, String> clientesMap = new LinkedHashMap<>();


    public interface CompraConfirmadaListener {
        void onCompraConfirmada(long idVenta);
    }

    public ConfirmarCompraDialog() {
    }

    // Método newInstance para crear instancias del diálogo
    public static ConfirmarCompraDialog newInstance(
            List<ProductoDAO.ProductoConStock> productos,
            List<Integer> cantidades,
            double total) {

        ConfirmarCompraDialog dialog = new ConfirmarCompraDialog();
        dialog.productos = productos;
        dialog.cantidadesList = cantidades;
        dialog.total = total;

        dialog.cantidadesMap = new HashMap<>();
        for (int i = 0; i < productos.size(); i++) {
            dialog.cantidadesMap.put(i, cantidades.get(i));
        }

        return dialog;
    }

    public void setCompraConfirmadaListener(CompraConfirmadaListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_confirmar_compra, null);

        // Inicializar vistas
        Spinner spinnerClientes = view.findViewById(R.id.spinnerClientes);
        RecyclerView rvCarrito = view.findViewById(R.id.rvCarrito);
        Button btnCancelar = view.findViewById(R.id.btnCancelar);
        Button btnConfirmar = view.findViewById(R.id.btnConfirmar);

        // Configurar RecyclerView del carrito
        List<ProductoDAO.ProductoConStock> productosSeleccionados = new ArrayList<>();
        for (int i = 0; i < productos.size(); i++) {
            productosSeleccionados.add(productos.get(i));
        }

        CarritoAdapter carritoAdapter = new CarritoAdapter(productosSeleccionados, cantidadesMap, getContext());
        rvCarrito.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCarrito.setAdapter(carritoAdapter);

        // Cargar clientes en el spinner
        cargarClientes(spinnerClientes);

        btnCancelar.setOnClickListener(v -> dismiss());
        btnConfirmar.setOnClickListener(v -> confirmarCompra(spinnerClientes));

        builder.setView(view);
        return builder.create();
    }

    private void cargarClientes(Spinner spinner) {
        executor.execute(() -> {
            try {
                databaseZapateria db = databaseZapateria.getInstance(getContext());
                List<Cliente> clientesDAO = db.clienteDAO().getAllClientes();

                clientesMap.clear();
                for (Cliente cliente : clientesDAO) {
                    clientesMap.put(cliente.getId(), cliente.getNombre());
                }

                requireActivity().runOnUiThread(() -> {
                    if (clientesMap.isEmpty()) {
                        Toast.makeText(getContext(), "No hay clientes disponibles", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            getContext(),
                            android.R.layout.simple_spinner_item,
                            new ArrayList<>(clientesMap.values()));

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Error al cargar clientes", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void confirmarCompra(Spinner spinner) {
        Log.d("ConfirmarCompra", "Iniciando proceso de confirmación de compra");

        // Verificar si hay clientes cargados
        if (clientesMap.isEmpty()) {
            Log.e("ConfirmarCompra", "No hay clientes disponibles");
            Toast.makeText(getContext(), "No hay clientes disponibles", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener la posición seleccionada
        int selectedPosition = spinner.getSelectedItemPosition();
        if (selectedPosition == AdapterView.INVALID_POSITION) {
            Log.e("ConfirmarCompra", "No se ha seleccionado ningún cliente");
            Toast.makeText(getContext(), "Seleccione un cliente", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener lista de IDs de clientes
        List<Integer> clienteIds = new ArrayList<>(clientesMap.keySet());

        // Verificar que la posición es válida
        if (selectedPosition < 0 || selectedPosition >= clienteIds.size()) {
            Log.e("ConfirmarCompra", "Posición seleccionada inválida: " + selectedPosition);
            Toast.makeText(getContext(), "Selección de cliente inválida", Toast.LENGTH_SHORT).show();
            return;
        }

        int clienteId = clienteIds.get(selectedPosition);
        Log.d("ConfirmarCompra", "Cliente seleccionado - ID: " + clienteId);

        executor.execute(() -> {
            databaseZapateria db = databaseZapateria.getInstance(getContext());

            db.runInTransaction(() -> {
                try {
                    // Obtener cliente
                    Cliente clienteSeleccionado = db.clienteDAO().getClienteById(clienteId);
                    Venta nuevaVenta = getVenta(clienteSeleccionado);

                    long idVenta = db.ventaDAO().insertVenta(nuevaVenta);
                    Log.d("ConfirmarCompra", "Venta creada con ID: " + idVenta);

                    for (int i = 0; i < productos.size(); i++) {
                        try {

                            if (i >= cantidadesList.size()) {
                                Log.e("ConfirmarCompra", "Índice fuera de rango en cantidadesList");
                                continue;
                            }

                            Integer cantidad = cantidadesList.get(i);
                            if (cantidad == null || cantidad <= 0) {
                                Log.d("ConfirmarCompra", "Cantidad no válida, omitiendo producto " + i);
                                continue;
                            }

                            ProductoDAO.ProductoConStock producto = productos.get(i);

                            // 1. Obtener el inventario actual del producto
                            InventarioActual inventario = db.inventarioActualDAO().getInventarioByProductoId(producto.getId());
                            if (inventario == null) {
                                throw new Exception("No existe registro de inventario para el producto ID: " + producto.getId());
                            }

                            // 2. Verificar stock disponible
                            if (inventario.getStock() < cantidad) {
                                throw new Exception("Stock insuficiente para producto ID: " + producto.getId() +
                                        ". Stock actual: " + inventario.getStock() + ", solicitado: " + cantidad);
                            }

                            // 3. Actualizar inventario (dos métodos alternativos)

                            int filasActualizadas = db.inventarioActualDAO().disminuirStock(
                                    producto.getId(),
                                    cantidad
                            );

                            if (filasActualizadas != 1) {
                                throw new Exception("No se pudo actualizar el inventario para el producto ID: " + producto.getId());
                            }

                            InventarioActual inventarioVerificado = db.inventarioActualDAO().getInventarioByProductoId(producto.getId());
                            Log.d("ConfirmarCompra", "Stock verificado - Anterior: " + inventario.getStock() +
                                    ", Nuevo: " + inventarioVerificado.getStock());

                            // 5. Insertar detalle de venta
                            DetalleVenta detalle = new DetalleVenta(
                                    (int) idVenta,
                                    producto.getId(),
                                    cantidad,
                                    producto.getPrecio(),
                                    producto.getPrecio() * cantidad
                            );

                            db.detalleVentaDAO().insertDetalleVenta(detalle);

                            Log.d("ConfirmarCompra", "Producto procesado - ID: " + producto.getId() +
                                    ", Cantidad: " + cantidad +
                                    ", Stock anterior: " + inventario.getStock());

                        } catch (Exception e) {
                            Log.e("ConfirmarCompra", "Error procesando producto " + i, e);
                            throw e; // Revierte la transacción
                        }
                    }

                    requireActivity().runOnUiThread(() -> {
                        if (listener != null) {
                            listener.onCompraConfirmada(idVenta);
                        }
                        dismiss();
                        Toast.makeText(getContext(), "Venta registrada exitosamente", Toast.LENGTH_SHORT).show();
                    });

                } catch (Exception e) {
                    Log.e("ConfirmarCompra", "Error en transacción de venta", e);
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Error al registrar venta: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });
    }

    @NonNull
    private Venta getVenta(Cliente clienteSeleccionado) throws Exception {
        if (clienteSeleccionado == null) {
            throw new Exception("Cliente no encontrado en la base de datos");
        }

        // Crear y insertar venta
        String fechaActual = "hoy";

        Venta nuevaVenta = new Venta(
                clienteSeleccionado.getId(),
                1, // estado: 1 = completada
                fechaActual,
                total
        );
        return nuevaVenta;
    }
}