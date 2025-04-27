package com.example.zapateria_app.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.zapateria_app.Models.Cliente;

import java.util.List;

@Dao
public interface ClienteDAO {
    @Insert
    long insertCliente(Cliente cliente);

    @Update
    int updateCliente(Cliente cliente);

    @Delete
    int deleteCliente(Cliente cliente);

    @Query("SELECT * FROM clientes")
    List<Cliente> getAllClientes();

    @Query("SELECT * FROM clientes WHERE id = :id")
    Cliente getClienteById(int id);

    @Query("SELECT COUNT(*) FROM clientes")
    int countClientes();

    @Query("SELECT COUNT(*) FROM ventas WHERE id_cliente = :clienteId")
    int countVentasByCliente(int clienteId);

    public static class ClienteConVentas extends Cliente {
        private int cantidadVentas;

        public int getCantidadVentas() {
            return cantidadVentas;
        }

        public void setCantidadVentas(int cantidadVentas) {
            this.cantidadVentas = cantidadVentas;
        }
    }

    @Query("SELECT clientes.*, COUNT(ventas.id) as cantidadVentas FROM clientes LEFT JOIN ventas ON clientes.id = ventas.id_cliente GROUP BY clientes.id")
    List<ClienteConVentas> getClientesConVentas();
}