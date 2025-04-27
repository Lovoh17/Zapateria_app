package com.example.zapateria_app.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.zapateria_app.Models.Cliente;
import com.example.zapateria_app.R;

public class DialogoCliente extends DialogFragment {

    public interface OnClienteGuardadoListener {
        void onClienteGuardado(Cliente cliente);
    }

    private OnClienteGuardadoListener listener;
    private Cliente cliente;
    private EditText etNombre, etTelefono, etCorreo;

    public static DialogoCliente newInstance(Cliente cliente) {
        DialogoCliente fragment = new DialogoCliente();
        Bundle args = new Bundle();
        args.putSerializable("cliente", cliente);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cliente = (Cliente) getArguments().getSerializable("cliente");
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (OnClienteGuardadoListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " debe implementar OnClienteGuardadoListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_cliente, null);

        etNombre = view.findViewById(R.id.etNombre);
        etTelefono = view.findViewById(R.id.etTelefono);
        etCorreo = view.findViewById(R.id.etCorreo);

        if (cliente != null) {
            etNombre.setText(cliente.getNombre());
            etTelefono.setText(cliente.getTelefono());
            etCorreo.setText(cliente.getCorreo());
        }

        builder.setView(view)
                .setTitle(cliente == null ? "Nuevo Cliente" : "Editar Cliente")
                .setPositiveButton("Guardar", (dialog, id) -> guardarCliente())
                .setNegativeButton("Cancelar", (dialog, id) -> dismiss());

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(window.getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                window.setAttributes(layoutParams);
            }
        });

        return dialog;
    }

    private void guardarCliente() {
        String nombre = etNombre.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();

        if (nombre.isEmpty()) {
            etNombre.setError("Nombre requerido");
            return;
        }

        Cliente nuevoCliente;
        if (cliente != null) {
            // Editar cliente existente
            nuevoCliente = cliente;
            nuevoCliente.setNombre(nombre);
            nuevoCliente.setTelefono(telefono);
            nuevoCliente.setCorreo(correo);
        } else {
            // Crear nuevo cliente
            nuevoCliente = new Cliente();
            nuevoCliente.setNombre(nombre);
            nuevoCliente.setTelefono(telefono);
            nuevoCliente.setCorreo(correo);
        }

        if (listener != null) {
            listener.onClienteGuardado(nuevoCliente);
        }
        dismiss();
    }
}