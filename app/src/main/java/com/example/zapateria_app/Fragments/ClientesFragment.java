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

import com.example.zapateria_app.Adapters.ClienteAdapter;
import com.example.zapateria_app.DAO.ClienteDAO;
import com.example.zapateria_app.R;
import com.example.zapateria_app.database.databaseZapateria;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ClientesFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ClienteAdapter adapter;
    private List<ClienteDAO.ClienteConVentas> listaClientes = new ArrayList<>();
    private Executor executor = Executors.newSingleThreadExecutor();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout
        View view = inflater.inflate(R.layout.fragment_clientes, container, false);

        // Inicializar vistas
        recyclerView = view.findViewById(R.id.rvClientes);
        progressBar = view.findViewById(R.id.progressBar);

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ClienteAdapter(listaClientes, getContext());
        recyclerView.setAdapter(adapter);

        // Cargar datos
        cargarClientes();

        return view;
    }

    private void cargarClientes() {
        progressBar.setVisibility(View.VISIBLE);

        executor.execute(() -> {
            try {
                // Obtener instancia de la base de datos
                databaseZapateria db = databaseZapateria.getInstance(getContext());
                ClienteDAO clienteDAO = db.clienteDAO();

                // Obtener clientes de la base de datos
                List<ClienteDAO.ClienteConVentas> clientes = clienteDAO.getClientesConVentas();

                // Actualizar UI en el hilo principal
                requireActivity().runOnUiThread(() -> {
                    listaClientes.clear();
                    listaClientes.addAll(clientes);
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);

                    if (clientes.isEmpty()) {
                        Toast.makeText(getContext(), "No hay clientes registrados", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error al cargar clientes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}