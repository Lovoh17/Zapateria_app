package com.example.zapateria_app.DAO;

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
}