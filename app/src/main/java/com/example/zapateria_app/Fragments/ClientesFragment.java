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
    private Button btnAgregarCliente;
    private List<Cliente> listaClientes = new ArrayList<>();
    private final Executor executor = Executors.newSingleThreadExecutor();

    public ClientesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clientes, container, false);

        recyclerView = view.findViewById(R.id.rvClientes);
        progressBar = view.findViewById(R.id.progressBar);
        btnAgregarCliente = view.findViewById(R.id.addCliente);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ClienteAdapter(listaClientes, requireContext(), this);
        recyclerView.setAdapter(adapter);

        btnAgregarCliente.setOnClickListener(v -> {
            DialogoCliente dialogo = new DialogoCliente();
            dialogo.setOnClienteGuardadoListener(this);
            dialogo.show(getParentFragmentManager(), "DialogoAgregarCliente");
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cargarClientes();
    }

    @Override
    public void onEditarClick(Cliente cliente) {
        DialogoCliente dialogo = DialogoCliente.newInstance(cliente);
        dialogo.setOnClienteGuardadoListener(this);
        dialogo.show(getParentFragmentManager(), "DialogoEditarCliente");
    }

    @Override
    public void onBorrarClick(Cliente cliente) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirmar borrado")
                .setMessage("¿Estás seguro de que quieres borrar a " + cliente.getNombre() + "?")
                .setPositiveButton("Borrar", (dialog, which) -> borrarCliente(cliente))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onEditarClick(ClienteDAO.ClienteConVentas cliente) {

    }

    private void borrarCliente(Cliente cliente) {
        progressBar.setVisibility(View.VISIBLE);
        executor.execute(() -> {
            try {
                databaseZapateria db = databaseZapateria.getInstance(requireContext());
                int result = db.clienteDAO().deleteCliente(cliente);

                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    if (result > 0) {
                        cargarClientes();
                        Toast.makeText(getContext(), "Cliente eliminado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Error al eliminar cliente", Toast.LENGTH_SHORT).show();
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
    public void onBorrarClick(ClienteDAO.ClienteConVentas cliente) {

    }

    @Override
    public void onClienteGuardado(Cliente cliente) {
        progressBar.setVisibility(View.VISIBLE);
        executor.execute(() -> {
            try {
                databaseZapateria db = databaseZapateria.getInstance(requireContext());

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
                databaseZapateria db = databaseZapateria.getInstance(requireContext());
                List<Cliente> clientes = db.clienteDAO().getAllClientes();

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