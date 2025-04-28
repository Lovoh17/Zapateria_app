package com.example.zapateria_app.Models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import com.example.zapateria_app.models.Venta;
import com.example.zapateria_app.Models.Producto;

@Entity(tableName = "detalle_ventas",
        foreignKeys = {
                @ForeignKey(entity = Venta.class,
                        parentColumns = "id",
                        childColumns = "id_venta",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Producto.class,
                        parentColumns = "id",
                        childColumns = "id_producto",
                        onDelete = ForeignKey.RESTRICT)
        },
        indices = {
                @Index(value = "id_venta"),
                @Index(value = "id_producto")
        })
public class DetalleVenta implements java.io.Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "id_venta")
    private int idVenta;

    @ColumnInfo(name = "id_producto")
    private int idProducto;

    @ColumnInfo
    private int cantidad;

    @ColumnInfo(name = "precio_unitario")
    private double precioUnitario;

    @ColumnInfo
    private double subtotal;

    public DetalleVenta() {
    }

    @Ignore
    public DetalleVenta(int id, int idVenta, int idProducto, int cantidad, double precioUnitario, double subtotal) {
        this.id = id;
        this.idVenta = idVenta;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }

    @Ignore
    public DetalleVenta(int idVenta, int idProducto, int cantidad, double precioUnitario, double subtotal) {
        this.idVenta = idVenta;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
}