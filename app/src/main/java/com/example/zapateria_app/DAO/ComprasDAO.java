package com.example.zapateria_app.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.zapateria_app.models.Compra;

import java.util.List;

@Dao
public interface ComprasDAO {
    @Insert
    long insert(Compra compra);

    @Insert
    void insertAll(List<Compra> compras);

    // Actualizar una compra
    @Update
    void update(Compra compra);

    // Eliminar una compra
    @Delete
    void delete(Compra compra);

    // Obtener todas las compras ordenadas por fecha (más reciente primero)
    @Query("SELECT * FROM compras ORDER BY fechaCompra DESC")
    List<Compra> getAllCompras();

    // Obtener una compra por ID
    @Query("SELECT * FROM compras WHERE id = :id")
    Compra getCompraById(int id);

    // Obtener compras de un cliente específico
    @Query("SELECT * FROM compras WHERE clienteId = :clienteId ORDER BY fechaCompra DESC")
    List<Compra> getComprasByCliente(int clienteId);

    // Obtener compras realizadas por un empleado
    @Query("SELECT * FROM compras WHERE empleadoId = :empleadoId ORDER BY fechaCompra DESC")
    List<Compra> getComprasByEmpleado(int empleadoId);

    // Obtener compras por rango de fechas
    @Query("SELECT * FROM compras WHERE fechaCompra BETWEEN :desde AND :hasta ORDER BY fechaCompra DESC")
    List<Compra> getComprasByFecha(long desde, long hasta);

    // Obtener compras por estado
    @Query("SELECT * FROM compras WHERE estado = :estado ORDER BY fechaCompra DESC")
    List<Compra> getComprasByEstado(String estado);

    // Obtener compras que contengan un producto específico
    @Query("SELECT * FROM compras WHERE productos LIKE '%' || :productoId || '%' ORDER BY fechaCompra DESC")
    List<Compra> getComprasByProducto(int productoId);

    // Obtener el total de ventas (suma de todos los totales)
    @Query("SELECT SUM(total) FROM compras WHERE estado = 'Completada'")
    double getTotalVentas();

    // Obtener compras paginadas (para listas grandes)
    @Query("SELECT * FROM compras ORDER BY fechaCompra DESC LIMIT :limit OFFSET :offset")
    List<Compra> getComprasPaginadas(int limit, int offset);

    // Obtener el número total de compras
    @Query("SELECT COUNT(*) FROM compras")
    int getTotalCompras();
}