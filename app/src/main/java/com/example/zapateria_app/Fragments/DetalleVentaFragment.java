package com.example.zapateria_app.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zapateria_app.Adapters.DetalleVentaAdapter;
import com.example.zapateria_app.DAO.DetalleVentaDAO;
import com.example.zapateria_app.POJO.DetalleVentaCompleto;
import com.example.zapateria_app.R;
import com.example.zapateria_app.database.databaseZapateria;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DetalleVentaFragment extends Fragment {
    private static final String TAG = "DetalleVentaFragment";
    private RecyclerView recyclerView;
    private DetalleVentaAdapter adapter;
    private ProgressBar progressBar;
    private Executor executor = Executors.newSingleThreadExecutor();

    public DetalleVentaFragment() {
    }

    public static DetalleVentaFragment newInstance() {
        return new DetalleVentaFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detalle_venta, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.rvDetalleVenta);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DetalleVentaAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        cargarTodosLosDetallesCompletos();

        return view;
    }

    private void cargarTodosLosDetallesCompletos() {
        progressBar.setVisibility(View.VISIBLE);

        executor.execute(() -> {
            try {
                databaseZapateria db = databaseZapateria.getInstance(getContext());
                DetalleVentaDAO detalleVentaDAO = db.detalleVentaDAO();

                List<DetalleVentaCompleto> detalles = detalleVentaDAO.getAllDetallesCompletos();

                Log.d(TAG, "Total de detalles completos cargados: " + detalles.size());

                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    if (detalles.isEmpty()) {
                        Toast.makeText(getContext(), "No hay detalles de ventas registrados", Toast.LENGTH_SHORT).show();
                    } else {
                        adapter.updateData(detalles);
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Error al cargar detalles completos", e);
                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error al cargar detalles: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}