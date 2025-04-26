package com.example.zapateria_app.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.zapateria_app.Models.MovimientoInventario;

import java.util.List;

@Dao
public interface MovimientoInventarioDAO {
    @Insert
    long insertMovimiento(MovimientoInventario movimiento);

    @Update
    int updateMovimiento(MovimientoInventario movimiento);

    @Delete
    int deleteMovimiento(MovimientoInventario movimiento);

    @Query("SELECT * FROM movimientos_inventario WHERE id_producto = :productoId")
    List<MovimientoInventario> getMovimientosByProducto(int productoId);

    @Query("SELECT * FROM movimientos_inventario ORDER BY fecha DESC")
    List<MovimientoInventario> getAllMovimientos();

    @Query("SELECT SUM(CASE WHEN tipo = 'ENTRADA' THEN cantidad ELSE -cantidad END) " +
            "FROM movimientos_inventario WHERE id_producto = :productoId")
    int getStockActual(int productoId);

    @Query("SELECT AVG(costo_unitario) FROM movimientos_inventario " +
            "WHERE id_producto = :productoId AND tipo = 'ENTRADA'")
    double getCostoPromedio(int productoId);
}