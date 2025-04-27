package com.example.zapateria_app.DAO;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.zapateria_app.Models.Producto;

import java.util.List;

@Dao
public interface ProductoDAO {
    @Insert
    long insertProducto(Producto producto);

    @Update
    int updateProducto(Producto producto);

    @Delete
    int deleteProducto(Producto producto);

    @Query("SELECT * FROM productos")
    List<Producto> getAllProductos();

    @Query("SELECT * FROM productos WHERE id = :id")
    Producto getProductoById(int id);

    @Query("SELECT * FROM productos WHERE id_categoria = :categoriaId")
    List<Producto> getProductosByCategoria(int categoriaId);

    @Query("SELECT COUNT(*) FROM productos")
    int countProductos();

    @Query("SELECT COUNT(*) FROM detalle_ventas WHERE id_producto = :productoId")
    int countVentasByProducto(int productoId);

    // Consulta para aumentar el stock
    @Query("UPDATE inventario_actual SET stock = stock + :cantidad WHERE id_producto = :productoId")
    void aumentarStock(int productoId, int cantidad);

    @Query("SELECT p.*, i.stock as stock, i.costo_promedio as costoPromedio, c.nombre as nombreCategoria " +
            "FROM productos p " +
            "LEFT JOIN inventario_actual i ON p.id = i.id_producto " +
            "LEFT JOIN categorias c ON p.id_categoria = c.id")
    List<ProductoConStock> getAllProductosConStock();

    // Clase para el resultado de la consulta
    public static class ProductoConStock extends Producto {
        @ColumnInfo(name = "stock")
        private int stock;

        @ColumnInfo(name = "costoPromedio")
        private double costoPromedio;

        @ColumnInfo(name = "nombreCategoria")
        private String nombreCategoria;

        // Getters y Setters
        public int getStock() {
            return stock;
        }

        public void setStock(int stock) {
            this.stock = stock;
        }

        public double getCostoPromedio() {
            return costoPromedio;
        }

        public void setCostoPromedio(double costoPromedio) {
            this.costoPromedio = costoPromedio;
        }

        public String getNombreCategoria() {
            return nombreCategoria;
        }

        public void setNombreCategoria(String nombreCategoria) {
            this.nombreCategoria = nombreCategoria;
        }
    }
}