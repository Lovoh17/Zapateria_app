package com.example.zapateria_app.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.zapateria_app.Models.InventarioActual;

@Dao
public interface InventarioActualDAO {
    @Insert
    void insertInventario(InventarioActual inventario);

    @Update
    int updateInventario(InventarioActual inventario);

    @Query("SELECT * FROM inventario_actual WHERE id_producto = :productoId")
    InventarioActual getInventarioByProducto(int productoId);

    @Query("UPDATE inventario_actual SET stock = stock + :cantidad WHERE id_producto = :productoId")
    int actualizarStock(int productoId, int cantidad);

    @Query("UPDATE inventario_actual SET costo_promedio = :costoPromedio WHERE id_producto = :productoId")
    int actualizarCostoPromedio(int productoId, double costoPromedio);

    @Query("SELECT SUM(stock * costo_promedio) FROM inventario_actual")
    double getValorTotalInventario();

    @Query("SELECT * FROM inventario_actual WHERE id_producto = :productoId")
    InventarioActual getInventarioByProductoId(int productoId);

    @Query("UPDATE inventario_actual SET stock = stock - :cantidad WHERE id_producto = :productoId")
    int disminuirStock(int productoId, int cantidad);
}