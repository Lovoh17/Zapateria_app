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

import com.example.zapateria_app.Models.Empleado;
import com.example.zapateria_app.R;

public class DialogoEmpleado extends DialogFragment {

    public interface OnEmpleadoGuardadoListener {
        void OnEmpleadoGuardadoListener(Empleado cliente);
    }

    private OnEmpleadoGuardadoListener listener;
    private Empleado empleado;
    private EditText etNombre, etPuesto;

    public static DialogoEmpleado newInstance(Empleado empleado) {
        DialogoEmpleado fragment = new DialogoEmpleado();
        Bundle args = new Bundle();
        args.putSerializable("empleado", empleado);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            empleado = (Empleado) getArguments().getSerializable("empleado");
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (OnEmpleadoGuardadoListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " debe implementar OnClienteGuardadoListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_empleado, null);

        etNombre = view.findViewById(R.id.etNombre);
        etPuesto = view.findViewById(R.id.etTelefono);

        if (empleado != null) {
            etNombre.setText(empleado.getNombre());
            etPuesto.setText(empleado.getPuesto());

        }

        builder.setView(view)
                .setTitle(empleado == null ? "Nuevo Cliente" : "Editar Cliente")
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
        String telefono = etPuesto.getText().toString().trim();
        if (nombre.isEmpty()) {
            etNombre.setError("Nombre requerido");
            return;
        }

        Empleado nuevoEmpleado;
        if (empleado != null) {
            // Editar cliente existente
            nuevoEmpleado = empleado;
            nuevoEmpleado.setNombre(nombre);
           // nuevoEmpleado.setPuesto(etPuesto);
        } else {
            // Crear nuevo cliente
            nuevoEmpleado = new Empleado();
            nuevoEmpleado.setNombre(nombre);
            //nuevoEmpleado.setPuesto(etPuesto);

        }

        if (listener != null) {
            listener.OnEmpleadoGuardadoListener(nuevoEmpleado);
        }
        dismiss();
    }
}