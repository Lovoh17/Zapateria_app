package com.example.zapateria_app.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.zapateria_app.Models.DetalleVenta;

import java.util.List;

@Dao
public interface DetalleVentaDAO {
    @Insert
    long insertDetalleVenta(DetalleVenta detalleVenta);

    @Update
    int updateDetalleVenta(DetalleVenta detalleVenta);

    @Delete
    int deleteDetalleVenta(DetalleVenta detalleVenta);

    @Query("SELECT * FROM detalle_ventas WHERE id_venta = :ventaId")
    List<DetalleVenta> getDetallesByVenta(int ventaId);

    @Query("SELECT SUM(subtotal) FROM detalle_ventas WHERE id_venta = :ventaId")
    double getTotalVenta(int ventaId);

    @Query("SELECT COUNT(*) FROM detalle_ventas WHERE id_venta = :ventaId")
    int countProductosEnVenta(int ventaId);
}