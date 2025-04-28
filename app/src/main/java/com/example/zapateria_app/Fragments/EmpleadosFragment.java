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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zapateria_app.Adapters.EmpleadoAdapter;
import com.example.zapateria_app.Dialogs.DialogoEmpleado;
import com.example.zapateria_app.Models.Empleado;
import com.example.zapateria_app.R;
import com.example.zapateria_app.database.databaseZapateria;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class EmpleadosFragment extends Fragment implements EmpleadoAdapter.OnEmpleadoClickListener, DialogoEmpleado.OnEmpleadoGuardadoListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private EmpleadoAdapter adapter;
    private List<Empleado> listaEmpleados = new ArrayList<>();
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_empleados, container, false);

        recyclerView = view.findViewById(R.id.rvEmpleados);
        progressBar = view.findViewById(R.id.progressBar);
        Button btnAgregarEmpleado = view.findViewById(R.id.addEmpleado);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EmpleadoAdapter(listaEmpleados, requireContext(), this);
        recyclerView.setAdapter(adapter);

        btnAgregarEmpleado.setOnClickListener(v -> {
            DialogoEmpleado dialogo = new DialogoEmpleado();
            dialogo.setOnEmpleadoGuardadoListener(this);
            dialogo.show(getParentFragmentManager(), "DialogoAgregarEmpleado");
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cargarEmpleados();
    }

    @Override
    public void onEditarClick(Empleado empleado) {
        DialogoEmpleado dialogo = DialogoEmpleado.newInstance(empleado);
        dialogo.setOnEmpleadoGuardadoListener(this);
        dialogo.show(getParentFragmentManager(), "DialogoEditarEmpleado");
    }

    @Override
    public void onBorrarClick(Empleado empleado) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar a " + empleado.getNombre() + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> borrarEmpleado(empleado))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void borrarEmpleado(Empleado empleado) {
        progressBar.setVisibility(View.VISIBLE);
        executor.execute(() -> {
            try {
                databaseZapateria db = databaseZapateria.getInstance(requireContext());
                int result = db.empleadoDAO().deleteEmpleado(empleado);

                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    if (result > 0) {
                        cargarEmpleados();
                        Toast.makeText(getContext(), "Empleado eliminado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Error al eliminar empleado", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    public void onEmpleadoGuardado(Empleado empleado) {
        progressBar.setVisibility(View.VISIBLE);
        executor.execute(() -> {
            try {
                databaseZapateria db = databaseZapateria.getInstance(requireContext());

                if (empleado.getId() == 0) {
                    long id = db.empleadoDAO().insertEmpleado(empleado);
                    if (id == -1) {
                        throw new Exception("Error al insertar empleado");
                    }
                } else {
                    int result = db.empleadoDAO().updateEmpleado(empleado);
                    if (result == 0) {
                        throw new Exception("Error al actualizar empleado");
                    }
                }

                requireActivity().runOnUiThread(() -> {
                    cargarEmpleados();
                    Toast.makeText(getContext(), "Empleado guardado", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void cargarEmpleados() {
        progressBar.setVisibility(View.VISIBLE);
        executor.execute(() -> {
            try {
                databaseZapateria db = databaseZapateria.getInstance(requireContext());
                List<Empleado> empleados = db.empleadoDAO().getAllEmpleados();

                requireActivity().runOnUiThread(() -> {
                    listaEmpleados.clear();
                    listaEmpleados.addAll(empleados);
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);

                    if (empleados.isEmpty()) {
                        Toast.makeText(getContext(), "No hay empleados registrados", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error al cargar empleados: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}