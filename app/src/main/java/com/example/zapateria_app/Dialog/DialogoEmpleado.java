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
        void onEmpleadoGuardado(Empleado empleado);
    }
    public interface OnEmpleadoEditarListener {
        void OnEmpleadoEditarListener(Empleado empleado);
    }

    private OnEmpleadoGuardadoListener listener;
    private Empleado empleado;
    private EditText etNombre, etPuesto;

    public void setOnEmpleadoGuardadoListener(OnEmpleadoGuardadoListener listener) {
        this.listener = listener;
    }
    public void OnEmpleadoEditarListener(OnEmpleadoGuardadoListener listener) {
        this.listener = listener;
    }

    public static DialogoEmpleado newInstance(Empleado empleado) {
        DialogoEmpleado fragment = new DialogoEmpleado();
        Bundle args = new Bundle();
        args.putSerializable("empleado", empleado);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_empleado, null);

        etNombre = view.findViewById(R.id.etNombre);
        etPuesto = view.findViewById(R.id.etPuesto);

        if (empleado != null) {
            etNombre.setText(empleado.getNombre());
            etPuesto.setText(empleado.getPuesto());
        }

        builder.setView(view)
                .setTitle(empleado == null ? "Nuevo Empleado" : "Editar Empleado") // Corregido a "Empleado"
                .setPositiveButton("Guardar", (dialog, id) -> guardarEmpleado())
                .setNegativeButton("Cancelar", (dialog, id) -> dismiss());

        return builder.create();
    }

    private void guardarEmpleado() {
        String nombre = etNombre.getText().toString().trim();
        String puesto = etPuesto.getText().toString().trim();

        if (nombre.isEmpty()) {
            etNombre.setError("Nombre requerido");
            return;
        }

        Empleado nuevoEmpleado;
        if (empleado != null) {
            nuevoEmpleado = empleado;
            nuevoEmpleado.setNombre(nombre);
            nuevoEmpleado.setPuesto(puesto);
        } else {
            nuevoEmpleado = new Empleado(nombre, puesto);
        }

        if (listener != null) {
            listener.onEmpleadoGuardado(nuevoEmpleado);
        }
        dismiss();
    }
}