package com.example.zapateria_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zapateria_app.DAO.EmpleadoDAO;
import com.example.zapateria_app.Models.Empleado;
import com.example.zapateria_app.database.databaseZapateria;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private TextView tvForgotPassword;
    private EmpleadoDAO empleadoDAO;
    private Executor executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseZapateria db = databaseZapateria.getInstance(getApplicationContext());

        empleadoDAO = db.empleadoDAO();
        executor = Executors.newSingleThreadExecutor();
        inicializar();

    }

    public void inicializar()
    {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);


        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (validateInputs(username, password)) {
                attemptLogin(username, password);
            }
        });

    }

    private boolean validateInputs(String username, String password) {
        boolean isValid = true;

        if (username.isEmpty()) {
            etUsername.setError("Ingrese su usuario");
            isValid = false;
        }

        if (password.isEmpty()) {
            etPassword.setError("Ingrese su contraseña");
            isValid = false;
        }

        return isValid;
    }

    private void attemptLogin(String username, String password) {
        // Mostrar progreso y deshabilitar botón
        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        executor.execute(() -> {
            try {
                // Simular delay de red (opcional)
                Thread.sleep(1000);

                Empleado empleado = empleadoDAO.getEmpleadoByNombre(username);

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);

                    if (empleado != null && empleado.getPuesto().equals(password)) {
                        // Login exitoso
                        Toast.makeText(MainActivity.this,
                                "Bienvenido " + empleado.getNombre(),
                                Toast.LENGTH_SHORT).show();

                        // Navegar a la siguiente actividad
                        startActivity(new Intent(MainActivity.this, inicio.class));
                        finish();
                    } else {
                        // Credenciales incorrectas
                        Toast.makeText(MainActivity.this,
                                "Usuario o contraseña incorrectos",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);
                    Toast.makeText(MainActivity.this,
                            "Error al conectar con la base de datos",
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }


}