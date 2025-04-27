package com.example.zapateria_app.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.zapateria_app.R;


public class UsuariosFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UsuariosFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static UsuariosFragment newInstance(String param1, String param2) {
        UsuariosFragment fragment = new UsuariosFragment();
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
        View view = inflater.inflate(R.layout.fragment_usuarios, container, false);
        setupButtonListeners(view);
        loadInitialFragment();
        return view;
    }
    private void setupButtonListeners(View view) {
        Button btnEmpleados = view.findViewById(R.id.VerEmpleados);
        Button btnClientes = view.findViewById(R.id.VerClientes);

        btnEmpleados.setOnClickListener(v -> replaceFragment(new EmpleadosFragment()));
        btnClientes.setOnClickListener(v -> replaceFragment(new ClientesFragment()));
    }
    private void loadInitialFragment() {
        if (getParentFragmentManager().findFragmentById(R.id.fragmentContainerView2) == null) {
            replaceFragment(new EmpleadosFragment());
        }
    }
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.setReorderingAllowed(true);
        transaction.replace(R.id.fragmentContainerView2, fragment);
        transaction.commit();
    }
}