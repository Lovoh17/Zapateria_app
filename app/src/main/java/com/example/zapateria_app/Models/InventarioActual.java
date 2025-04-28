package com.example.zapateria_app.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


@Entity(tableName = "inventario_actual",
        foreignKeys = @ForeignKey(entity = com.example.zapateria_app.Models.Producto.class,
                parentColumns = "id",
                childColumns = "id_producto",
                onDelete = ForeignKey.CASCADE))
public class InventarioActual implements java.io.Serializable {
    @PrimaryKey
    @ColumnInfo(name = "id_producto")
    private int idProducto;

    @ColumnInfo
    private int stock;

    @ColumnInfo(name = "costo_promedio")
    private double costoPromedio;

    public InventarioActual() {
    }

    @Ignore
    public InventarioActual(int idProducto, int stock, double costoPromedio) {
        this.idProducto = idProducto;
        this.stock = stock;
        this.costoPromedio = costoPromedio;
    }

    // Getters y Setters
    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

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
}