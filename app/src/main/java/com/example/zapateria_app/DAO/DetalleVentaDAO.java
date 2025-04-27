package com.example.zapateria_app.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.zapateria_app.Models.DetalleVenta;
import com.example.zapateria_app.POJO.DetalleVentaCompleto;

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

    @Query("SELECT dv.id as idDetalle, dv.cantidad, dv.precio_unitario as precioUnitario, " +
            "dv.subtotal, p.nombre as nombreProducto, p.marca as marcaProducto, " +
            "v.id as idVenta, v.fecha as fechaVenta, v.total as totalVenta, " +
            "c.nombre as nombreCliente, c.telefono as telefonoCliente, " +
            "e.nombre as nombreEmpleado, e.puesto as puestoEmpleado " +
            "FROM detalle_ventas dv " +
            "INNER JOIN productos p ON dv.id_producto = p.id " +
            "INNER JOIN ventas v ON dv.id_venta = v.id " +
            "INNER JOIN clientes c ON v.id_cliente = c.id " +
            "INNER JOIN empleados e ON v.id_empleado = e.id")
    List<DetalleVentaCompleto> getAllDetallesCompletos();


}