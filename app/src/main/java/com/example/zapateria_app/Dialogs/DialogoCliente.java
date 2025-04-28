package com.example.zapateria_app.Dialogs;

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
        if (cliente != null) {
            Bundle args = new Bundle();
            args.putSerializable("cliente", cliente);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnClienteGuardadoListener) {
            listener = (OnClienteGuardadoListener) context;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_cliente, null);

        initViews(view);
        loadClienteData();

        builder.setView(view)
                .setTitle(cliente == null ? "Nuevo Cliente" : "Editar Cliente")
                .setPositiveButton("Guardar", (dialog, id) -> guardarCliente())
                .setNegativeButton("Cancelar", (dialog, id) -> dismiss());

        AlertDialog dialog = builder.create();
        setupDialogWindow(dialog);
        return dialog;
    }

    private void initViews(View view) {
        etNombre = view.findViewById(R.id.etNombre);
        etTelefono = view.findViewById(R.id.etTelefono);
        etCorreo = view.findViewById(R.id.etCorreo);
    }

    private void loadClienteData() {
        if (getArguments() != null) {
            cliente = (Cliente) getArguments().getSerializable("cliente");
        }

        if (cliente != null) {
            etNombre.setText(cliente.getNombre());
            etTelefono.setText(cliente.getTelefono());
            etCorreo.setText(cliente.getCorreo());
        }
    }

    private void setupDialogWindow(AlertDialog dialog) {
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
    }

    private void guardarCliente() {
        if (!validateInputs()) {
            return;
        }

        Cliente nuevoCliente = prepareClienteData();

        if (listener != null) {
            listener.onClienteGuardado(nuevoCliente);
        }
        dismiss();
    }

    private boolean validateInputs() {
        if (etNombre.getText().toString().trim().isEmpty()) {
            etNombre.setError("Nombre requerido");
            return false;
        }
        return true;
    }

    private Cliente prepareClienteData() {
        String nombre = etNombre.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();

        if (cliente != null) {
            cliente.setNombre(nombre);
            cliente.setTelefono(telefono);
            cliente.setCorreo(correo);
            return cliente;
        } else {
            return new Cliente(nombre, telefono, correo);
        }
    }

    public void setOnClienteGuardadoListener(OnClienteGuardadoListener listener) {
        this.listener = listener;
    }
}