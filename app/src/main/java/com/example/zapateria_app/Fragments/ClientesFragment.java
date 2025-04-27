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

import com.example.zapateria_app.Adapters.ClienteAdapter;
import com.example.zapateria_app.DAO.ClienteDAO;
import com.example.zapateria_app.Dialog.DialogoCliente;
import com.example.zapateria_app.Models.Cliente;
import com.example.zapateria_app.R;
import com.example.zapateria_app.database.databaseZapateria;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ClientesFragment extends Fragment implements ClienteAdapter.OnClienteClickListener, DialogoCliente.OnClienteGuardadoListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ClienteAdapter adapter;
    private Button fabAddCliente;
    private List<ClienteDAO.ClienteConVentas> listaClientes = new ArrayList<>();
    private Executor executor = Executors.newSingleThreadExecutor();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clientes, container, false);

        recyclerView = view.findViewById(R.id.rvClientes);
        progressBar = view.findViewById(R.id.progressBar);
        //boton para agregar cliente
        fabAddCliente = view.findViewById(R.id.fabAddCliente);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fabAddCliente.setOnClickListener(v -> {
            DialogoCliente dialogo = new DialogoCliente();
            dialogo.show(getParentFragmentManager(), "DialogoCrearCliente");
        });

        return view;
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new ClienteAdapter(listaClientes, requireContext());
        recyclerView.setAdapter(adapter);
        cargarClientes();
    }

    @Override
    public void onEditarClick(ClienteDAO.ClienteConVentas clienteConVentas) {
        DialogoCliente dialogo = DialogoCliente.newInstance(clienteConVentas.cliente);
        dialogo.show(getParentFragmentManager(), "DialogoEditarCliente");
    }

    @Override
    public void onBorrarClick(ClienteDAO.ClienteConVentas clienteConVentas) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirmar borrado")
                .setMessage("¿Estás seguro de que quieres borrar a " + clienteConVentas.cliente.getNombre() + "?")
                .setPositiveButton("Borrar", (dialog, which) -> borrarCliente(clienteConVentas))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void borrarCliente(ClienteDAO.ClienteConVentas clienteConVentas) {
        progressBar.setVisibility(View.VISIBLE);

        executor.execute(() -> {
            try {
                databaseZapateria db = databaseZapateria.getInstance(getContext());

                // Primero verificar si tiene ventas asociadas
                int ventasAsociadas = db.clienteDAO().countVentasByCliente(clienteConVentas.cliente.getId());

                if (ventasAsociadas > 0) {
                    requireActivity().runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(),
                                "No se puede borrar el cliente porque tiene ventas asociadas",
                                Toast.LENGTH_LONG).show();
                    });
                    return;
                }

                // Si no tiene ventas, proceder con el borrado
                int result = db.clienteDAO().deleteCliente(clienteConVentas.cliente);

                requireActivity().runOnUiThread(() -> {
                    if (result > 0) {
                        cargarClientes();
                        Toast.makeText(getContext(), "Cliente borrado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Error al borrar cliente", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error al borrar cliente: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    public void onClienteGuardado(Cliente cliente) {
        progressBar.setVisibility(View.VISIBLE);
        executor.execute(() -> {
            try {
                databaseZapateria db = databaseZapateria.getInstance(getContext());

                if (cliente.getId() == 0) {
                    long id = db.clienteDAO().insertCliente(cliente);
                    if (id == -1) {
                        throw new Exception("Error al insertar cliente");
                    }
                } else {
                    int result = db.clienteDAO().updateCliente(cliente);
                    if (result == 0) {
                        throw new Exception("Error al actualizar cliente");
                    }
                }

                requireActivity().runOnUiThread(() -> {
                    cargarClientes();
                    Toast.makeText(getContext(), "Cliente guardado", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void cargarClientes() {
        progressBar.setVisibility(View.VISIBLE);

        executor.execute(() -> {
            try {
                databaseZapateria db = databaseZapateria.getInstance(getContext());
                List<ClienteDAO.ClienteConVentas> clientes = db.clienteDAO().getClientesConVentas();

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