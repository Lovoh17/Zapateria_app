package com.example.zapateria_app.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zapateria_app.Adapters.MovimientoAdapter;
import com.example.zapateria_app.DAO.MovimientoInventarioDAO;
import com.example.zapateria_app.POJO.MovimientoConProducto;
import com.example.zapateria_app.R;
import com.example.zapateria_app.database.databaseZapateria;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MovimientoInventarioFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private MovimientoAdapter adapter;
    private List<MovimientoConProducto> movimientos = new ArrayList<>();
    private Executor executor = Executors.newSingleThreadExecutor();

    public MovimientoInventarioFragment() {

    }

    public static MovimientoInventarioFragment newInstance() {
        return new MovimientoInventarioFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movimieto_inventario, container, false);

        // Inicializar vistas
        recyclerView = view.findViewById(R.id.rvMovimientos);
        progressBar = view.findViewById(R.id.progressBar);

        // Configurar RecyclerView
        setupRecyclerView();

        // Cargar datos
        cargarMovimientos();

        return view;
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MovimientoAdapter(movimientos, getContext());
        recyclerView.setAdapter(adapter);
    }

    private void cargarMovimientos() {
        progressBar.setVisibility(View.VISIBLE);

        executor.execute(() -> {
            try {
                databaseZapateria db = databaseZapateria.getInstance(getContext());
                MovimientoInventarioDAO movimientoDAO = db.movimientoInventarioDAO();
                List<MovimientoConProducto> nuevosMovimientos = movimientoDAO.getAllMovimientosConProductos();

                requireActivity().runOnUiThread(() -> {
                    actualizarListaMovimientos(nuevosMovimientos);
                    progressBar.setVisibility(View.GONE);

                    if (nuevosMovimientos.isEmpty()) {
                        Toast.makeText(getContext(), "No hay movimientos registrados", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error al cargar movimientos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void actualizarListaMovimientos(List<MovimientoConProducto> nuevosMovimientos) {
        movimientos.clear();
        movimientos.addAll(nuevosMovimientos);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Recargar datos cuando el fragment se vuelve visible
        cargarMovimientos();
    }
}