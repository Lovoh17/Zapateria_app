package com.example.zapateria_app.Models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "productos",
        foreignKeys = @ForeignKey(entity = Categoria.class,
                parentColumns = "id",
                childColumns = "id_categoria",
                onDelete = ForeignKey.RESTRICT),
        indices = @Index(value = "id_categoria"))

public class Producto implements java.io.Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo
    private String nombre;

    @ColumnInfo
    private String marca;

    @ColumnInfo
    private int talla;

    @ColumnInfo
    private double precio;

    @ColumnInfo(name = "id_categoria")
    private int idCategoria;

    public Producto() {
    }

    @Ignore
    public Producto(int id, String nombre, String marca, int talla, double precio, int idCategoria) {
        this.id = id;
        this.nombre = nombre;
        this.marca = marca;
        this.talla = talla;
        this.precio = precio;
        this.idCategoria = idCategoria;
    }

    @Ignore
    public Producto(String nombre, String marca, int talla, double precio, int idCategoria) {
        this.nombre = nombre;
        this.marca = marca;
        this.talla = talla;
        this.precio = precio;
        this.idCategoria = idCategoria;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public int getTalla() {
        return talla;
    }

    public void setTalla(int talla) {
        this.talla = talla;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }
}
