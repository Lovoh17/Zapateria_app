package com.example.zapateria_app.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.zapateria_app.DAO.CategoriaDAO;
import com.example.zapateria_app.DAO.ProductoDAO;
import com.example.zapateria_app.Models.Producto;
import com.example.zapateria_app.R;
import com.example.zapateria_app.database.databaseZapateria;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RegistrarProductoDialog extends DialogFragment {

    private EditText etNombre, etMarca, etTalla, etPrecio;
    private Spinner spinnerCategoria;
    private ProductoRegistradoListener listener;
    private Executor executor = Executors.newSingleThreadExecutor();

    public interface ProductoRegistradoListener {
        void onProductoRegistrado();
    }

    public static RegistrarProductoDialog newInstance() {
        return new RegistrarProductoDialog();
    }

    public void setProductoRegistradoListener(ProductoRegistradoListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_registrar_producto, null);

        // Inicializar vistas
        etNombre = view.findViewById(R.id.etNombreProducto);
        etMarca = view.findViewById(R.id.etMarcaProducto);
        etTalla = view.findViewById(R.id.etTallaProducto);
        etPrecio = view.findViewById(R.id.etPrecioProducto);
        spinnerCategoria = view.findViewById(R.id.spinnerCategoria);
        Button btnGuardar = view.findViewById(R.id.btnGuardarProducto);
        Button btnCancelar = view.findViewById(R.id.btnCancelarRegistroProducto);

        // Cargar categorías
        cargarCategorias();

        // Configurar botones
        btnGuardar.setOnClickListener(v -> registrarProducto());
        btnCancelar.setOnClickListener(v -> dismiss());

        builder.setView(view)
                .setTitle("Registrar Nuevo Producto");

        return builder.create();
    }

    private void cargarCategorias() {
        executor.execute(() -> {
            try {
                databaseZapateria db = databaseZapateria.getInstance(getContext());
                CategoriaDAO categoriaDAO = db.categoriaDAO();
                List<String> categorias = categoriaDAO.getAllNombresCategorias();

                requireActivity().runOnUiThread(() -> {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            getContext(),
                            android.R.layout.simple_spinner_item,
                            categorias
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategoria.setAdapter(adapter);
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Error al cargar categorías", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void registrarProducto() {
        // Validar campos
        String nombre = etNombre.getText().toString().trim();
        String marca = etMarca.getText().toString().trim();
        String tallaStr = etTalla.getText().toString().trim();
        String precioStr = etPrecio.getText().toString().trim();
        String categoria = spinnerCategoria.getSelectedItem().toString();

        if (nombre.isEmpty() || marca.isEmpty() || tallaStr.isEmpty() || precioStr.isEmpty()) {
            Toast.makeText(getContext(), "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int talla = Integer.parseInt(tallaStr);
            double precio = Double.parseDouble(precioStr);

            if (talla <= 0 || precio <= 0) {
                Toast.makeText(getContext(), "Valores deben ser mayores a cero", Toast.LENGTH_SHORT).show();
                return;
            }

            // Obtener ID de categoría
            executor.execute(() -> {
                try {
                    databaseZapateria db = databaseZapateria.getInstance(getContext());
                    CategoriaDAO categoriaDAO = db.categoriaDAO();
                    int idCategoria = categoriaDAO.getIdCategoriaPorNombre(categoria);

                    Producto producto = new Producto(nombre, marca, talla, precio, idCategoria);
                    ProductoDAO productoDAO = db.productoDAO();
                    long id = productoDAO.insertProducto(producto);

                    requireActivity().runOnUiThread(() -> {
                        if (id > 0) {
                            Toast.makeText(getContext(), "Producto registrado", Toast.LENGTH_SHORT).show();
                            if (listener != null) {
                                listener.onProductoRegistrado();
                            }
                            dismiss();
                        } else {
                            Toast.makeText(getContext(), "Error al registrar", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            });

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Valores numéricos inválidos", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ProductoRegistradoListener) {
            listener = (ProductoRegistradoListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}