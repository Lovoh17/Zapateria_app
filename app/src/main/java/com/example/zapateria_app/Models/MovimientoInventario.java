package com.example.zapateria_app.Models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "movimientos_inventario",
        foreignKeys = @ForeignKey(entity = Producto.class,
                parentColumns = "id",
                childColumns = "id_producto",
                onDelete = ForeignKey.RESTRICT),
        indices = @Index(value = "id_producto"))
public class MovimientoInventario implements java.io.Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "id_producto")
    private int idProducto;

    @ColumnInfo
    private String tipo; // ENTRADA o SALIDA

    @ColumnInfo
    private int cantidad;

    @ColumnInfo(name = "costo_unitario")
    private double costoUnitario;

    @ColumnInfo
    private String fecha;

    public MovimientoInventario() {
    }

    @Ignore
    public MovimientoInventario(int id, int idProducto, String tipo, int cantidad, double costoUnitario, String fecha) {
        this.id = id;
        this.idProducto = idProducto;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.costoUnitario = costoUnitario;
        this.fecha = fecha;
    }

    @Ignore
    public MovimientoInventario(int idProducto, String tipo, int cantidad, double costoUnitario, String fecha) {
        this.idProducto = idProducto;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.costoUnitario = costoUnitario;
        this.fecha = fecha;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getCostoUnitario() {
        return costoUnitario;
    }

    public void setCostoUnitario(double costoUnitario) {
        this.costoUnitario = costoUnitario;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
