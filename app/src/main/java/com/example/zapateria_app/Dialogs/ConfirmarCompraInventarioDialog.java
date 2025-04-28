package com.example.zapateria_app.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.zapateria_app.DAO.ProductoDAO;
import com.example.zapateria_app.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ConfirmarCompraInventarioDialog extends DialogFragment {

    private static final String ARG_PRODUCTOS = "productos";
    private static final String ARG_CANTIDADES = "cantidades";
    private static final String ARG_TOTAL = "total";

    private List<ProductoDAO.ProductoConStock> productos;
    private List<Integer> cantidades;
    private double total;
    private CompraConfirmadaListener listener;

    public interface CompraConfirmadaListener {
        void onCompraConfirmada(long idVenta);
    }

    public static ConfirmarCompraInventarioDialog newInstance(List<ProductoDAO.ProductoConStock> productos,
                                                              List<Integer> cantidades,
                                                              double total) {
        ConfirmarCompraInventarioDialog fragment = new ConfirmarCompraInventarioDialog();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PRODUCTOS, new ArrayList<>(productos));
        args.putSerializable(ARG_CANTIDADES, new ArrayList<>(cantidades));
        args.putDouble(ARG_TOTAL, total);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productos = (List<ProductoDAO.ProductoConStock>) getArguments().getSerializable(ARG_PRODUCTOS);
            cantidades = (List<Integer>) getArguments().getSerializable(ARG_CANTIDADES);
            total = getArguments().getDouble(ARG_TOTAL);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_confirmar_compra_inventario, null);

        // Configurar vistas
        TextView tvTotal = view.findViewById(R.id.tvTotalCompraInventario);
        TextView tvDetalles = view.findViewById(R.id.tvDetallesCompraInventario);
        Button btnConfirmar = view.findViewById(R.id.btnConfirmarCompraInventario);
        Button btnCancelar = view.findViewById(R.id.btnCancelarCompraInventario);

        // Validar datos recibidos
        if (productos == null || cantidades == null || productos.isEmpty() || productos.size() != cantidades.size()) {
            return builder.setTitle("Error")
                    .setMessage("Datos de compra inválidos")
                    .setPositiveButton("Aceptar", (dialog, which) -> dismiss())
                    .create();
        }

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());

        StringBuilder detalles = new StringBuilder();
        for (int i = 0; i < productos.size(); i++) {
            ProductoDAO.ProductoConStock producto = productos.get(i);
            int cantidad = cantidades.get(i);

            if (cantidad > 0) { // Solo mostrar productos con cantidad > 0
                double subtotal = cantidad * producto.getPrecio();
                detalles.append(String.format(Locale.getDefault(),
                        "• %s (Talla %d) - %d x %s = %s\n",
                        producto.getNombre(),
                        producto.getTalla(),
                        cantidad,
                        currencyFormat.format(producto.getPrecio()),
                        currencyFormat.format(subtotal)));
            }
        }
        tvDetalles.setText(detalles.toString());

        // Configurar botones
        btnConfirmar.setOnClickListener(v -> confirmarCompra());
        btnCancelar.setOnClickListener(v -> dismiss());

        builder.setView(view)
                .setCancelable(false); // Evitar que se cierre al tocar fuera

        return builder.create();
    }

    private void confirmarCompra() {
        if (listener != null) {
            try {
                AlertDialog dialog = (AlertDialog) getDialog();
                if (dialog != null) {
                    dialog.findViewById(R.id.btnConfirmarCompraInventario).setEnabled(false);
                    dialog.findViewById(R.id.btnCancelarCompraInventario).setEnabled(false);
                }

                new Thread(() -> {
                    try {
                        Thread.sleep(1500); // Simular tiempo de procesamiento
                        requireActivity().runOnUiThread(() -> {
                            long idVenta = System.currentTimeMillis(); // ID temporal
                            listener.onCompraConfirmada(idVenta);
                            dismiss();
                        });
                    } catch (InterruptedException e) {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Error al procesar compra", Toast.LENGTH_SHORT).show();
                            dismiss();
                        });
                    }
                }).start();
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                dismiss();
            }
        }
    }

    public void setCompraConfirmadaListener(CompraConfirmadaListener listener) {
        this.listener = listener;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof CompraConfirmadaListener) {
            listener = (CompraConfirmadaListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}