package com.example.zapateria_app.Models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import com.example.zapateria_app.Models.Cliente;
import com.example.zapateria_app.Models.Empleado;
import com.example.zapateria_app.Models.Producto;

import java.util.Date;

@Entity(tableName = "compras",
        foreignKeys = {
                @ForeignKey(entity = Cliente.class,
                        parentColumns = "id",
                        childColumns = "clienteId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Empleado.class,
                        parentColumns = "id",
                        childColumns = "empleadoId",
                        onDelete = ForeignKey.SET_NULL),
                @ForeignKey(entity = Producto.class,
                        parentColumns = "id",
                        childColumns = "productoId",
                        onDelete = ForeignKey.SET_NULL)
        },
        indices = {
                @Index(value = "clienteId"),
                @Index(value = "empleadoId"),
                @Index(value = "productoId"),
                @Index(value = "fechaCompra")
        })
public class Compra implements java.io.Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private int clienteId;
    private Integer empleadoId;
    private Integer productoId;

    private String fechaCompra;
    private double total;
    private String metodoPago;
    private String estado;


    private String productos;
    private String cantidades;

    public Compra() {
        this.fechaCompra = "hoy";
        this.estado = "Pendiente";
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClienteId() {
        return clienteId;
    }

    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }

    public Integer getEmpleadoId() {
        return empleadoId;
    }

    public void setEmpleadoId(Integer empleadoId) {
        this.empleadoId = empleadoId;
    }

    public Integer getProductoId() {
        return productoId;
    }

    public void setProductoId(Integer productoId) {
        this.productoId = productoId;
    }

    public String getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(String fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getProductos() {
        return productos;
    }

    public void setProductos(String productos) {
        this.productos = productos;
    }

    public String getCantidades() {
        return cantidades;
    }

    public void setCantidades(String cantidades) {
        this.cantidades = cantidades;
    }
}