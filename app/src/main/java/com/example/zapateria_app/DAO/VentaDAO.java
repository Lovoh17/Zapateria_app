package com.example.zapateria_app.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.zapateria_app.Models.Venta;
import com.example.zapateria_app.POJO.VentaCompletaPOJO;

import java.util.List;

@Dao
public interface VentaDAO {
    @Insert
    long insertVenta(Venta venta);

    @Update
    int updateVenta(Venta venta);

    @Delete
    int deleteVenta(Venta venta);

    @Query("SELECT * FROM ventas")
    List<Venta> getAllVentas();

    @Query("SELECT * FROM ventas WHERE id = :id")
    Venta getVentaById(int id);

    @Query("SELECT * FROM ventas WHERE id_cliente = :clienteId")
    List<Venta> getVentasByCliente(int clienteId);

    @Query("SELECT * FROM ventas WHERE id_empleado = :empleadoId")
    List<Venta> getVentasByEmpleado(int empleadoId);

    @Query("SELECT COUNT(*) FROM ventas")
    int countVentas();

    @Transaction
    @Query("SELECT v.id, v.id_cliente, v.id_empleado, v.fecha, v.total, " +
            "c.nombre as nombreCliente, e.nombre as nombreEmpleado " +
            "FROM ventas v " +
            "INNER JOIN clientes c ON v.id_cliente = c.id " +
            "INNER JOIN empleados e ON v.id_empleado = e.id")
    List<VentaCompletaPOJO> getVentasCompletas();
}