package com.example.zapateria_app.DAO;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.zapateria_app.Models.Categoria;

import java.util.List;

@Dao
public interface CategoriaDAO {
    @Insert
    long insertCategoria(Categoria categoria);

    @Update
    int updateCategoria(Categoria categoria);

    @Delete
    int deleteCategoria(Categoria categoria);

    @Query("SELECT * FROM categorias")
    List<Categoria> getAllCategorias();

    @Query("SELECT * FROM categorias WHERE id = :id")
    Categoria getCategoriaById(int id);

    @Query("SELECT COUNT(*) FROM categorias")
    int countCategorias();

    @Query("SELECT COUNT(*) FROM productos WHERE id_categoria = :categoriaId")
    int countProductosByCategoria(int categoriaId);
}