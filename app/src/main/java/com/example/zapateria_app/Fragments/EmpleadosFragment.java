package com.example.zapateria_app.Fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.zapateria_app.Adapters.EmpleadoAdapter;
import com.example.zapateria_app.DAO.EmpleadoDAO;
import com.example.zapateria_app.Models.Empleado;
import com.example.zapateria_app.R;
import com.example.zapateria_app.database.databaseZapateria;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class EmpleadosFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private EmpleadoAdapter adapter;
    private List<Empleado> listaEmpleados = new ArrayList<>();
    private Executor executor = Executors.newSingleThreadExecutor();

    private String mParam1;
    private String mParam2;

    public EmpleadosFragment() {
        // Required empty public constructor
    }

    public static EmpleadosFragment newInstance(String param1, String param2) {
        EmpleadosFragment fragment = new EmpleadosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_empleados, container, false);

        recyclerView = view.findViewById(R.id.rvEmpleados);
        progressBar = view.findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EmpleadoAdapter(listaEmpleados, getContext());
        recyclerView.setAdapter(adapter);

        cargarEmpleados();
        return view;
    }

    private void cargarEmpleados() {
        progressBar.setVisibility(View.VISIBLE);

        executor.execute(() -> {
            try {
                databaseZapateria db = databaseZapateria.getInstance(getContext());
                EmpleadoDAO empleadoDAO = db.empleadoDAO();

                List<Empleado> empleados = empleadoDAO.getAllEmpleados();

                requireActivity().runOnUiThread(() -> {
                    listaEmpleados.clear();
                    listaEmpleados.addAll(empleados);
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);

                    if (empleados.isEmpty()) {
                        Toast.makeText(getContext(), "No hay empleados registrados", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getContext(), "Empleados cargados correctamente", Toast.LENGTH_SHORT).show();
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