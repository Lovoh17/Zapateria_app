package com.example.zapateria_app;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.zapateria_app.Fragments.CompraInventarioFragment;
import com.example.zapateria_app.Fragments.DetalleVentaFragment;
import com.example.zapateria_app.Fragments.ProductoFragment;
import com.example.zapateria_app.Fragments.UsuariosFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class inicio extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inicio);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configuración EdgeToEdge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

//navegación
        bottomNavigationView.setOnItemSelectedListener(item -> {

      Fragment selectedFragment = null;

            int itemId = item.getItemId();
            if (itemId == R.id.productos) {
                selectedFragment = new ProductoFragment();
            } else if (itemId == R.id.inventario) {
                selectedFragment = new CompraInventarioFragment();
            } else if (itemId == R.id.ventas) {
                selectedFragment = new DetalleVentaFragment();
            }else if (itemId == R.id.movimientoInventario) {
                selectedFragment = new UsuariosFragment();
            }else if (itemId == R.id.usuarios) {
                selectedFragment = new UsuariosFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView, selectedFragment)
                        .commit();
                return true;
            }

            return false;
        });

// Establecer fragmento inicial
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.productos);
        }

    }
}