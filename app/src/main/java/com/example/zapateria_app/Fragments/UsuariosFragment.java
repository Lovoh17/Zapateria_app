package com.example.zapateria_app.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.zapateria_app.R;

public class UsuariosFragment extends Fragment {

    public UsuariosFragment() {
        // Required empty public constructor
    }

    public static UsuariosFragment newInstance() {
        return new UsuariosFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_usuarios, container, false);
        setupButtonListeners(view);
        loadInitialFragment();
        return view;
    }

    private void setupButtonListeners(View view) {
        Button btnEmpleados = view.findViewById(R.id.VerEmpleados);
        Button btnClientes = view.findViewById(R.id.VerClientes);

        btnEmpleados.setOnClickListener(v -> loadFragment(new EmpleadosFragment()));
        btnClientes.setOnClickListener(v -> loadFragment(new ClientesFragment()));
    }

    private void loadInitialFragment() {
        // Solo cargar si no hay fragmento actual
        if (getChildFragmentManager().findFragmentById(R.id.UserfragmentContainer) == null) {
            loadFragment(new EmpleadosFragment());
        }
    }

    private void loadFragment(Fragment fragment) {
        getChildFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.UserfragmentContainer, fragment)
                .commit();
    }
}